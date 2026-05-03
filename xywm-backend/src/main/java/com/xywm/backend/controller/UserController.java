package com.xywm.backend.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.common.Result;
import com.xywm.backend.entity.User;
import com.xywm.backend.service.UserService;
import com.xywm.backend.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 获取当前登录用户信息
    @GetMapping("/profile")
    public Result<User> getProfile() {
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(null);
        return Result.success(user);
    }

    // 更新个人信息
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        // 更新基本信息
        if (body.containsKey("nickname")) user.setNickname((String) body.get("nickname"));
        if (body.containsKey("phone"))    user.setPhone((String) body.get("phone"));
        if (body.containsKey("avatar"))   user.setAvatar((String) body.get("avatar"));

        // 修改密码（可选）
        if (body.containsKey("newPassword")) {
            String newPwd = (String) body.get("newPassword");
            if (newPwd != null && !newPwd.isEmpty()) {
                if (newPwd.length() < 6) throw new BusinessException("密码至少6位");
                user.setPassword(DigestUtil.md5Hex(newPwd));
            }
        }

        userService.updateById(user);
        return Result.success();
    }
}