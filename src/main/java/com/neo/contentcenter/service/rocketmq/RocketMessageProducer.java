package com.neo.contentcenter.service.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaoWenCai
 * @date 2020/6/10
 * @since 1.0.0
 */
@Slf4j
@Service
public class RocketMessageProducer {
    @Autowired
    private RocketMQTemplate template;

    /**
     * 发送同步消息
     *
     * @param topic topic
     * @param msg   消息体
     */
    public void syncSend(String topic, Object msg) {
        template.syncSend(topic, msg);
    }

    /**
     * 发送异步消息
     *
     * @param topic topic
     * @param msg   消息体
     */
    public void asyncSend(String topic, Object msg) {
        template.asyncSend(topic, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功，message = {}, SendStatus = {}", msg, sendResult.getSendStatus());
            }

            @Override
            public void onException(Throwable e) {
                log.info("异步消息发送异常，exception = {}", e.getMessage());
            }
        });
    }

    /**
     * 发送单向消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void sendOneWay(String topic, Object message) {
        template.sendOneWay(topic, message);
    }

    /**
     * 发送带 tag 消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void sendWithTag(String topic, Object message) {
        this.template.syncSend(topic, MessageBuilder.withPayload(message)
                .setHeader("KEYS", "a-key")
                .build());
        log.info("已发送带 tag 的消息 message = {}", message);
    }

    /**
     * 发送事务消息
     *
     * @param topic   topic
     * @param payload 消息体
     */
    public void sendMessageInTransaction(String topic, Object payload) {
        //发送半消息，发送事务消息时，可以通过设置 header 传递数据，第四个参数也可以传递一个对象
        String uuid = UUID.randomUUID().toString();
        this.template.sendMessageInTransaction("tx-test-group", topic, MessageBuilder.withPayload(payload)
                        .setHeader("transactionalId", uuid)
                        .build(),
                payload);
    }

    public List<Message<String>> syncSendMessages(String topic) {
        List<Message<String>> messages = Stream.of("message-1", "message-2", "message-3")
                .map(msg -> MessageBuilder.withPayload(msg).build())
                .collect(Collectors.toList());
        this.template.syncSend(topic, messages, 5000);
        log.info("已发送批量数据 messages = {}", messages);
        return messages;
    }

    /**
     * 同步发送延时消息
     *
     * @param topic      topic
     * @param message    消息体
     * @param timeout    超时
     * @param delayLevel 延时等级：现在RocketMq并不支持任意时间的延时，需要设置几个固定的延时等级，
     *                   从1s到2h分别对应着等级 1 到 18，消息消费失败会进入延时消息队列
     *                   "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
     */
    public void syncSendDelay(String topic, Object message, long timeout, int delayLevel) {
        template.syncSend(topic, MessageBuilder.withPayload(message).build(), timeout, delayLevel);
        log.info("已同步发送延时消息 message = {}", message);
    }

    /**
     * 异步发送延时消息
     *
     * @param topic      topic
     * @param message    消息体
     * @param timeout    超时时间
     * @param delayLevel 延时等级
     */
    public void asyncSendDelay(String topic, Object message, long timeout, int delayLevel) {
        template.asyncSend(topic, MessageBuilder.withPayload(message).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送延时消息成功，message ={}", message);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("异步发送延时消息发生异常，exception = {}", throwable.getMessage());
            }
        }, timeout, delayLevel);
        log.info("已异步发送延时消息 message = {}", message);
    }

    /**
     * 发送单向顺序消息
     *
     * @param topic topic
     */
    public void sendOneWayOrderly(String topic) {
        for (int i = 0; i < 30; i++) {
            template.sendOneWayOrderly(topic, MessageBuilder.withPayload("message - " + i).build(), "topic");
            log.info("已发送消息：message = {}", "message - " + i);
        }
    }

    /**
     * 同步发送顺序消息
     *
     * @param topic topic
     */
    public void syncSendOrderly(String topic) {
        for (int i = 0; i < 30; i++) {
            SendResult sendResult = template.syncSendOrderly(topic, MessageBuilder.withPayload("message - " + i).build(), "syncOrderlyKey");
            log.info("同步顺序消息发送成功：message = {}, sendResult = {}", "message - " + i, sendResult);
        }
    }

    /**
     * 异步有序发送消息
     *
     * @param topic topic
     */
    public void asyncSendOrderly(String topic) {

        for (int i = 0; i < 30; i++) {
            template.asyncSendOrderly(topic, MessageBuilder.withPayload("message - " + i).build(), "asyncSendOrderlyKey", new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("异步顺序消息发送成功：sendResult = {}", sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    log.info("消息发送异常：exception = {}", e.getMessage());
                }
            });
        }
    }
}
