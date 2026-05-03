package com.xywm.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.common.Result;
import com.xywm.backend.dto.AuditDTO;
import com.xywm.backend.entity.Category;
import com.xywm.backend.entity.Orders;
import com.xywm.backend.entity.User;
import com.xywm.backend.service.CategoryService;
import com.xywm.backend.service.OrdersService;
import com.xywm.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CategoryService categoryService;

    // ===== 商家管理 =====

    @GetMapping("/merchant/list")
    public Result<List<User>> getMerchantList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, 1).orderByDesc(User::getCreateTime);
        return Result.success(userService.list(wrapper));
    }

    @PostMapping("/merchant/status")
    public Result<Void> updateMerchantStatus(@RequestBody AuditDTO auditDTO) {
        User user = userService.getById(auditDTO.getUserId());
        if (user == null || user.getRole() != 1) {
            throw new BusinessException("商家不存在");
        }
        user.setStatus(auditDTO.getStatus());
        if (auditDTO.getStatus() == 1 && auditDTO.getCategoryId() != null) {
            user.setCategoryId(auditDTO.getCategoryId());
        }
        userService.updateById(user);
        return Result.success();
    }

    // ===== 用户管理 =====

    @GetMapping("/user/list")
    public Result<List<User>> getUserList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(User::getRole).orderByDesc(User::getCreateTime);
        List<User> list = userService.list(wrapper);
        list.forEach(u -> u.setPassword(null));
        return Result.success(list);
    }

    @PostMapping("/user/status")
    public Result<Void> updateUserStatus(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (user.getRole() == 0) throw new BusinessException("不能操作管理员账号");
        user.setStatus(status);
        userService.updateById(user);
        return Result.success();
    }

    // ===== 平台数据统计 =====

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        List<User> allUsers = userService.list();
        long merchantCount  = allUsers.stream().filter(u -> u.getRole() == 1).count();
        long activeMerchant = allUsers.stream().filter(u -> u.getRole() == 1 && u.getStatus() == 1).count();
        long studentCount   = allUsers.stream().filter(u -> u.getRole() == 2).count();

        List<User> pendingMerchants = allUsers.stream()
                .filter(u -> u.getRole() == 1 && u.getStatus() == 2)
                .peek(u -> u.setPassword(null))
                .collect(Collectors.toList());

        List<Orders> allOrders = ordersService.list(
                new LambdaQueryWrapper<Orders>().ne(Orders::getStatus, 5)
        );

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayOrders = allOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(todayStart))
                .count();

        BigDecimal totalSales = allOrders.stream()
                .filter(o -> o.getStatus() == 4)
                .map(Orders::getActualAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        List<String> days = new ArrayList<>();
        List<Long> dailyOrders = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime end   = start.plusDays(1);
            days.add(LocalDate.now().minusDays(i).format(fmt));
            dailyOrders.add(allOrders.stream()
                    .filter(o -> o.getCreateTime() != null
                            && o.getCreateTime().isAfter(start)
                            && o.getCreateTime().isBefore(end))
                    .count());
        }

        List<Category> categories = categoryService.getPlatformCategories();
        Map<String, Long> categoryDist = new LinkedHashMap<>();
        for (Category cat : categories) {
            long count = allUsers.stream()
                    .filter(u -> u.getRole() == 1 && cat.getId().equals(u.getCategoryId()))
                    .count();
            categoryDist.put(cat.getName(), count);
        }
        long uncategorized = allUsers.stream()
                .filter(u -> u.getRole() == 1 && u.getCategoryId() == null)
                .count();
        if (uncategorized > 0) categoryDist.put("未分类", uncategorized);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("merchantCount",    merchantCount);
        result.put("activeMerchant",   activeMerchant);
        result.put("studentCount",     studentCount);
        result.put("totalOrders",      allOrders.size());
        result.put("todayOrders",      todayOrders);
        result.put("totalSales",       totalSales);
        result.put("days",             days);
        result.put("dailyOrders",      dailyOrders);
        result.put("categoryDist",     categoryDist);
        result.put("pendingMerchants", pendingMerchants);

        return Result.success(result);
    }

    @GetMapping("/order/list")
    public Result<?> getAllOrders() {
        return Result.success(ordersService.getAllOrders());
    }
}