package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.repository.InscripcionMembresiaRepository; // Importa tu nuevo repositorio
import com.gimnasio.systemgym.service.MiembroDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate; // Importa LocalDate
import java.util.Optional;

@Controller
public class DashboardController {

    private final InscripcionMembresiaRepository inscripcionMembresiaRepository;

    @Autowired
    public DashboardController(InscripcionMembresiaRepository inscripcionMembresiaRepository) {
        this.inscripcionMembresiaRepository = inscripcionMembresiaRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof MiembroDetails) {
            MiembroDetails miembroDetails = (MiembroDetails) authentication.getPrincipal();

            // Obtenemos el objeto Miembro real desde MiembroDetails
            Miembro miembroAutenticado = miembroDetails.getMiembro();

            // Buscar la inscripción de membresía activa y no vencida para este miembro
            // Usaremos "ACTIVA" para el estado.
            Optional<InscripcionMembresia> optionalInscripcion = inscripcionMembresiaRepository
                    .findTopByMiembroAndEstadoOrderByFechaFinDesc(miembroAutenticado, "ACTIVA");

            // Opcional: También podrías verificar la fecha de fin aquí, aunque el estado "ACTIVA" debería ser suficiente
            // Si la encuentras, asegúrate de que no esté vencida por si el estado no se ha actualizado aún.
            if (optionalInscripcion.isPresent()) {
                InscripcionMembresia inscripcion = optionalInscripcion.get();
                // Si la fecha de fin ya pasó, consideramos que está vencida para la vista
                if (inscripcion.getFechaFin().isBefore(LocalDate.now())) {
                    model.addAttribute("inscripcionMembresia", null); // Considera que no hay activa
                    // Opcional: podrías actualizar el estado a "VENCIDA" aquí, pero es mejor que el servicio lo haga
                } else {
                    model.addAttribute("inscripcionMembresia", inscripcion);
                }
            } else {
                model.addAttribute("inscripcionMembresia", null); // No se encontró una membresía activa
            }

            model.addAttribute("miembroPrincipal", miembroDetails);

        } else {
            // Si no está autenticado o no es MiembroDetails, redirige a login
            return "redirect:/login";
        }
        return "dashboard";
    }
}