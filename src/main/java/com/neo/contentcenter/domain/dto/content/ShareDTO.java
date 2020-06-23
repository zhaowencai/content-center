package com.neo.contentcenter.domain.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhaoyun
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareDTO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    private String vxNickname;

    private Byte isOriginal;

    /**
     * 作者
     */
    private String author;

    private String cover;

    private String summary;

    private BigDecimal price;

    private String downloadUrl;

    /**
     * 购买人数
     */
    private Integer buyCount;

    private Boolean showFlag;

    private String auditStatus;

    private String reason;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;
}