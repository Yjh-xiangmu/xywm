package com.xywm.backend.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.dto.OrderDTO;
import com.xywm.backend.entity.*;
import com.xywm.backend.mapper.CouponMapper;
import com.xywm.backend.mapper.OrderItemMapper;
import com.xywm.backend.mapper.OrdersMapper;
import com.xywm.backend.mapper.UserCouponMapper;
import com.xywm.backend.service.*;
import com.xywm.backend.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private DishService dishService;
    @Autowired private CartService cartService;
    @Autowired private UserService userService;
    @Autowired private AddressService addressService;
    @Autowired private CouponMapper couponMapper;
    @Autowired private UserCouponMapper userCouponMapper;

    @Override
    @Transactional
    public String createOrder(Long userId, OrderDTO dto) {
        // 计算总价
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDTO.OrderItemDTO item : dto.getItems()) {
            Dish dish = dishService.getById(item.getDishId());
            if (dish == null || dish.getStatus() == 0)
                throw new BusinessException("菜品【" + (dish == null ? item.getDishId() : dish.getName()) + "】已下架");
            total = total.add(dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 地址快照
        Address address = addressService.getById(dto.getAddressId());
        String addressSnapshot = address != null ?
                address.getName() + " " + address.getPhone() + " " + address.getAddress() : "";

        // 优惠券抵扣
        BigDecimal actualAmount = total;
        Long appliedUserCouponId = null;

        if (dto.getUserCouponId() != null) {
            UserCoupon uc = userCouponMapper.selectById(dto.getUserCouponId());
            if (uc != null && uc.getStatus() == 0
                    && uc.getUserId().equals(userId)
                    && uc.getMerchantId().equals(dto.getMerchantId())) {

                boolean notExpired = uc.getExpireTime() == null
                        || LocalDateTime.now().isBefore(uc.getExpireTime());

                if (notExpired) {
                    Coupon coupon = couponMapper.selectById(uc.getCouponId());
                    if (coupon != null && total.compareTo(coupon.getMinAmount()) >= 0) {
                        if (coupon.getType() == 1) {
                            // 满减
                            actualAmount = total.subtract(coupon.getDiscountAmount());
                        } else {
                            // 折扣
                            actualAmount = total.multiply(coupon.getDiscountRate())
                                    .divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);
                        }
                        if (actualAmount.compareTo(BigDecimal.ZERO) < 0)
                            actualAmount = BigDecimal.ZERO;
                        appliedUserCouponId = uc.getId();
                    }
                }
            }
        }

        // 创建订单
        Orders order = new Orders();
        order.setOrderNo(IdUtil.fastSimpleUUID());
        order.setUserId(userId);
        order.setMerchantId(dto.getMerchantId());
        order.setAddressId(dto.getAddressId());
        order.setAddressSnapshot(addressSnapshot);
        order.setTotalAmount(total);
        order.setActualAmount(actualAmount);
        order.setStatus(1);
        order.setRemark(dto.getRemark());
        save(order);

        // 保存订单明细
        for (OrderDTO.OrderItemDTO item : dto.getItems()) {
            Dish dish = dishService.getById(item.getDishId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(item.getDishId());
            orderItem.setDishName(dish.getName());
            orderItem.setDishImage(dish.getImage());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemMapper.insert(orderItem);
        }

        // 标记优惠券已使用
        if (appliedUserCouponId != null) {
            UserCoupon uc = new UserCoupon();
            uc.setId(appliedUserCouponId);
            uc.setStatus(1);
            uc.setUseTime(LocalDateTime.now());
            uc.setOrderId(order.getId());
            userCouponMapper.updateById(uc);
        }

        // 清空购物车
        cartService.clearCart(userId, dto.getMerchantId());
        return order.getOrderNo();
    }

    @Override
    public List<OrderVO> getUserOrders(Long userId) {
        List<Orders> orders = list(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime));
        return orders.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderVO> getMerchantOrders(Long merchantId) {
        List<Orders> orders = list(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getMerchantId, merchantId)
                .orderByDesc(Orders::getCreateTime));
        return orders.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderVO> getAllOrders() {
        List<Orders> orders = list(new LambdaQueryWrapper<Orders>()
                .orderByDesc(Orders::getCreateTime));
        return orders.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setStatus(status);
        updateById(order);
    }

    private OrderVO toVO(Orders order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        String[] statusTexts = {"待付款", "待接单", "已接单", "配送中", "已完成", "已取消"};
        vo.setStatusText(statusTexts[Math.min(order.getStatus(), 5)]);
        User merchant = userService.getById(order.getMerchantId());
        if (merchant != null) vo.setMerchantName(merchant.getNickname());
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        vo.setItems(items.stream().map(item -> {
            OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList()));
        return vo;
    }
}