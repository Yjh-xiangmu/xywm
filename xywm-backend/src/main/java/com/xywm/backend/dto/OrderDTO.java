package com.xywm.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private Long merchantId;
    private Long addressId;
    private String remark;
    private List<OrderItemDTO> items;
    private Long userCouponId;  // 新增：使用的用户优惠券ID

    @Data
    public static class OrderItemDTO {
        private Long dishId;
        private Integer quantity;
    }
}