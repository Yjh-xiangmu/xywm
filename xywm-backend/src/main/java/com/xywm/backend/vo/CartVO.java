package com.xywm.backend.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartVO {
    private Long id;
    private Long dishId;
    private String dishName;
    private String dishImage;
    private BigDecimal price;
    private Integer quantity;
    private Long merchantId;
    private String merchantName;
    private BigDecimal subtotal;
}