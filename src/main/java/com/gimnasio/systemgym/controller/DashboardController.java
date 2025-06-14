package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.repository.InscripcionMembresiaRepository;
import com.gimnasio.systemgym.service.MiembroDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.gimnasio.systemgym.service.InscripcionMembresiaService;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class DashboardController {

    private final InscripcionMembresiaService inscripcionMembresiaService;

    @Autowired
    public DashboardController(InscripcionMembresiaService inscripcionMembresiaService) {
        this.inscripcionMembresiaService = inscripcionMembresiaService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof MiembroDetails) {
            MiembroDetails miembroDetails = (MiembroDetails) authentication.getPrincipal();
            Miembro miembroAutenticado = miembroDetails.getMiembro();


            Optional<InscripcionMembresia> optionalInscripcionActiva = inscripcionMembresiaService
                    .obtenerMembresiaActivaPorMiembro(miembroAutenticado.getId());

            if (optionalInscripcionActiva.isPresent()) {
                InscripcionMembresia inscripcion = optionalInscripcionActiva.get();

                if (inscripcion.getFechaFin().isBefore(LocalDate.now())) {
                    model.addAttribute("inscripcionMembresia", null);

                } else {
                    model.addAttribute("inscripcionMembresia", inscripcion);
                }
            } else {
                model.addAttribute("inscripcionMembresia", null);
            }


            Optional<InscripcionMembresia> optionalInscripcionPendiente = inscripcionMembresiaService
                    .obtenerInscripcionPendientePorMiembro(miembroAutenticado.getId());

            if (optionalInscripcionPendiente.isPresent()) {
                model.addAttribute("membresiaPendiente", optionalInscripcionPendiente.get());
            } else {
                model.addAttribute("membresiaPendiente", null);
            }

            model.addAttribute("miembroPrincipal", miembroDetails);

        } else {
            return "redirect:/login";
        }
        return "dashboard";
    }
}