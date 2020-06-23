package com.neo.contentcenter.feignclient;

import com.neo.contentcenter.domain.dto.user.UserDTO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * sentinel 配置的限流、降级被触发后，执行逻辑并打印异常
 *
 * @author zhaoWenCai
 * @date 2020/5/29 13:44
 * @since 1.0.0
 */
@Component
@Slf4j
public class UserCenterFeignClientFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable throwable) {
        return new UserCenterFeignClient() {
            @Override
            public UserDTO findById(Integer id) {
                log.warn("远程调用被限流、降级了", throwable);
                UserDTO userDTO = new UserDTO();
                userDTO.setVxNickname("一个默认用户");
                return userDTO;
            }
        };
    }
}
