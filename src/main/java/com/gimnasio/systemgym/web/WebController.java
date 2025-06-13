package com.gimnasio.systemgym.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/membresias")
    public String showMembresiasPage() {
        return "membresias";
    }

    @GetMapping("/registro-miembro")
    public String showRegistroMiembroPage(@RequestParam(name = "membershipId", required = false) Long membershipId,
                                          @RequestParam(name = "membershipType", required = false) String membershipType,
                                          Model model) {
        // Pasa los parámetros de la URL al modelo para que Thymeleaf pueda usarlos
        if (membershipId != null) {
            model.addAttribute("selectedMembershipId", membershipId); // <--- ¡AQUÍ SE AÑADE AL MODELO!
        }
        if (membershipType != null) {
            model.addAttribute("selectedMembershipType", membershipType);
        }
        return "registro-miembro";
    }

    @GetMapping("/pago")
    public String showPagoPage(@RequestParam(name = "miembroId") Long miembroId, // Obligatorio
                               @RequestParam(name = "inscripcionMembresiaId") Long inscripcionMembresiaId, // <-- ¡CAMBIO AQUÍ!
                               Model model) {
        model.addAttribute("miembroId", miembroId);
        model.addAttribute("inscripcionMembresiaId", inscripcionMembresiaId); // <-- ¡CAMBIO AQUÍ!
        return "pago";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/old-dashboard")
    public String showDashboardPage() {
        return "old-dashboard"; // Retorna la vista dashboard.html
    }
}