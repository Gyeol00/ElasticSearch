package com.example.elasticsearch.controller;

import com.example.elasticsearch.conifg.AppInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/searchList")
    public String searchList() {

        return "searchList";
    }
}
