package com.neo.contentcenter.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 扩展 ribbon，优先调用同一集群下的实例
 *
 * @author zhaoWenCai
 * @date 2020/5/25 10:25
 * @since 1.0.0
 */
@Slf4j
public class NacosSomeClusterWeightedRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    }

    @Override
    public Server choose(Object o) {
        try {
            //拿到配置文件中的集群名称
            String clusterName = nacosDiscoveryProperties.getClusterName();
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            //想要请求的微服务的名称
            String name = loadBalancer.getName();
            //拿到服务发现的相关 API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            //找到指定服务的所有实例 A
            List<Instance> instances = namingService.selectInstances(name, true);
            //过滤指定集群下的所有实例 B
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> {
                        Map<String, String> metadata = instance.getMetadata();
                        return Objects.equals(instance.getClusterName(), clusterName)
                                && "v1".equals(metadata.get("version"));
                    })
                    .collect(Collectors.toList());
            //如果 B 失效，则引用 A
            List<Instance> chooseInstances = new ArrayList<>();
            if (CollectionUtils.isEmpty(sameClusterInstances)) {
                chooseInstances = instances;
                log.warn("发生了跨集群调用，name = {}, cluster = {}, instances = {}", name, clusterName, instances);
            } else {
                chooseInstances = sameClusterInstances;
            }
            //
            //基于权重的负载均衡算法，返回一个实例
            Instance instance = ExtendBalancer.getHostByRandomWeight2(chooseInstances);
            log.info("调用的实例是：name = {}, port = {}, instance = {}", name, instance.getPort(), instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("发生异常了：", e);
            return null;
        }
    }
}

/**
 * 继承封装 nacos 基于负载均衡算法选择一个实例
 */
class ExtendBalancer extends Balancer {
    public static Instance getHostByRandomWeight2(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}
