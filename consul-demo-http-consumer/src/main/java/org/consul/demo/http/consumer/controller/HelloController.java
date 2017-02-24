package org.consul.demo.http.consumer.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * 接口测试
 * 1.rest, 批量测试，不重试
 * 2.retry,批量测试，重试
 * 3.hello,单个接口测试，重试
 * @author Administrator
 *
 */
@RestController
public class HelloController {

	 
      
    @Autowired
	private RestTemplate restTemplate;
    
    @Value("${provider.instaceName}")
    private  String instaceName;
    
    
    /**
     * rest 方式调用, 只要知道服务名称,就可以远程调用了
     * 循环调用：times 次,然后返回结果
     * 使用  restTemplate 调用， 可能服务的某个节点down了，但是 restTemplate 依然尝试去调用他
     * 如果需要成功，请重试调用
     * @return
     */
    @RequestMapping("/hello/rest")
	public Object rest(Integer times) {
    	
    	if(times==null){
    		times = 10;
    	}
    	Random rd = new Random();
    	Long begin = System.currentTimeMillis();
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	for(int i=0;i<times;i++){
    		String key = null;
    		String url = null;
    		Integer id = rd.nextInt(5);
    		try{
    		    url = "http://"+instaceName+"/hello?id="+id;
    			restTemplate.getForObject(url, String.class,id);
    			key = "successed";
        	}catch (Exception e) {
    			// TODO: handle exception
        		key = "error , e" + e.getMessage();
        		System.err.println("error , e" + e.toString());
    		}
    		
        	Integer res = map.get(key+"__" + url);
        	if(res==null){
        		res = 0;
        	}
        	res++;
        	map.put(key+"__" + url, res);
    	}
    	Long userTime = System.currentTimeMillis() - begin;
    	map.put("times="+times,userTime.intValue());
    	System.out.println(String.format("耗时:%s ,调用次数: %s",userTime,times ));
        return map;  
        
	}
    
    
    /**
     * rest 方式调用, 只要知道服务名称,就可以远程调用了
     * 循环调用：times 次,然后返回结果
     * 使用  restTemplate 调用， 可能服务的某个节点down了，但是 restTemplate 依然尝试去调用他
     * 如果需要成功，请重试调用
     * @return
     */
    @RequestMapping("/hello/retry")
	public Object restWitretry(Integer times) {
    	
    	if(times==null){
    		times = 10;
    	}
    	Random rd = new Random();
    	Long begin = System.currentTimeMillis();
    	Integer retry = 15; //失败后，重试10次
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	for(int i=0;i<times;i++){
    		String key = null;
    		String url = null;
    		Integer id = rd.nextInt(5);
    		
    		for(int r=0;r<retry;r++){
    			try{
    				 url = "http://"+instaceName+"/hello?id="+id;
        			restTemplate.getForObject(url, String.class,id);
        			key = "successed, retryTimes = " + r + " id = " + id;
        			break;
            	}catch (Exception e) {
            		//重试之后，依然错误
            		if(r==retry-1){
            			key = "error , e" + e.getMessage() + " id = " + id;
                		System.err.println("error , e" + e.toString());
            		}
            		
        		}
    		}
    		
    		
        	Integer res = map.get(key+"__" + url);
        	if(res==null){
        		res = 0;
        	}
        	res++;
        	map.put(key+"__" + url, res);
    	}
    	Long userTime = System.currentTimeMillis() - begin;
    	map.put("times="+times,userTime.intValue());
    	System.out.println(String.format("耗时:%s ,调用次数: %s",userTime,times ));
        return map;  
        
	}
    
    
    /**
     * 远程调用，超时重试
     * @param times
     * @return
     */
    @RequestMapping("/hello/info")
	public Object hello(Integer times) {
    	Long bg = System.currentTimeMillis();
    	Random rd = new Random();
    	Integer id = rd.nextInt(5);
    	String url = "http://"+instaceName+"/hello?id="+id;
    	if(times==null){
    		times = 10;
    	}
    	
    	String res = null;
    	for(int r=0;r<times;r++){
			try{
				res = restTemplate.getForObject(url, String.class);
    			break;
        	}catch (Exception e) {
        		//重试之后，依然错误
        		if(r==times-1){
        			res = "error, retryTimes = " + r;
        		}
        		
    		}
		}
    	Long userTime = System.currentTimeMillis() - bg;
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("res", res);
    	map.put("url", url);
    	map.put("userTimes", userTime);
    
        return map;  
        
	}
    
 
	
}




