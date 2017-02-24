package org.consul.demo.http.consumer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

	@Autowired  
    private LoadBalancerClient loadBalancer;  
      
    @Autowired  
    private DiscoveryClient discoveryClient;
    
    @Autowired
	private RestTemplate restTemplate;
    
    @Value("${provider.instaceName}")
    private  String instaceName;
    
    
    /**
     * rest 方式调用, 只要知道服务名称,就可以远程调用了
     * 循环调用：times 次,然后返回结果
     * @return
     */
    @RequestMapping("/rest")
	public Object rest(Integer times) {
    	
    	if(times==null){
    		times = 10;
    	}
    	
    	Long begin = System.currentTimeMillis();
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	for(int i=0;i<times;i++){
    		String key = null;
    		try{
    			 key = restTemplate.getForObject("http://"+instaceName+"/hello", String.class);
        	}catch (Exception e) {
    			// TODO: handle exception
        		key = "error , e" + e.getMessage();
        		System.err.println("error , e" + e.toString());
    		}
    		
        	Integer res = map.get(key);
        	if(res==null){
        		res = 0;
        	}
        	res++;
        	map.put(key, res);
    	}
    	Long userTime = System.currentTimeMillis() - begin;
    	map.put("times="+times,userTime.intValue());
    	System.out.println(String.format("耗时:%s ,调用次数: %s",userTime,times ));
        return map;  
        
	}
    
    
	@GetMapping("/hello")
	public Map<String,Object> getInfo(){
		 Map<String,Object> map = new  HashMap<String,Object>();
		 map.put("nams", "shicy");
		 map.put("age", 35);
		 map.put("id", 1);
		 return map;
	}
	
	
	/** 
     * 从所有服务中选择一个服务（轮询， 实际上是本地轮询,性能没有问题） 
     * 1.这里拿到的是可用的提供者,down的提供者机器列表，30秒后，会被清理掉
     * 
     * 2.虽然说是可用的提供者机器列表，但是还是有延迟，
     * 	   比如一个提供者挂了之后， 服务不可用， 但是 客户端需要1分钟左右，才会去更新 服务列表
     *   在这个期间， 消费者还是认为down的机器可用， 
     *   因此在rpc的时候，负责重试（telnet 调用不通，换另外一个地址）
     */  
    @RequestMapping("/d")  
    public Object discover(String instaceName1,Integer times) {  
    	if(instaceName1!=null){
    		instaceName = instaceName1;
    	}
    	if(times==null){
    		times = 10;
    	}
    	Long begin = System.currentTimeMillis();
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	for(int i=0;i<times;i++){
    		ServiceInstance sv=  loadBalancer.choose(instaceName);
        	String host = sv.getHost();
        	Integer port = sv.getPort();
        	String key = (host + ":" + port);
        	Integer res = map.get(key);
        	if(res==null){
        		res = 0;
        	}
        	res++;
        	map.put(key, res);
    	}
    	Long userTime = System.currentTimeMillis() - begin;
    	map.put("times="+times,userTime.intValue());
    	System.out.println(String.format("耗时:%s ,调用次数: %s",userTime,times ));
        return map;  
    }  
      
    /** 
     * 获取所有服务  
     */  
    @RequestMapping("/s")  
    public Object services(String instaceName1) {  
    	if(instaceName1!=null){
    		instaceName = instaceName1;
    	}
    	
        return discoveryClient.getInstances(instaceName);  
    }
}




