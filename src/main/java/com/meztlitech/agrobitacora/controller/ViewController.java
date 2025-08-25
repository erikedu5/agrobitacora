package com.meztlitech.agrobitacora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageId", "home");
        model.addAttribute("title", "Condiciones climáticas");
        return "pages/home/index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("pageId", "home");
        model.addAttribute("title", "Condiciones climáticas");
        return "pages/home/index";
    }

    @GetMapping("/auth")
    public String auth(Model model) {
        model.addAttribute("pageId", "auth");
        model.addAttribute("title", "Gestión de Autenticación");
        return "pages/auth/index";
    }

    @GetMapping("/bill")
    public String bill(Model model) {
        model.addAttribute("pageId", "bill");
        model.addAttribute("title", "Gestión de Facturas");
        return "pages/bill/index";
    }

    @GetMapping("/crop")
    public String crop(Model model) {
        model.addAttribute("pageId", "crop");
        model.addAttribute("title", "Gestión de Cultivos");
        return "pages/crop/index";
    }

    @GetMapping("/fumigation")
    public String fumigation(Model model) {
        model.addAttribute("pageId", "fumigation");
        model.addAttribute("title", "Gestión de Fumigaciones");
        return "pages/fumigation/index";
    }

    @GetMapping("/irrigation")
    public String irrigation(Model model) {
        model.addAttribute("pageId", "irrigation");
        model.addAttribute("title", "Gestión de Riegos");
        return "pages/irrigation/index";
    }

    @GetMapping("/labor")
    public String labor(Model model) {
        model.addAttribute("pageId", "labor");
        model.addAttribute("title", "Gestión de Labores");
        return "pages/labor/index";
    }

    @GetMapping("/nutrition")
    public String nutrition(Model model) {
        model.addAttribute("pageId", "nutrition");
        model.addAttribute("title", "Gestión de Nutrición");
        return "pages/nutrition/index";
    }

    @GetMapping("/production")
    public String production(Model model) {
        model.addAttribute("pageId", "production");
        model.addAttribute("title", "Gestión de Producción");
        return "pages/production/index";
    }

    @GetMapping("/association")
    public String association(Model model) {
        model.addAttribute("pageId", "association");
        model.addAttribute("title", "Asociaciones");
        return "pages/association/index";
    }

    @GetMapping("/balance")
    public String balance(Model model) {
        model.addAttribute("pageId", "balance");
        model.addAttribute("title", "Balance");
        return "pages/balance/index";
    }

    @GetMapping("/store")
    public String store(Model model) {
        model.addAttribute("pageId", "store");
        model.addAttribute("title", "Mi tienda");
        return "pages/store/index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("pageId", "admin");
        model.addAttribute("title", "Administración");
        return "pages/admin/index";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("pageId", "admin/users");
        model.addAttribute("title", "Usuarios");
        return "pages/admin/users/index";
    }

    @GetMapping("/admin/engineers")
    public String adminEngineers(Model model) {
        model.addAttribute("pageId", "admin/engineers");
        model.addAttribute("title", "Ingenieros");
        return "pages/admin/engineers/index";
    }

    @GetMapping("/admin/licenses")
    public String adminLicenses(Model model) {
        model.addAttribute("pageId", "admin/licenses");
        model.addAttribute("title", "Licencias");
        return "pages/admin/licenses/index";
    }
}
