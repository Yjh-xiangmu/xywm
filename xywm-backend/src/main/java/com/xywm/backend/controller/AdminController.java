package com.xywm.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.common.Result;
import com.xywm.backend.dto.AuditDTO;
import com.xywm.backend.entity.User;
import com.xywm.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

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

        // 【新增逻辑】如果是审核通过(1)，并且传了分类ID，就给商家绑定该平台分类
        if (auditDTO.getStatus() == 1 && auditDTO.getCategoryId() != null) {
            user.setCategoryId(auditDTO.getCategoryId());
        }

        userService.updateById(user);
        return Result.success();
    }
}