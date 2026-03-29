package com.xywm.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xywm.backend.common.Result;
import com.xywm.backend.entity.User;
import com.xywm.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> getShopList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 查找角色为 1（商家）且状态为 1（审核通过，正常营业）的用户
        wrapper.eq(User::getRole, 1)
                .eq(User::getStatus, 1);

        List<User> shopList = userService.list(wrapper);

        // 返回给前端前，将密码字段置空，保障数据安全
        for (User shop : shopList) {
            shop.setPassword(null);
        }

        return Result.success(shopList);
    }
}