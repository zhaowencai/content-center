package com.neo.contentcenter.controller;

import com.neo.contentcenter.domain.dto.content.ShareDTO;
import com.neo.contentcenter.service.share.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaoWenCai
 * @date 2020/5/22 09:15
 * @since 1.0.0
 */
@RestController
@RequestMapping("/shares")
public class ShareController {
    @Autowired
    private ShareService shareService;

    @RequestMapping("/{id}")
    public ShareDTO findById(@PathVariable Integer id) {
        return shareService.findById(id);
    }

}
