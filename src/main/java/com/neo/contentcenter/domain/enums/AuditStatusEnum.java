package com.neo.contentcenter.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 管理员审核状态
 *
 * @author zhaoWenCai
 * @date 2020/5/31 11:10
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {
    /**
     * 待审核
     */
    NOT_YET,
    /**
     * 审核通过
     */
    PASS,
    /**
     * 审核不通过
     */
    REJECT
}
