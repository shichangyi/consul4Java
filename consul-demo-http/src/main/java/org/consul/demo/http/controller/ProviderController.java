package org.consul.demo.http.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {

	private String env;
	private String name;
	private String host;

	@Value("${server.address}")
	private String serverAddress;
	@Value("${server.port}")
	private String serverPort;
	
	private Random rd = new Random();

	@GetMapping("/hello")
	public Map<String, Object> hello(Integer id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nams", "shicy" + id);
		map.put("age", rd.nextInt(100 + id) % 100 + id * id);
		map.put("id", id * id);
		return map;
	}
	
	/**
	 * 模拟一个耗时的任务
	 * 
	 * @return
	 */
	@GetMapping("/dojob")
	public Map<String, Object> getHoser() {

		long bg = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("env", env);
		map.put("name", name);
		map.put("host", host);

		Random rd = new Random();
		long millis = (rd.nextLong() + 10000) % 10000;
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("userTime", System.currentTimeMillis() - bg);

		return map;
	}




	@GetMapping("/myInfo")
	public Map<String, Object> getInfo() {
		//Random rd = new Random(); 
		//Integer result = rd.nextInt(10); 
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("serverAddress", serverAddress);
		map.put("serverPort", serverPort);
		map.put("host", host);
		//map.put("result", result);
		System.out.println(System.currentTimeMillis() + " -- map = " + map);
		return map;
	}

}
