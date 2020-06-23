package com.neo.contentcenter.rocketmq;

import com.neo.contentcenter.domain.entity.content.Share;
import com.neo.contentcenter.service.share.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

/**
 * @author zhaoWenCai
 * @date 2020/6/13
 * @since 1.0.0
 */
@Slf4j
@RocketMQTransactionListener(txProducerGroup = "tx-test-group")
public class TestMessageTransactionLister implements RocketMQLocalTransactionListener {
    @Autowired
    private ShareService shareService;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        Share share = (Share) o;
        try {
            //执行本地事务
            shareService.insert(share);
            log.info("本地事务执行完成，消息可以被消费了。。");
            //本地事务正常执行，则二次确认消息提交，可被消费
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        return null;
    }
}
