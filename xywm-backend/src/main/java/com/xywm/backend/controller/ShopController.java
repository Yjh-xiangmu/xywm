package com.xywm.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.common.Result;
import com.xywm.backend.entity.User;
import com.xywm.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private UserService userService;

    // 获取所有正常营业的商家列表
    @GetMapping("/list")
    public Result<List<User>> getShopList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, 1)
                .eq(User::getStatus, 1);

        List<User> shopList = userService.list(wrapper);

        for (User shop : shopList) {
            shop.setPassword(null);
        }

        return Result.success(shopList);
    }

    // 获取单个商家详情
    @GetMapping("/{merchantId}")
    public Result<User> getShopDetail(@PathVariable Long merchantId) {
        User user = userService.getById(merchantId);
        if (user == null || user.getRole() != 1) {
            throw new BusinessException("商家不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }
}