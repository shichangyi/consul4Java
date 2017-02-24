package com.ec.consul.utils;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

/**
 * 自己封装了一个 负载均衡
 * 
 * @author shicy
 *
 */
public class MyLoadBalancer {

	private LoadBalancerClient loadBalancer;
	private DiscoveryClient discoveryClient;
	private String serviceId;
	private Map<String, Boolean> blacklist;// 黑名单

	private TelnetClient telnetClient = new TelnetClient();
	private Integer defaultTimeout = 100;
	private Integer connectTimeout = 1000;
	private Integer retry = 10;
	
	private Long period = 5000l; //每隔5秒更新一次
	private Boolean needTellnet = true; // 每次更新 重置的时候, 是否需要去tellnet , blacklist

	public MyLoadBalancer(LoadBalancerClient loadBalancer, DiscoveryClient discoveryClient, String serviceId) {
		init(loadBalancer, discoveryClient, serviceId);
	}

	public MyLoadBalancer() {

	}

	public void init(LoadBalancerClient loadBalancer, DiscoveryClient discoveryClient, String serviceId) {
		System.out.println("初始化我的负载均衡器开始");
		this.loadBalancer = loadBalancer;
		this.discoveryClient = discoveryClient;
		telnetClient.setDefaultTimeout(defaultTimeout); // socket延迟时间：5000ms
		telnetClient.setConnectTimeout(connectTimeout);
		// 定时更新黑名单
		schedule();
		System.out.println("初始化我的负载均衡器结束");

	}

	

	private void schedule() {
		// TODO Auto-generated method stub
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// 定期更新任务
				updateBlacklist();
			}
		}, 3000, period);
	}
	
	protected void updateBlacklist() {
		// 获取所有的服务
		
		//1.重置黑名单
		blacklist = new HashMap<String, Boolean>();
		
		//如果不支持tellnet 跟新，那么，仅仅重置黑名单
		if(!needTellnet){
			
			System.out.println(blacklist);
			return ;
		}
		List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
		if (list != null) {
			for (ServiceInstance instance : list) {
				// 检查是否可用
				check(instance);
			}
		}
		
	}
	
	
	

	/**
	 * 随机选择一个不在黑名单的节点
	 * @param instaceName
	 * @return
	 */
	public ServiceInstance chooseUsedBlacklist(String instaceName) {
		// 重试次数
		for (int i = 0; i < retry; i++) {
			ServiceInstance sv = loadBalancer.choose(instaceName);
			String key = getKey(sv);
			Boolean in = blacklist.get(key);
			// 如果不在黑名单里面
			if(in!=null){
				return sv;
			}
		}
		return null;
	}
	
	
	/**
	 * 随机选择一个机器节点(这个节点保证telnet 通过)
	 * @param instaceName
	 * @return
	 */
	public ServiceInstance chooseWithCheck(String instaceName) {
		// 重试次数
		for (int i = 0; i < retry; i++) {
			ServiceInstance sv = loadBalancer.choose(instaceName);
			String key = getKey(sv);
			boolean  res = tellnet(sv.getHost(), sv.getPort());
			if(res){
				// tellnet 通过，返回节点
				return sv;
			}
			//tellnet 不通过的时候
			blacklist.put(key, true);
		}
		return null;
	}
	
	
	/**
	 * 随机选择一个机器节点(这个节点保证telnet 通过)
	 * @param instaceName
	 * @return
	 */
	public ServiceInstance choose(String instaceName) {
		return  loadBalancer.choose(instaceName);
	}
	
	

	/**
	 * 检查一个机器，是否是可用的
	 * 
	 * @param sv
	 * @return
	 */
	public boolean check(ServiceInstance sv) {
		
		String host = sv.getHost();
		Integer port = sv.getPort();
		String key = getKey(sv);
		// 如果不在黑名单中，看看是否能telnet通,如果不通，加入黑名单，返回false,表示不可用
		Boolean res = tellnet(host, port);
		if (res == false) {
			blacklist.put(key, true);
			return false;
		}
		return true;
	}

	private String getKey(ServiceInstance sv) {
		// TODO Auto-generated method stub
		String host = sv.getHost();
		Integer port = sv.getPort();
		return host + ":" + port;
	}

	/**
	 * 使用这个 方法去检查一个端口是否可用使用，需要 1ms
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	public boolean tellnet(String host, Integer port) {
		// TODO Auto-generated method stub
		try {
			telnetClient.connect(host, port);
			telnetClient.disconnect();
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		} // 建立一个连接,默认端口是23
		return true;
	}

	public static void main(String[] args) throws SocketException, IOException {
		MyLoadBalancer mb = new MyLoadBalancer();
		Long bg = System.currentTimeMillis();
		int times = 1000;
		int trueNum = 0;
		int falseNum = 0;

		for (int i = 0; i < times; i++) {
			// 本机 times = 1000 , all time = 894 , avg time = 0 , falseNum = 0 ,
			// trueNum = 1000
			boolean res = mb.tellnet("10.0.108.7", 20003);
			// boolean res = mb.tellnet("10.0.200.51", 18080);

			// 外网telnet times = 10 , all time = 1051 , avg time = 105 , falseNum
			// = 0 , trueNum = 10
			// boolean res = mb.tellnet("blog.csdn.net", 80);

			// 测试环境 times = 1000 , all time = 1403 , avg time = 1 , falseNum = 0
			// , trueNum = 1000
			// boolean res = mb.tellnet("10.0.201.167", 18080);

			// 开发环境 times = 1000 , all time = 1272 , avg time = 1 , falseNum = 0
			// , trueNum = 1000
			// boolean res = mb.tellnet("10.0.200.51", 18080);

			if (res == false) {
				falseNum++;
			} else {
				trueNum++;
			}
		}

		Long res = (System.currentTimeMillis() - bg);
		Long avg = res / times;
		String format = "times = %s , all time = %s , avg time = %s , falseNum = %s , trueNum = %s";

		System.out.println(String.format(format, times, res, avg, falseNum, trueNum));

	}

	public LoadBalancerClient getLoadBalancer() {
		return loadBalancer;
	}

	public void setLoadBalancer(LoadBalancerClient loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	public DiscoveryClient getDiscoveryClient() {
		return discoveryClient;
	}

	public void setDiscoveryClient(DiscoveryClient discoveryClient) {
		this.discoveryClient = discoveryClient;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Map<String, Boolean> getBlacklist() {
		return blacklist;
	}

	public void setBlacklist(Map<String, Boolean> blacklist) {
		this.blacklist = blacklist;
	}

	public TelnetClient getTelnetClient() {
		return telnetClient;
	}

	public void setTelnetClient(TelnetClient telnetClient) {
		this.telnetClient = telnetClient;
	}

	public Integer getDefaultTimeout() {
		return defaultTimeout;
	}

	public void setDefaultTimeout(Integer defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public Boolean getNeedTellnet() {
		return needTellnet;
	}

	public void setNeedTellnet(Boolean needTellnet) {
		this.needTellnet = needTellnet;
	}
	
	
	

}
