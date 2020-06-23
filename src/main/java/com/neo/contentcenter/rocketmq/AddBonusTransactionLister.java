package com.neo.contentcenter.rocketmq;

import com.alibaba.fastjson.JSON;
import com.neo.contentcenter.dao.rocketMQ.RocketmqTransactionLogMapper;
import com.neo.contentcenter.domain.dto.content.ShareAuditDTO;
import com.neo.contentcenter.domain.entity.rocketMQ.RocketmqTransactionLog;
import com.neo.contentcenter.service.share.ShareService;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

/**
 * @author zhaoWenCai
 * @date 2020/6/9
 * @since 1.0.0
 */
@Service
@RocketMQTransactionListener(txProducerGroup = "tx-add-bonus-group")
public class AddBonusTransactionLister implements RocketMQLocalTransactionListener {
    @Autowired
    private ShareService shareService;
    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        MessageHeaders headers = message.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Integer shareId = Integer.valueOf((String)headers.get("share_id"));
        //有个小坑
        String dtoString = (String) headers.get("dto");
        ShareAuditDTO auditDTO = JSON.parseObject(dtoString, ShareAuditDTO.class);
        try {
            //根据本地事务状态二次确认提交消息
            shareService.auditByIdWithRocketMqLog(shareId, auditDTO, transactionId);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        MessageHeaders headers = message.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        RocketmqTransactionLog transactionLog = this.rocketmqTransactionLogMapper.selectOne(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .build()
        );
        if (transactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
