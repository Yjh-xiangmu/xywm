package com.xywm.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private Long merchantId;
    private Long addressId;
    private String remark;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long dishId;
        private Integer quantity;
    }
}