package com.xywm.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private String name;
    private Integer type;
    private BigDecimal minAmount;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private Integer total;
    private Integer received;
    private Integer status;
    private Integer expireDays;
    private Integer canRepeat;  // 0只能领一次 1用完可再领
    private LocalDateTime createTime;
}