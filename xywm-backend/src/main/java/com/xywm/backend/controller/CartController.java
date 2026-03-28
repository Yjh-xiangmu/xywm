package com.xywm.backend.controller;

import com.xywm.backend.common.Result;
import com.xywm.backend.service.CartService;
import com.xywm.backend.utils.UserContext;
import com.xywm.backend.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Result<List<CartVO>> getCart() {
        return Result.success(cartService.getCartList(UserContext.getUserId()));
    }

    @PostMapping("/add")
    public Result<Void> addToCart(@RequestBody Map<String, Object> params) {
        Long dishId = Long.valueOf(params.get("dishId").toString());
        Integer quantity = Integer.valueOf(params.getOrDefault("quantity", 1).toString());
        cartService.addToCart(UserContext.getUserId(), dishId, quantity);
        return Result.success();
    }

    @PutMapping("/{cartId}/quantity/{quantity}")
    public Result<Void> updateQuantity(@PathVariable Long cartId, @PathVariable Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return Result.success();
    }

    @DeleteMapping("/{cartId}")
    public Result<Void> removeFromCart(@PathVariable Long cartId) {
        cartService.removeFromCart(cartId);
        return Result.success();
    }
}