package com.neo.contentcenter.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.neo.contentcenter.domain.dto.message.UserAddBonusMsgDTO;
import com.neo.contentcenter.domain.entity.content.Share;
import com.neo.contentcenter.feignclient.TestBaiduFeignClient;
import com.neo.contentcenter.service.rocketmq.RocketMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhaoWenCai
 * @date 2020/5/26 16:55
 * @since 1.0.0
 */
@RestController
@Slf4j
@RefreshScope
public class TestController {

    @Autowired
    private TestBaiduFeignClient testBaiduFeignClient;

    @GetMapping("/baidu")
    public String test() {
        log.info("我可以访问百度了");
        return this.testBaiduFeignClient.index();
    }

    @GetMapping("/test-hot")
    @SentinelResource("hot")
    public String testHot(
            @RequestParam(required = false) String a,
            @RequestParam(required = false) String b) {
        return a + b;
    }

    @GetMapping("/test-sentinel-resource")
    @SentinelResource(value = "sentinel-api", blockHandler = "block")
    public String testSentinelApi(@RequestParam(required = false) String a) throws IllegalAccessException {
        if (StringUtils.isEmpty(a)) {
            throw new IllegalAccessException("a cannot be black.");
        }
        return a;
    }

    public String block(String a, BlockException e) {
        log.warn("限流，或者降级了", e);
        return "限流或者降级了";
    }

    @Value("${your.configuration}")
    private String yourConfiguration;

    @GetMapping("/test-config")
    public String config() {
        return this.yourConfiguration;
    }

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/test-stream")
    public String testStream() {
        this.rocketMQTemplate.send(MessageBuilder.withPayload("这是一个普通的测试消息~").build());
        return "success";
    }

    @Autowired
    private RocketMessageProducer rocketMessageProducer;

    /**
     * 同步发送字符串消息
     *
     * @param msg 消息体
     * @return 反馈信息
     */
    @GetMapping("/sync-message")
    public String syncSend(@RequestParam(required = false) String msg) {
        if (StringUtils.isEmpty(msg)) {
            log.info("没有消息可发。。。");
            return "请指定要发送的消息";
        }
        rocketMessageProducer.syncSend("sync-message", msg);
        log.info("已发送的消息: message = {}", msg);
        return "success：已发送同步消息 " + msg;
    }

    /**
     * 异步发送消息
     *
     * @return 反馈信息
     */
    @GetMapping("/async-message")
    public String asyncSend() {
        String msg = "hello";
        rocketMessageProducer.asyncSend("async-message", msg);
        return "success：已发送异步消息 " + msg;
    }

    @GetMapping("/tag-message")
    public String sendWithTag() {
        rocketMessageProducer.sendWithTag("tag-message:tagA", "msg with tag");
        return "success：已发送带 tag 的消息 ";
    }

    @GetMapping("/sync-messages")
    public String syncSendMessages() {
        List<Message<String>> messages = rocketMessageProducer.syncSendMessages("sync-messages");
        return "已同步发送批量消息 messages = " + JSON.toJSONString(messages);
    }

    @GetMapping("/transactional-message")
    public String sendMessageInTransaction() {
        this.rocketMessageProducer.sendMessageInTransaction("transactional-message", Share.builder()
                .userId(2)
                .reason("分享的资源")
                .author("zhuzhu")
                .title("学习笔记")
                .gmtCreate(new Date())
                .auditStatus("NOT_YET")
                .build());
        return "已发送事务消息";
    }

    /**
     * 发送
     *
     * @return 反馈信息
     */
    @GetMapping("/sync-delay-message")
    public String syncSendTimeout() {
        UserAddBonusMsgDTO dto = UserAddBonusMsgDTO.builder()
                .userId(2)
                .bonus(25)
                .build();
        String jsonString = JSON.toJSONString(dto);
        rocketMessageProducer.syncSendDelay("sync-delay-message", dto, 10000, 3);
        return "success：已发送同步延时消息 " + jsonString;
    }

    @GetMapping("/async-delay-message")
    public String asyncSendDelay() {
        UserAddBonusMsgDTO dto = UserAddBonusMsgDTO.builder()
                .userId(2)
                .bonus(25)
                .build();
        String jsonString = JSON.toJSONString(dto);
        rocketMessageProducer.asyncSendDelay("async-delay-message", dto, 10000, 3);
        return "success：已发送异步延时消息 " + jsonString;
    }

    /**
     * 单向发送消息
     *
     * @return 反馈信息
     */
    @GetMapping("/oneWay-message")
    public String sendOneWay() {
        UserAddBonusMsgDTO msgDTO = UserAddBonusMsgDTO.builder()
                .userId(3)
                .bonus(20)
                .build();
        String jsonString = JSON.toJSONString(msgDTO);
        rocketMessageProducer.sendOneWay("oneWay-message", msgDTO);
        log.info("已单向发送消息: message = {}", jsonString);
        return "success：已发送单向消息 " + jsonString;
    }

    /**
     * 单向发送有序消息
     *
     * @return 反馈信息
     */
    @GetMapping("/oneWay-order-message")
    public String sendOneWayOrderly() {
        rocketMessageProducer.sendOneWayOrderly("oneWay-order-message");
        log.info("已单向发送有序消息..");
        return "success：已单向发送有序消息.. ";
    }

    /**
     * 异步发送顺序消息
     *
     * @return 反馈信息
     */
    @GetMapping("/async-order-message")
    public String asyncSendOrderly() {
        rocketMessageProducer.asyncSendOrderly("async-order-message");
        log.info("已异步发送有序消息..");
        return "success：已异步发送有序消息.. ";
    }

    /**
     * 同步发送顺序消息
     *
     * @return 反馈信息
     */
    @GetMapping("/sync-order-message")
    public String syncSendOrderly() {
        rocketMessageProducer.syncSendOrderly("sync-order-message");
        log.info("已同步发送有序消息..");
        return "success：已同步发送有序消息.. ";
    }
}
