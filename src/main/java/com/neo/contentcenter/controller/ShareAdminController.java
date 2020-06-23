package com.neo.contentcenter.controller;

import com.neo.contentcenter.domain.dto.content.ShareAuditDTO;
import com.neo.contentcenter.domain.entity.content.Share;
import com.neo.contentcenter.service.share.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaoWenCai
 * @date 2020/5/31 10:26
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin/shares")
public class ShareAdminController {
    @Autowired
    private ShareService shareService;

    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO auditDTO) {
        // TODO 认证、授权
        return this.shareService.auditById(id, auditDTO);
    }
}
