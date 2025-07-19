package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.service.MiembroDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.gimnasio.systemgym.service.InscripcionMembresiaService;
import com.gimnasio.systemgym.service.MiembroService;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class DashboardController {

    private final MiembroService miembroService;
    private final InscripcionMembresiaService inscripcionMembresiaService;

    @Autowired
    public DashboardController(MiembroService miembroService,
                               InscripcionMembresiaService inscripcionMembresiaService) {
        this.miembroService = miembroService;
        this.inscripcionMembresiaService = inscripcionMembresiaService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof MiembroDetails) {
            MiembroDetails miembroDetails = (MiembroDetails) authentication.getPrincipal();
            String identificadorMiembro = miembroDetails.getUsername();

            Optional<Miembro> optionalMiembroAutenticado = miembroService.obtenerMiembroPorNumeroIdentificacion(identificadorMiembro);

            if (optionalMiembroAutenticado.isPresent()) {
                Miembro miembroAutenticado = optionalMiembroAutenticado.get();

                Optional<InscripcionMembresia> optionalInscripcionActiva = miembroService.obtenerMembresiaActivaActual(miembroAutenticado);

                if (optionalInscripcionActiva.isPresent()) {
                    InscripcionMembresia inscripcion = optionalInscripcionActiva.get();
                    model.addAttribute("inscripcionMembresia", inscripcion);
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
                System.out.println("DEBUG: Miembro no encontrado en la base de datos para el identificador: " + identificadorMiembro);
                return "redirect:/logout";
            }
        } else {
            System.out.println("DEBUG: Authentication is null or not MiembroDetails in DashboardController.");
            return "redirect:/login";
        }
        System.out.println("DEBUG: Returning dashboard view for authenticated user.");
        return "dashboard";
    }
}