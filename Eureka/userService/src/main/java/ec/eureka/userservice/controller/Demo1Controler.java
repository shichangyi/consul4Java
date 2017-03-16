package ec.eureka.userservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.eureka.userservice.entity.Student;

@RestController
public class Demo1Controler {

	private Map<Integer, Student> stuMap;

	public Demo1Controler() {
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		stuMap = new HashMap<Integer, Student>();
		for (int i = 10000; i < 50000; i++) {
			Student stu = new Student();
			stu.setId(i);
			stu.setName("name" + i);
			stuMap.put(i, stu);
		}
	}
	
	
	// http://127.0.0.1:8181/getStudent?id=11111

	@RequestMapping("/getStudent")
	public Object getStudentById(Integer id) {

		Map<String, Object> res = new HashMap<String, Object>();
		Student stu = stuMap.get(id);
		res.put("data", stu);
		res.put("result", 0);
		return res;
	}

}
