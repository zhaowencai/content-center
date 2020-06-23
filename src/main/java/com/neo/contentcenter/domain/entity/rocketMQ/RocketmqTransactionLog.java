package com.neo.contentcenter.domain.entity.rocketMQ;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zhaoyun
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rocketmq_transaction_log")
public class RocketmqTransactionLog {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 事务 id
     */
    @Column(name = "transaction_id")
    private String transactionId;

    /**
     * 日志
     */
    private String log;
}