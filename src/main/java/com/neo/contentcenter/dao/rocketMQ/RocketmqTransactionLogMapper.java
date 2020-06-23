package com.neo.contentcenter.dao.rocketMQ;

import com.neo.contentcenter.domain.entity.rocketMQ.RocketmqTransactionLog;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author zhaoyun
 */
@Repository
public interface RocketmqTransactionLogMapper extends Mapper<RocketmqTransactionLog> {
}