package org.consul.demo.http.consumer.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ec.consul.utils.MyLoadBalancer;

@Component
public class InitBean {

	@Autowired  
    private LoadBalancerClient loadBalancer;  
      
    @Autowired  
    private DiscoveryClient discoveryClient;
    
    @Value("${provider.instaceName}")
    private String serviceId;
    
    @Bean
	private MyLoadBalancer myLoadBalancer(){
		MyLoadBalancer mloadBalancer =  new MyLoadBalancer();
		mloadBalancer.init(loadBalancer, discoveryClient, serviceId);
		return mloadBalancer;
	}
    
}
