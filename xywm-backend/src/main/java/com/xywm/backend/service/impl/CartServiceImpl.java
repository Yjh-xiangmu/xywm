package com.xywm.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.entity.Cart;
import com.xywm.backend.entity.Dish;
import com.xywm.backend.entity.User;
import com.xywm.backend.mapper.CartMapper;
import com.xywm.backend.service.CartService;
import com.xywm.backend.service.DishService;
import com.xywm.backend.service.UserService;
import com.xywm.backend.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private DishService dishService;
    @Autowired
    private UserService userService;

    @Override
    public List<CartVO> getCartList(Long userId) {
        List<Cart> carts = list(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
        return carts.stream().map(cart -> {
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setDishId(cart.getDishId());
            vo.setQuantity(cart.getQuantity());
            vo.setMerchantId(cart.getMerchantId());
            Dish dish = dishService.getById(cart.getDishId());
            if (dish != null) {
                vo.setDishName(dish.getName());
                vo.setDishImage(dish.getImage());
                vo.setPrice(dish.getPrice());
                vo.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            }
            User merchant = userService.getById(cart.getMerchantId());
            if (merchant != null) vo.setMerchantName(merchant.getNickname());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void addToCart(Long userId, Long dishId, Integer quantity) {
        Dish dish = dishService.getById(dishId);
        if (dish == null) throw new BusinessException("菜品不存在");
        Cart existing = getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getDishId, dishId));
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            updateById(existing);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setDishId(dishId);
            cart.setMerchantId(dish.getMerchantId());
            cart.setQuantity(quantity);
            save(cart);
        }
    }

    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        if (quantity <= 0) {
            removeById(cartId);
            return;
        }
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setQuantity(quantity);
        updateById(cart);
    }

    @Override
    public void removeFromCart(Long cartId) {
        removeById(cartId);
    }

    @Override
    public void clearCart(Long userId, Long merchantId) {
        remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getMerchantId, merchantId));
    }
}