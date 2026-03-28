package com.xywm.backend.dto;

import lombok.Data;

@Data
public class AuditDTO {
    private Long userId;
    private Integer status; // 1: 审核通过(正常), 0: 审核驳回(禁用)
}