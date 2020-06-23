package com.neo.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author zhaoWenCai
 * @date 2020/5/26 11:13
 * @since 1.0.0
 */
public class GlobalFeignClientConfiguration {
    @Bean
    public Logger.Level level() {
        return Logger.Level.BASIC;
    }
}
