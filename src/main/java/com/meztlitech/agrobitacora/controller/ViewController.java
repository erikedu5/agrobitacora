package com.meztlitech.agrobitacora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/auth")
    public String auth() {
        return "auth";
    }

    @GetMapping("/bill")
    public String bill() {
        return "bill";
    }

    @GetMapping("/crop")
    public String crop() {
        return "crop";
    }

    @GetMapping("/fumigation")
    public String fumigation() {
        return "fumigation";
    }

    @GetMapping("/irrigation")
    public String irrigation() {
        return "irrigation";
    }

    @GetMapping("/labor")
    public String labor() {
        return "labor";
    }

    @GetMapping("/nutrition")
    public String nutrition() {
        return "nutrition";
    }

    @GetMapping("/production")
    public String production() {
        return "production";
    }
}
