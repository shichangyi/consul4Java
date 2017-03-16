package org.consul.demo.http.consumer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class EntranceController {

	@RequestMapping(value="/index", method=RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView mv = new ModelAndView("index");
        return mv;
    }
	
	
	
}
