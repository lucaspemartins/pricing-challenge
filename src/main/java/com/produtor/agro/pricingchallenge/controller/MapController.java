package com.produtor.agro.pricingchallenge.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MapController {
	
	@RequestMapping("/")
    public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView("/index");
		//modelAndView.addObject("farms", farmsList);
	    return modelAndView;
    }

}
