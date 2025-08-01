package com.meztlitech.agrobitacora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
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

    @GetMapping("/association")
    public String association() {
        return "association";
    }

    @GetMapping("/balance")
    public String balance() {
        return "balance";
    }

    @GetMapping("/store")
    public String store() {
        return "store";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin-users";
    }

    @GetMapping("/admin/engineers")
    public String adminEngineers() {
        return "admin-engineers";
    }

    @GetMapping("/admin/licenses")
    public String adminLicenses() {
        return "admin-licenses";
    }
}
