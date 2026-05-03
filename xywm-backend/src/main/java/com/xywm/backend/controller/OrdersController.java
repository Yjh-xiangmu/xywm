package com.xywm.backend.controller;

import com.xywm.backend.common.Result;
import com.xywm.backend.dto.OrderDTO;
import com.xywm.backend.service.OrdersService;
import com.xywm.backend.utils.UserContext;
import com.xywm.backend.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    // 用户下单
    @PostMapping
    public Result<String> createOrder(@RequestBody OrderDTO dto) {
        String orderNo = ordersService.createOrder(UserContext.getUserId(), dto);
        return Result.success(orderNo);
    }

    // 用户查自己的订单
    @GetMapping("/user")
    public Result<List<OrderVO>> getUserOrders() {
        return Result.success(ordersService.getUserOrders(UserContext.getUserId()));
    }

    // 商家查自己的订单
    @GetMapping("/merchant")
    public Result<List<OrderVO>> getMerchantOrders() {
        return Result.success(ordersService.getMerchantOrders(UserContext.getUserId()));
    }

    // 更新订单状态（商家接单/拒单、用户取消）
    @PutMapping("/{orderId}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long orderId, @PathVariable Integer status) {
        ordersService.updateOrderStatus(orderId, status);
        return Result.success();
    }
}