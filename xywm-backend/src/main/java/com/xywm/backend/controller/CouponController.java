package com.xywm.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.common.Result;
import com.xywm.backend.entity.Coupon;
import com.xywm.backend.entity.User;
import com.xywm.backend.entity.UserCoupon;
import com.xywm.backend.mapper.CouponMapper;
import com.xywm.backend.mapper.UserCouponMapper;
import com.xywm.backend.service.UserService;
import com.xywm.backend.utils.UserContext;
import com.xywm.backend.vo.UserCouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired private CouponMapper couponMapper;
    @Autowired private UserCouponMapper userCouponMapper;
    @Autowired private UserService userService;

    // ===== 商家端 =====

    @GetMapping("/merchant/list")
    public Result<List<Coupon>> getMerchantCoupons() {
        Long merchantId = UserContext.getUserId();
        List<Coupon> list = couponMapper.selectList(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getMerchantId, merchantId)
                        .orderByDesc(Coupon::getCreateTime));
        return Result.success(list);
    }

    @PostMapping("/merchant")
    public Result<Void> createCoupon(@RequestBody Coupon coupon) {
        coupon.setMerchantId(UserContext.getUserId());
        coupon.setReceived(0);
        coupon.setStatus(1);
        if (coupon.getCanRepeat() == null) coupon.setCanRepeat(0);
        couponMapper.insert(coupon);
        return Result.success();
    }

    @PutMapping("/merchant")
    public Result<Void> updateCoupon(@RequestBody Coupon coupon) {
        couponMapper.updateById(coupon);
        return Result.success();
    }

    @DeleteMapping("/merchant/{id}")
    public Result<Void> deleteCoupon(@PathVariable Long id) {
        couponMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping("/merchant/{id}/status/{status}")
    public Result<Void> toggleStatus(@PathVariable Long id, @PathVariable Integer status) {
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setStatus(status);
        couponMapper.updateById(coupon);
        return Result.success();
    }

    // ===== 用户端 =====

    // 获取某商家的优惠券列表（附带当前用户的领取状态）
    @GetMapping("/shop/{merchantId}")
    public Result<List<java.util.Map<String, Object>>> getShopCoupons(@PathVariable Long merchantId) {
        Long userId = UserContext.getUserId();
        List<Coupon> list = couponMapper.selectList(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getMerchantId, merchantId)
                        .eq(Coupon::getStatus, 1)
                        .orderByDesc(Coupon::getCreateTime));

        List<java.util.Map<String, Object>> result = list.stream().map(coupon -> {
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", coupon.getId());
            map.put("name", coupon.getName());
            map.put("type", coupon.getType());
            map.put("minAmount", coupon.getMinAmount());
            map.put("discountAmount", coupon.getDiscountAmount());
            map.put("discountRate", coupon.getDiscountRate());
            map.put("total", coupon.getTotal());
            map.put("received", coupon.getReceived());
            map.put("expireDays", coupon.getExpireDays());
            map.put("canRepeat", coupon.getCanRepeat());

            // 判断当前用户能否领取
            String receiveStatus = canReceive(userId, coupon);
            map.put("receiveStatus", receiveStatus);
            // received=可领取 holding=持有未使用 used=已用尽/不可再领 full=已抢完
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    // 领取优惠券
    @PostMapping("/receive/{couponId}")
    public Result<Void> receiveCoupon(@PathVariable Long couponId) {
        Long userId = UserContext.getUserId();

        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1)
            throw new BusinessException("优惠券不存在或已停用");
        if (coupon.getReceived() >= coupon.getTotal())
            throw new BusinessException("优惠券已被领完");

        String status = canReceive(userId, coupon);
        switch (status) {
            case "holding"  -> throw new BusinessException("您还有未使用的该券，用完再来领哦");
            case "used"     -> throw new BusinessException("该券只能领一次，已无法再领");
            case "full"     -> throw new BusinessException("优惠券已被领完");
        }

        // 创建用户优惠券记录
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setMerchantId(coupon.getMerchantId());
        uc.setStatus(0);
        uc.setReceiveTime(LocalDateTime.now());
        if (coupon.getExpireDays() != null && coupon.getExpireDays() > 0) {
            uc.setExpireTime(LocalDateTime.now().plusDays(coupon.getExpireDays()));
        }
        userCouponMapper.insert(uc);

        coupon.setReceived(coupon.getReceived() + 1);
        couponMapper.updateById(coupon);

        return Result.success();
    }

    // 我的优惠券
    @GetMapping("/my")
    public Result<List<UserCouponVO>> getMyCoupons() {
        Long userId = UserContext.getUserId();
        List<UserCoupon> list = userCouponMapper.selectList(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .orderByDesc(UserCoupon::getReceiveTime));
        return Result.success(list.stream().map(this::toVO).collect(Collectors.toList()));
    }

    // 结算页：获取当前用户可用于某商家的优惠券
    @GetMapping("/usable/{merchantId}")
    public Result<List<UserCouponVO>> getUsableCoupons(@PathVariable Long merchantId) {
        Long userId = UserContext.getUserId();
        List<UserCoupon> list = userCouponMapper.selectList(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getMerchantId, merchantId)
                        .eq(UserCoupon::getStatus, 0));
        List<UserCouponVO> result = list.stream()
                .filter(uc -> uc.getExpireTime() == null
                        || LocalDateTime.now().isBefore(uc.getExpireTime()))
                .map(this::toVO)
                .collect(Collectors.toList());
        return Result.success(result);
    }

    // ===== 内部方法 =====

    /**
     * 判断用户能否领取某券
     * @return "available"=可领 "holding"=持有未用 "used"=已领且不可再领 "full"=已抢完
     */
    private String canReceive(Long userId, Coupon coupon) {
        if (coupon.getReceived() >= coupon.getTotal()) return "full";

        // 查该用户持有的未使用的该券
        long holdingCount = userCouponMapper.selectCount(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getCouponId, coupon.getId())
                        .eq(UserCoupon::getStatus, 0));

        if (holdingCount > 0) return "holding"; // 手上还有，不能再领

        // canRepeat=0：只能领一次，用过就不能再领
        if (coupon.getCanRepeat() == null || coupon.getCanRepeat() == 0) {
            long totalReceived = userCouponMapper.selectCount(
                    new LambdaQueryWrapper<UserCoupon>()
                            .eq(UserCoupon::getUserId, userId)
                            .eq(UserCoupon::getCouponId, coupon.getId()));
            if (totalReceived > 0) return "used";
        }
        // canRepeat=1：用完可再领，走到这里说明没持有，可以领

        return "available";
    }

    private UserCouponVO toVO(UserCoupon uc) {
        UserCouponVO vo = new UserCouponVO();
        vo.setId(uc.getId());
        vo.setCouponId(uc.getCouponId());
        vo.setMerchantId(uc.getMerchantId());
        vo.setStatus(uc.getStatus());
        vo.setExpireTime(uc.getExpireTime());

        boolean expired = uc.getExpireTime() != null
                && LocalDateTime.now().isAfter(uc.getExpireTime())
                && uc.getStatus() == 0;

        String[] statusTexts = {"未使用", "已使用", "已过期"};
        vo.setStatusText(expired ? "已过期" : statusTexts[Math.min(uc.getStatus(), 2)]);

        Coupon coupon = couponMapper.selectById(uc.getCouponId());
        if (coupon != null) {
            vo.setName(coupon.getName());
            vo.setType(coupon.getType());
            vo.setMinAmount(coupon.getMinAmount());
            vo.setDiscountAmount(coupon.getDiscountAmount());
            vo.setDiscountRate(coupon.getDiscountRate());
            vo.setTypeText(coupon.getType() == 1 ? "满减券" : "折扣券");
            if (coupon.getType() == 1) {
                vo.setDescText("满" + coupon.getMinAmount().toPlainString()
                        + "减" + coupon.getDiscountAmount().toPlainString());
            } else {
                vo.setDescText("满" + coupon.getMinAmount().toPlainString()
                        + "享" + coupon.getDiscountRate() + "折");
            }
        }

        User merchant = userService.getById(uc.getMerchantId());
        if (merchant != null) vo.setMerchantName(merchant.getNickname());

        return vo;
    }
}