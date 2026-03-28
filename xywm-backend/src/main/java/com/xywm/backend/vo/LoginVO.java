package com.xywm.backend.vo;

import com.xywm.backend.entity.User;
import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private User userInfo;
}