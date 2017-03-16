package org.consul.demo.http;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StartApplication {

	public static void main(String[] args) throws SocketException {
		// 获取其他参数,新增了ip指定
		String argsNew[] = getArgs(args);
		SpringApplication.run(StartApplication.class, argsNew);
		
	}

	/**
	 * 这里新增了ip的指定,允许指定固定网段的ip
	 * @param args
	 * @return
	 */
	public static String[] getArgs(String[] args) {
		//初始化ip前缀， 
		String prefixIp = System.getProperty("prefixIp");
		if(prefixIp==null || "".equals(prefixIp.trim())){
			prefixIp = "10.0"; //默认情况
		}
		List<String> list = new ArrayList<String>();
		String ip = getIp(prefixIp);
    	list.add("--server.address="+ip);
    	if(args!=null){
    		for(String arg : args){
    			list.add(arg);
    		}
    	}
    	String[] args1 = list.toArray(new String[0]);
    	System.out.println("启动参数是: " + list);
    	return args1;
	}

	

	public static String getIp(String prefixIp) {
		// TODO Auto-generated method stub
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			String ip = null;
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				String name = ni.getName();
				// ni.getInetAddresses().nextElement().getHostAddress()

				try {
					ip = ni.getInetAddresses().nextElement().getHostAddress();
					if (ip != null && ip.startsWith(prefixIp)) {
						System.out.println("name = " + name + ", ip = " + ip);
						break;
					}

				} catch (Exception e) {

				}
			}

			if (ip == null) {
				throw new RuntimeException("获取ip失败，查询不能启动");
			}

			return ip;
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("获取ip失败，查询不能启动");
		}

	}

}
