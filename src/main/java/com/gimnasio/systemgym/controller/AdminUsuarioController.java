package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Usuario;
import com.gimnasio.systemgym.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/ver")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listar-usuarios";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("rolesDisponibles", Arrays.asList("ADMIN", "ENTRENADOR", "RECEPCIONISTA", "MIEMBRO"));
        return "crear-editar-usuario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("modoEdicion", true);
                    model.addAttribute("rolesDisponibles", Arrays.asList("ADMIN", "ENTRENADOR", "RECEPCIONISTA", "MIEMBRO"));
                    return "crear-editar-usuario";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Usuario no encontrado para editar.");
                    return "redirect:/admin/usuarios/ver";
                });
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            if (usuario.getId() == null) {
                usuarioService.crearUsuario(usuario);
                redirectAttributes.addFlashAttribute("success", "Usuario (Empleado) registrado exitosamente.");
            } else {
                usuarioService.actualizarUsuario(usuario);
                redirectAttributes.addFlashAttribute("success", "Usuario (Empleado) actualizado exitosamente.");
            }
            return "redirect:/admin/usuarios/ver";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (usuario.getId() != null) {
                return "redirect:/admin/usuarios/editar/" + usuario.getId();
            } else {
                return "redirect:/admin/usuarios/crear";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al guardar el usuario: " + e.getMessage());
            if (usuario.getId() != null) {
                return "redirect:/admin/usuarios/editar/" + usuario.getId();
            } else {
                return "redirect:/admin/usuarios/crear";
            }
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("success", "Usuario (Empleado) eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios/ver";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstadoActivoUsuario(@PathVariable Long id, @RequestParam boolean activo, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.cambiarEstadoActivoUsuario(id, activo);
            redirectAttributes.addFlashAttribute("success", "Estado del usuario (empleado) actualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado del usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios/ver";
    }
}