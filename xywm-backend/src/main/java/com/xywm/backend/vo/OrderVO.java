package com.xywm.backend.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long merchantId;
    private String merchantName;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private Integer status;
    private String statusText;
    private String remark;
    private String addressSnapshot;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;

    @Data
    public static class OrderItemVO {
        private Long dishId;
        private String dishName;
        private String dishImage;
        private java.math.BigDecimal price;
        private Integer quantity;
    }
}