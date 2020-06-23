package com.neo.contentcenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhaoyun
 * @date: 2020/5/21 22:37
 * @version: 1.0。0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    private String vxId;

    private String vxNickname;

    private String roles;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer bonus;

    private String avatarUrl;
}
