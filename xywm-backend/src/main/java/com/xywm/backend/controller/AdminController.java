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

    // 获取所有商家列表（包含待审核、正常营业、已封禁）
    @GetMapping("/merchant/list")
    public Result<List<User>> getMerchantList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 角色1代表商家，按创建时间倒序排列，新注册的在最前面
        wrapper.eq(User::getRole, 1).orderByDesc(User::getCreateTime);
        return Result.success(userService.list(wrapper));
    }

    // 处理商家状态变更（审核通过、驳回、封禁、解封共用这个接口）
    @PostMapping("/merchant/status")
    public Result<Void> updateMerchantStatus(@RequestBody AuditDTO auditDTO) {
        User user = userService.getById(auditDTO.getUserId());
        if (user == null || user.getRole() != 1) {
            throw new BusinessException("商家不存在");
        }

        // 直接更新为目标状态 (0: 封禁/驳回, 1: 正常/通过)
        user.setStatus(auditDTO.getStatus());
        userService.updateById(user);
        return Result.success();
    }
}