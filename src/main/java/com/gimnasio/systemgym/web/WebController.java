package com.gimnasio.systemgym.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        return "admin/dashboard";
    }

    @GetMapping("/recepcionista/miembros")
    public String recepcionistaMiembros(Model model) {
        return "recepcionista/miembros";
    }
}