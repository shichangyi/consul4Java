package org.consul.demo.http.consumer.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RibbonController {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${provider.instaceName}")
	private String instaceName = "shicy-h1";

	/**
	 * rest 方式调用, 只要知道服务名称,就可以远程调用了 循环调用：times 次,然后返回结果
	 * 
	 * @return
	 */
	@RequestMapping("/ribbon/env")
	public Object rest(Integer times) {

		if (times == null) {
			times = 10;
		}
		//map.put("serverAddress", serverAddress);
		//map.put("serverPort", serverPort);
		//map.put("host", host);
		//map.put("result", result);
		
		Random rd = new Random();
		String url = "http://" + instaceName + "/myInfo";
		Long begin = System.currentTimeMillis();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < times; i++) {
			String key = null;
			try {
				Integer id = rd.nextInt() % 100;
				Map key1 = restTemplate.getForObject(String.format(url, id), Map.class);
				key = key1.get("serverPort").toString();
			} catch (Exception e) {
				// TODO: handle exception
				key = "error , e" + e.getMessage();
				System.err.println("error , e" + e.toString());
			}
			Integer res = map.get(key);
			if (res == null) {
				res = 0;
			}
			res++;
			map.put(key, res);
		}
		Long userTime = System.currentTimeMillis() - begin;
		map.put("times=" + times, userTime.intValue());
		System.out.println(String.format("耗时:%s ,调用次数: %s", userTime, times));
		return map;

	}

}
