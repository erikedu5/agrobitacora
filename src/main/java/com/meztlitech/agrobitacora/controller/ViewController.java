package com.meztlitech.agrobitacora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meztlitech.agrobitacora.entity.RoleEntity;
import com.meztlitech.agrobitacora.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@Controller
@RequiredArgsConstructor
@Log4j2
public class ViewController {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ROLE_ADMIN = "Admin";

    private boolean isAdmin(String token) {
        if (token == null) {
            return false;
        }
        try {
            Claims claims = jwtService.decodeToken(token);
            RoleEntity role = objectMapper.convertValue(claims.get("role"), RoleEntity.class);
            return ROLE_ADMIN.equals(role.getName());
        } catch (Exception e) {
            log.warn("Failed to validate admin token", e);
            return false;
        }
    }

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

    @GetMapping("/admin")
    public String admin(@RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAdmin(token)) {
            return "redirect:/home";
        }
        return "admin";
    }

    @GetMapping("/admin/users")
    public String adminUsers(@RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAdmin(token)) {
            return "redirect:/home";
        }
        return "admin-users";
    }

    @GetMapping("/admin/engineers")
    public String adminEngineers(@RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAdmin(token)) {
            return "redirect:/home";
        }
        return "admin-engineers";
    }

    @GetMapping("/admin/admins")
    public String adminAdmins(@RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAdmin(token)) {
            return "redirect:/home";
        }
        return "admin-admins";
    }
}
