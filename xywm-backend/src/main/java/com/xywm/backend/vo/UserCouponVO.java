package com.xywm.backend.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCouponVO {
    private Long id;           // user_coupon 主键
    private Long couponId;
    private Long merchantId;
    private String merchantName;
    private String name;
    private Integer type;      // 1满减 2折扣
    private BigDecimal minAmount;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private Integer status;    // 0未使用 1已使用 2已过期
    private LocalDateTime expireTime;
    private String statusText;
    private String typeText;
    private String descText;   // 展示文案，如"满30减5"
}