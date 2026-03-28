package com.xywm.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer type;
    private Long parentId;
    private Long merchantId;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}