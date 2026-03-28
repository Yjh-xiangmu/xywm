package com.xywm.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long merchantId;
    private Long dishId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}