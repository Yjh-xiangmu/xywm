package com.xywm.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xywm.backend.dto.LoginDTO;
import com.xywm.backend.dto.RegisterDTO;
import com.xywm.backend.entity.User;
import com.xywm.backend.vo.LoginVO;

public interface UserService extends IService<User> {
    LoginVO login(LoginDTO loginDTO);
    void register(RegisterDTO registerDTO);
}