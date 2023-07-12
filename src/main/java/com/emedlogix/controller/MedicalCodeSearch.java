package com.emedlogix.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/mcode")
public class MedicalCodeSearch {

    @GetMapping("/search")
    String getCodeInfo() {
    	return "Hello World";
    }
}
