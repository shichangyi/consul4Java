package org.consul.demo.http.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController{

	private Random rd = new Random();
	 
	@GetMapping("/hello")
	public Map<String,Object> getInfo(Integer id){
		 Map<String,Object> map = new  HashMap<String,Object>();
		 map.put("nams", "shicy"+id);
		 map.put("age", rd.nextInt(100+id) % 100 + id *id);
		 map.put("id", id * id);
		 return map;
	}
}
