package com.example.postgenerator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostGeneratorController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
