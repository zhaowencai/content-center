package com.neo.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhaoWenCai
 * @date 2020/5/30 19:54
 * @since 1.0.0
 */
@Component
@Slf4j
public class MyUrlBlockHandler implements UrlCleaner {
    @Override
    public String clean(String originUrl) {
        log.info("originUrl = {}", originUrl);
        return originUrl;
    }
}
