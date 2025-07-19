package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Membresia;
import com.gimnasio.systemgym.service.MembresiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/membresias")
public class AdminMembresiaController {

    private final MembresiaService membresiaService;

    public AdminMembresiaController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }

    @GetMapping("/ver")
    public String listarMembresias(Model model) {
        List<Membresia> membresias = membresiaService.obtenerTodasLasMembresias();
        model.addAttribute("membresias", membresias);
        return "admin/membresias/listar-membresias";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("membresia", new Membresia());
        model.addAttribute("modoEdicion", false);
        return "admin/membresias/crear-editar-membresia";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return membresiaService.obtenerMembresiaPorId(id)
                .map(membresia -> {
                    model.addAttribute("membresia", membresia);
                    model.addAttribute("modoEdicion", true);
                    return "admin/membresias/crear-editar-membresia";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Membresía no encontrada para editar.");
                    return "redirect:/admin/membresias/ver";
                });
    }

    @PostMapping("/guardar")
    public String guardarMembresia(@ModelAttribute Membresia membresia, RedirectAttributes redirectAttributes) {
        try {
            if (membresia.getId() == null) {
                membresiaService.crearMembresia(membresia);
                redirectAttributes.addFlashAttribute("success", "Membresía creada exitosamente.");
            } else {
                membresiaService.actualizarMembresia(membresia);
                redirectAttributes.addFlashAttribute("success", "Membresía actualizada exitosamente.");
            }
            return "redirect:/admin/membresias/ver";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (membresia.getId() != null) {
                return "redirect:/admin/membresias/editar/" + membresia.getId();
            } else {
                return "redirect:/admin/membresias/crear";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al guardar la membresía: " + e.getMessage());
            if (membresia.getId() != null) {
                return "redirect:/admin/membresias/editar/" + membresia.getId();
            } else {
                return "redirect:/admin/membresias/crear";
            }
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMembresia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            membresiaService.eliminarMembresia(id);
            redirectAttributes.addFlashAttribute("success", "Membresía eliminada exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al eliminar la membresía: " + e.getMessage());
        }
        return "redirect:/admin/membresias/ver";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstadoMembresia(@PathVariable Long id, @RequestParam boolean activo, RedirectAttributes redirectAttributes) {
        try {
            membresiaService.cambiarEstadoActivoMembresia(id, activo);
            redirectAttributes.addFlashAttribute("success", "Estado de la membresía actualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado de la membresía: " + e.getMessage());
        }
        return "redirect:/admin/membresias/ver";
    }
}