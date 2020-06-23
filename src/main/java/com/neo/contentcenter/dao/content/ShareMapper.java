package com.neo.contentcenter.dao.content;

import com.neo.contentcenter.domain.entity.content.Share;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface ShareMapper extends Mapper<Share> {
}