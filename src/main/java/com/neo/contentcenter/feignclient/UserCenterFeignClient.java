package com.neo.contentcenter.feignclient;

import com.neo.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhaoWenCai
 * @date 2020/5/25 17:54
 * @since 1.0.0
 */
@FeignClient(name = "user-center", fallbackFactory = UserCenterFeignClientFallbackFactory.class)
public interface UserCenterFeignClient {
    /**
     * 调用用户中心服务
     *
     * @param id 用户 id
     * @return 用户信息
     */
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);
}
