package com.arx.springmvcangularjs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/overview")
public class OverviewController {

    @RequestMapping("/layout")
    public String getCarPartialPage() {
        return "overview/layout";
    }
}
