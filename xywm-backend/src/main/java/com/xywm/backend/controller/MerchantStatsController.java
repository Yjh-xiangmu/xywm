package com.xywm.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xywm.backend.common.Result;
import com.xywm.backend.entity.Orders;
import com.xywm.backend.service.OrdersService;
import com.xywm.backend.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/merchant/stats")
public class MerchantStatsController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping
    public Result<Map<String, Object>> getStats() {
        Long merchantId = UserContext.getUserId();

        // 查询该商家所有未删除、非取消的订单
        List<Orders> allOrders = ordersService.list(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getMerchantId, merchantId)
                        .ne(Orders::getStatus, 5) // 排除已取消
        );

        // 今日范围
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd   = todayStart.plusDays(1);

        List<Orders> todayOrders = allOrders.stream()
                .filter(o -> o.getCreateTime() != null
                        && o.getCreateTime().isAfter(todayStart)
                        && o.getCreateTime().isBefore(todayEnd))
                .collect(Collectors.toList());

        // 今日数据
        long   todayCount  = todayOrders.size();
        BigDecimal todaySales = todayOrders.stream()
                .map(Orders::getActualAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 待处理订单数（待接单）
        long pendingCount = allOrders.stream()
                .filter(o -> o.getStatus() == 1)
                .count();

        // 累计数据
        long   totalCount = allOrders.size();
        BigDecimal totalSales = allOrders.stream()
                .filter(o -> o.getStatus() == 4) // 只统计已完成的
                .map(Orders::getActualAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 近7天每日营业额（折线图数据）
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        List<String>     days       = new ArrayList<>();
        List<BigDecimal> dailySales = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime end   = start.plusDays(1);
            String label = LocalDate.now().minusDays(i).format(fmt);
            days.add(label);

            BigDecimal daySale = allOrders.stream()
                    .filter(o -> o.getCreateTime() != null
                            && o.getCreateTime().isAfter(start)
                            && o.getCreateTime().isBefore(end))
                    .map(Orders::getActualAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dailySales.add(daySale);
        }

        // 订单状态分布（饼图数据）
        Map<String, Long> statusDist = new LinkedHashMap<>();
        statusDist.put("待接单", allOrders.stream().filter(o -> o.getStatus() == 1).count());
        statusDist.put("已接单", allOrders.stream().filter(o -> o.getStatus() == 2).count());
        statusDist.put("配送中", allOrders.stream().filter(o -> o.getStatus() == 3).count());
        statusDist.put("已完成", allOrders.stream().filter(o -> o.getStatus() == 4).count());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayCount",   todayCount);
        result.put("todaySales",   todaySales);
        result.put("pendingCount", pendingCount);
        result.put("totalCount",   totalCount);
        result.put("totalSales",   totalSales);
        result.put("days",         days);
        result.put("dailySales",   dailySales);
        result.put("statusDist",   statusDist);

        return Result.success(result);
    }
}