package ec.consul.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Demo1Controler {
	
	@RequestMapping("/index")
    public Object index() {  
        return "index";  
    }  
      
    @RequestMapping("/home")  
    public Object home() {  
        return "home";  
    }  

}
