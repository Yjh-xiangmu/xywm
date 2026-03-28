package com.xywm.backend.dto;

import lombok.Data;

@Data
public class AuditDTO {
    private Long userId;
    private Integer status; // 1: 审核通过(正常), 0: 审核驳回(禁用)

    // 【新增】审核通过时，管理员分配的平台分类ID
    private Long categoryId;
}