package com.neo.contentcenter.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 扩展 Ribbon 以支持 nacos 权重
 *
 * @author zhaoWenCai
 * @date 2020/5/24 14:18
 * @since 1.0.0
 */
@Slf4j
public class NacosWeightedRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            //想要请求的微服务的名称
            String name = loadBalancer.getName();
            //拿到服务发现的相关 API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            //nacos client自动通过基于权重的负载均衡算法，给我们一个实例
            Instance instance = namingService.selectOneHealthyInstance(name);
            log.info("选择的实例是：port = {},instance = {}", instance.getPort(), instance.getServiceName());
            return new NacosServer(instance);
        } catch (NacosException e) {
            return null;
        }
    }
}
