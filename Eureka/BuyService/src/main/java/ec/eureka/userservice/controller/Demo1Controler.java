package ec.eureka.userservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

@RestController
public class Demo1Controler {

	@Autowired
	DiscoveryClient client;

	@Autowired
	LoadBalancerClient loadBalancerClient;

	private String userServerName = "USERSERVICE";

	// http://127.0.0.1:8181/getStudent?id=11111
	// http://10.0.108.7:59381/getStudent?id=11111

	@RequestMapping("/test")
	public Object getStudentById() {

		List<ServiceInstance> list = client.getInstances(userServerName);
		RestTemplate restTemplate = new RestTemplate();

		Random rd = new Random();
		int id = 10000 + rd.nextInt() % 50000;
		if (list != null && list.size() > 0) {
			String uri = list.get(0).getUri().toString();
			if (uri != null) {

				return (restTemplate.getForObject(uri + "/getStudent?id=" + id, String.class));
			}
		}
		return "error!!!!";

	}

	// loadBalancerClient
	// http://127.0.0.1:8020/choose?times=10
	@RequestMapping("/choose")
	public Object choose(Integer times) {
		Map<String, Integer> cntMap = new HashMap<String, Integer>();

		for (int i = 0; i < times; i++) {
			ServiceInstance sd = loadBalancerClient.choose(userServerName);
			String uri = sd.getUri().toString();
			Integer t = cntMap.get(uri);
			if (t == null) {
				t = 0;
			}
			t++;
			cntMap.put(uri, t);
		}
		return cntMap;
	}

	@RequestMapping("/test1")
	public Object test1() {

		List<ServiceInstance> list = client.getInstances(userServerName);

		List<String> uris = new ArrayList<String>();

		if (list != null && list.size() > 0) {
			for (ServiceInstance si : list) {
				String uri = si.getUri().toString();
				uris.add(uri);
			}

		}
		return uris;

	}

}
