package com.neo.contentcenter.domain.entity.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "share")
public class Share {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 用户 ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    @Column(name = "is_original")
    private Byte isOriginal;

    /**
     * 作者
     */
    private String author;

    private String cover;

    private String summary;

    private BigDecimal price;

    @Column(name = "download_url")
    private String downloadUrl;

    /**
     * 购买人数
     */
    @Column(name = "buy_count")
    private Integer buyCount;

    @Column(name = "show_flag")
    private Boolean showFlag;

    @Column(name = "audit_status")
    private String auditStatus;

    private String reason;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;
}