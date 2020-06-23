package com.neo.contentcenter.service.share;

import com.neo.contentcenter.dao.content.ShareMapper;
import com.neo.contentcenter.dao.rocketMQ.RocketmqTransactionLogMapper;
import com.neo.contentcenter.domain.dto.content.ShareAuditDTO;
import com.neo.contentcenter.domain.dto.content.ShareDTO;
import com.neo.contentcenter.domain.dto.message.UserAddBonusMsgDTO;
import com.neo.contentcenter.domain.dto.user.UserDTO;
import com.neo.contentcenter.domain.entity.content.Share;
import com.neo.contentcenter.domain.entity.rocketMQ.RocketmqTransactionLog;
import com.neo.contentcenter.domain.enums.AuditStatusEnum;
import com.neo.contentcenter.feignclient.UserCenterFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author: zhaoyun
 * @date: 2020/5/21 22:23
 * @version: 1.0.0
 */
@Service
@Slf4j
public class ShareService {
    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;
    @Autowired
    private UserCenterFeignClient userCenterFeignClient;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        int userId = share.getUserId();
        UserDTO userDTO = userCenterFeignClient.findById(userId);
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setVxNickname(userDTO.getVxNickname());
        return shareDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void insert(Share share) {
        this.shareMapper.insertSelective(share);
    }

    public Share auditById(Integer id, ShareAuditDTO auditDTO) {
        Share share = shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法，分享不存在！");
        }
        if (!"NOT_YET".equals(share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法，该分享未通过审核或已通过审核！");
        }
        //如果是 PASS 发送消息给rocketMQ，让用户去消费消息，并为用户添加积分
        if (AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())) {
            //发送半消息
            String transactionId = UUID.randomUUID().toString();
            this.rocketMQTemplate.send(
                    MessageBuilder.withPayload(UserAddBonusMsgDTO.builder()
                            .userId(share.getUserId())
                            .bonus(50)
                            .build())
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .build()
            );
        } else {
            this.auditByIdInDB(id, auditDTO);
        }
        return shareMapper.selectByPrimaryKey(id);
    }

    /**
     * 将分享审核数据存入数据库
     *
     * @param id       用户 id
     * @param auditDTO 审核数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO auditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(auditDTO.getAuditStatusEnum().toString())
                .reason(auditDTO.getReason())
                .build();
        this.shareMapper.updateByPrimaryKeySelective(share);
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO auditDTO, String transactionId) {
        this.auditByIdInDB(id, auditDTO);
        //记录日志
        this.rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .log("审核分享...")
                        .build()
        );
    }
}
