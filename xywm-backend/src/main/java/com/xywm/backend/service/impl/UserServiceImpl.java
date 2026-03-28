package com.xywm.backend.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.dto.LoginDTO;
import com.xywm.backend.dto.RegisterDTO;
import com.xywm.backend.entity.User;
import com.xywm.backend.mapper.UserMapper;
import com.xywm.backend.service.UserService;
import com.xywm.backend.utils.JwtUtils;
import com.xywm.backend.vo.LoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查找对应角色和用户名的账号
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getRole, loginDTO.getRole());
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException("账号不存在或角色选择错误");
        }

        // 校验 MD5 密码
        String md5Password = DigestUtil.md5Hex(loginDTO.getPassword());
        if (!md5Password.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被封禁，请联系管理员");
        }

        // 生成 JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        String token = jwtUtils.generateToken(claims);

        // 返回给前端前抹除密码信息
        user.setPassword(null);
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(user);

        return loginVO;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已被注册");
        }

        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(DigestUtil.md5Hex(registerDTO.getPassword()));

        // 区分角色设置状态
        if (registerDTO.getRole() == 1) { // 商家
            if (registerDTO.getLicenseUrl() == null || registerDTO.getLicenseUrl().isEmpty()) {
                throw new BusinessException("商家入驻必须上传营业执照");
            }
            user.setStatus(2); // 2代表待审核
        } else { // 学生
            user.setStatus(1); // 1代表正常
        }

        this.save(user);
    }
}