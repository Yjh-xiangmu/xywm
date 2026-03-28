package com.xywm.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xywm.backend.entity.Cart;
import com.xywm.backend.vo.CartVO;
import java.util.List;

public interface CartService extends IService<Cart> {
    List<CartVO> getCartList(Long userId);
    void addToCart(Long userId, Long dishId, Integer quantity);
    void updateQuantity(Long cartId, Integer quantity);
    void removeFromCart(Long cartId);
    void clearCart(Long userId, Long merchantId);
}