package com.neo.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhaoWenCai
 * @date 2020/5/26 16:56
 * @since 1.0.0
 */
@FeignClient(name = "baidu", url = "http://www.baidu.com")
public interface TestBaiduFeignClient {
    @GetMapping("")
    String index();
}
