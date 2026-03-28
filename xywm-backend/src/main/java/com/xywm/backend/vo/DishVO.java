package com.xywm.backend.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishVO {
    private Long id;
    private String name;
    private Long merchantId;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private String image;
    private String description;
    private Integer status;
    private Integer stock;
}