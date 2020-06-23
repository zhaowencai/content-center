package com.neo.contentcenter.feignclient;

import com.neo.contentcenter.domain.dto.user.UserDTO;

/**
 * @author zhaoWenCai
 * @date 2020/5/29 11:34
 * @since 1.0.0
 */
public class UserCenterFeignClientFallback implements UserCenterFeignClient{
    @Override
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setVxNickname("一个默认用户");
        return userDTO;
    }
}
