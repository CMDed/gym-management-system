package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.dto.MiembroDTO;
import com.gimnasio.systemgym.dto.MiembroListDTO;
import com.gimnasio.systemgym.model.Membresia;
import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Miembro.Sexo;
import com.gimnasio.systemgym.service.MiembroService;
import com.gimnasio.systemgym.service.MembresiaService;
import com.gimnasio.systemgym.service.InscripcionMembresiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

@Controller
@RequestMapping("/admin/miembros")
public class AdminMiembroController {

    private final MiembroService miembroService;
    private final MembresiaService membresiaService;
    private final InscripcionMembresiaService inscripcionMembresiaService;

    @Autowired
    public AdminMiembroController(MiembroService miembroService,
                                  MembresiaService membresiaService,
                                  InscripcionMembresiaService inscripcionMembresiaService) {
        this.miembroService = miembroService;
        this.membresiaService = membresiaService;
        this.inscripcionMembresiaService = inscripcionMembresiaService;
    }

    @GetMapping("/ver")
    public String listarMiembros(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Miembro> miembros;
        if (query != null && !query.trim().isEmpty()) {
            miembros = miembroService.buscarMiembros(query);
        } else {
            miembros = miembroService.obtenerTodosLosMiembros();
        }

        List<MiembroListDTO> miembrosDTO = miembros.stream()
                .map(miembro -> {
                    Optional<InscripcionMembresia> membresiaActiva = miembroService.obtenerMembresiaActivaActual(miembro);
                    return new MiembroListDTO(miembro, membresiaActiva.orElse(null));
                })
                .collect(Collectors.toList());
        model.addAttribute("miembros", miembrosDTO);

        model.addAttribute("query", query);
        return "listar-miembros";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("miembroDTO", new MiembroDTO());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("sexoOpciones", Arrays.asList(Sexo.values()));
        model.addAttribute("membresiasDisponibles", membresiaService.obtenerMembresiasActivas());
        return "crear-editar-miembro";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return miembroService.obtenerMiembroPorId(id)
                .map(miembro -> {
                    MiembroDTO miembroDTO = new MiembroDTO();
                    miembroDTO.setId(miembro.getId());
                    miembroDTO.setTipoIdentificacion(miembro.getTipoIdentificacion());
                    miembroDTO.setNumeroIdentificacion(miembro.getNumeroIdentificacion());
                    miembroDTO.setNombre(miembro.getNombre());
                    miembroDTO.setApellido(miembro.getApellido());
                    miembroDTO.setCorreo(miembro.getCorreo());
                    miembroDTO.setTelefono(miembro.getTelefono());
                    miembroDTO.setSexo(miembro.getSexo());
                    miembroDTO.setFechaNacimiento(miembro.getFechaNacimiento());
                    miembroDTO.setActivo(miembro.getActivo());

                    miembroService.obtenerMembresiaActivaActual(miembro).ifPresent(
                            inscripcion -> miembroDTO.setMembresiaId(inscripcion.getMembresia().getId())
                    );

                    model.addAttribute("miembroDTO", miembroDTO);
                    model.addAttribute("modoEdicion", true);
                    model.addAttribute("sexoOpciones", Arrays.asList(Sexo.values()));
                    model.addAttribute("membresiasDisponibles", membresiaService.obtenerMembresiasActivas());
                    return "crear-editar-miembro";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Miembro no encontrado para editar.");
                    return "redirect:/admin/miembros/ver";
                });
    }

    @PostMapping("/guardar")
    public String guardarMiembro(@Valid @ModelAttribute("miembroDTO") MiembroDTO miembroDTO,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (miembroDTO.getMembresiaId() != null) {
            boolean hasPaymentData = miembroDTO.getNumeroTarjeta() != null && !miembroDTO.getNumeroTarjeta().isEmpty() &&
                    miembroDTO.getFechaVencimiento() != null && !miembroDTO.getFechaVencimiento().isEmpty() &&
                    miembroDTO.getCvv() != null && !miembroDTO.getCvv().isEmpty();

            if (!hasPaymentData) {
                if (miembroDTO.getNumeroTarjeta() == null || miembroDTO.getNumeroTarjeta().isEmpty()) {
                    result.addError(new FieldError("miembroDTO", "numeroTarjeta", "El número de tarjeta es obligatorio para el pago inmediato."));
                }
                if (miembroDTO.getFechaVencimiento() == null || miembroDTO.getFechaVencimiento().isEmpty()) {
                    result.addError(new FieldError("miembroDTO", "fechaVencimiento", "La fecha de vencimiento es obligatoria para el pago inmediato."));
                }
                if (miembroDTO.getCvv() == null || miembroDTO.getCvv().isEmpty()) {
                    result.addError(new FieldError("miembroDTO", "cvv", "El CVV es obligatorio para el pago inmediato."));
                }
            } else {
                try {
                    String[] parts = miembroDTO.getFechaVencimiento().split("/");
                    int month = Integer.parseInt(parts[0]);
                    int year = 2000 + Integer.parseInt(parts[1]);

                    LocalDate expiryDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
                    if (expiryDate.isBefore(LocalDate.now())) {
                        result.addError(new FieldError("miembroDTO", "fechaVencimiento", "La tarjeta ha expirado."));
                    }
                } catch (Exception e) {
                    result.addError(new FieldError("miembroDTO", "fechaVencimiento", "Formato de fecha de vencimiento inválido (MM/AA)."));
                }
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("modoEdicion", miembroDTO.getId() != null);
            model.addAttribute("sexoOpciones", Arrays.asList(Sexo.values()));
            model.addAttribute("membresiasDisponibles", membresiaService.obtenerMembresiasActivas());
            model.addAttribute("error", "Por favor, corrige los errores en el formulario.");
            return "crear-editar-miembro";
        }

        try {
            Miembro miembro;
            InscripcionMembresia inscripcionProcesada = null;
            boolean isNewMiembro = (miembroDTO.getId() == null);

            if (isNewMiembro) {
                miembro = new Miembro();
                miembro.setTipoIdentificacion(miembroDTO.getTipoIdentificacion());
                miembro.setNumeroIdentificacion(miembroDTO.getNumeroIdentificacion());
                miembro.setNombre(miembroDTO.getNombre());
                miembro.setApellido(miembroDTO.getApellido());
                miembro.setCorreo(miembroDTO.getCorreo());
                miembro.setTelefono(miembroDTO.getTelefono());
                miembro.setSexo(miembroDTO.getSexo());
                miembro.setFechaNacimiento(miembroDTO.getFechaNacimiento());
                miembro.setContrasena(miembroDTO.getContrasena());
                miembro.setActivo(miembroDTO.getActivo() != null ? miembroDTO.getActivo() : true);
                miembro.setFechaRegistro(java.time.LocalDateTime.now());
                miembro.setRol("MIEMBRO");

                miembro = miembroService.registrarNuevoMiembro(miembro);
                redirectAttributes.addFlashAttribute("success", "Miembro '" + miembro.getNombre() + " " + miembro.getApellido() + "' registrado exitosamente.");

            } else {
                miembro = miembroService.obtenerMiembroPorId(miembroDTO.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado para actualización."));

                miembro.setTipoIdentificacion(miembroDTO.getTipoIdentificacion());
                miembro.setNumeroIdentificacion(miembroDTO.getNumeroIdentificacion());
                miembro.setNombre(miembroDTO.getNombre());
                miembro.setApellido(miembroDTO.getApellido());
                miembro.setCorreo(miembroDTO.getCorreo());
                miembro.setTelefono(miembroDTO.getTelefono());
                miembro.setSexo(miembroDTO.getSexo());
                miembro.setFechaNacimiento(miembroDTO.getFechaNacimiento());
                miembro.setActivo(miembroDTO.getActivo());

                if (miembroDTO.getContrasena() != null && !miembroDTO.getContrasena().isEmpty()) {
                    miembro.setContrasena(miembroDTO.getContrasena());
                }
                miembro = miembroService.actualizarMiembro(miembro);
                redirectAttributes.addFlashAttribute("success", "Miembro '" + miembro.getNombre() + " " + miembro.getApellido() + "' actualizado exitosamente.");
            }

            if (miembroDTO.getMembresiaId() != null) {
                boolean paymentDataProvided = (miembroDTO.getNumeroTarjeta() != null && !miembroDTO.getNumeroTarjeta().isEmpty());

                if (paymentDataProvided) {
                    inscripcionProcesada = inscripcionMembresiaService.crearOActualizarInscripcionConPago(
                            miembro.getId(),
                            miembroDTO.getMembresiaId(),
                            miembroDTO.getNumeroTarjeta(),
                            miembroDTO.getFechaVencimiento(),
                            miembroDTO.getCvv()
                    );
                    if ("COMPLETADO".equals(inscripcionProcesada.getEstado())) {
                        redirectAttributes.addFlashAttribute("success", "Miembro '" + miembro.getNombre() + " " + miembro.getApellido() + "' registrado/actualizado y membresía pagada exitosamente. ID Inscripción: " + inscripcionProcesada.getId());
                    } else {
                        redirectAttributes.addFlashAttribute("info", "Membresía asignada en estado '" + inscripcionProcesada.getEstado() + "'. Si el pago fue procesado, verifique el estado.");
                    }
                } else {
                    inscripcionProcesada = inscripcionMembresiaService.crearInscripcionInicial(
                            miembro.getId(),
                            miembroDTO.getMembresiaId()
                    );
                    redirectAttributes.addFlashAttribute("info", "Membresía asignada a " + miembro.getNombre() + " en estado 'PENDIENTE_PAGO'. ID Inscripción: " + inscripcionProcesada.getId());
                }
            } else {
                redirectAttributes.addFlashAttribute("info", "No se seleccionó membresía para el miembro " + miembro.getNombre() + ". Solo se guardaron los datos básicos.");
            }

            return "redirect:/admin/miembros/ver";

        } catch (IllegalArgumentException e) {
            result.addError(new FieldError("miembroDTO", "global", e.getMessage()));
            model.addAttribute("error", e.getMessage());
            model.addAttribute("modoEdicion", miembroDTO.getId() != null);
            model.addAttribute("sexoOpciones", Arrays.asList(Sexo.values()));
            model.addAttribute("membresiasDisponibles", membresiaService.obtenerMembresiasActivas());
            return "crear-editar-miembro";
        } catch (Exception e) {
            e.printStackTrace();
            result.addError(new FieldError("miembroDTO", "global", "Error interno al guardar el miembro: " + e.getMessage()));
            model.addAttribute("error", "Error interno al guardar el miembro: " + e.getMessage());
            model.addAttribute("modoEdicion", miembroDTO.getId() != null);
            model.addAttribute("sexoOpciones", Arrays.asList(Sexo.values()));
            model.addAttribute("membresiasDisponibles", membresiaService.obtenerMembresiasActivas());
            return "crear-editar-miembro";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMiembro(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            miembroService.eliminarMiembro(id);
            redirectAttributes.addFlashAttribute("success", "Miembro eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al eliminar el miembro: " + e.getMessage());
        }
        return "redirect:/admin/miembros/ver";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstadoMiembro(@PathVariable Long id, @RequestParam boolean activo, RedirectAttributes redirectAttributes) {
        try {
            miembroService.cambiarEstadoActivoMiembro(id, activo);
            redirectAttributes.addFlashAttribute("success", "Estado del miembro actualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado del miembro: " + e.getMessage());
        }
        return "redirect:/admin/miembros/ver";
    }

    @GetMapping("/historial-inscripciones/{miembroId}")
    public String verHistorialInscripciones(@PathVariable Long miembroId, Model model, RedirectAttributes redirectAttributes) {
        return miembroService.obtenerMiembroPorId(miembroId)
                .map(miembro -> {
                    List<InscripcionMembresia> historial = inscripcionMembresiaService.obtenerInscripcionesPorMiembro(miembroId);
                    Optional<InscripcionMembresia> membresiaActiva = inscripcionMembresiaService.obtenerMembresiaActivaPorMiembro(miembroId);
                    Optional<InscripcionMembresia> membresiaPendiente = inscripcionMembresiaService.obtenerInscripcionPendientePorMiembro(miembroId);

                    model.addAttribute("miembro", miembro);
                    model.addAttribute("historialInscripciones", historial);
                    model.addAttribute("membresiaActiva", membresiaActiva.orElse(null));
                    model.addAttribute("membresiaPendiente", membresiaPendiente.orElse(null));
                    model.addAttribute("membresiasDisponibles", membresiaService.obtenerMembresiasActivas());
                    return "historial-inscripciones";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Miembro no encontrado para ver historial de inscripciones.");
                    return "redirect:/admin/miembros/ver";
                });
    }

    @PostMapping("/inscripciones/crear/{miembroId}")
    public String crearNuevaInscripcion(@PathVariable Long miembroId,
                                        @RequestParam Long membresiaId,
                                        RedirectAttributes redirectAttributes) {
        try {
            InscripcionMembresia nuevaInscripcion = inscripcionMembresiaService.crearInscripcionInicial(miembroId, membresiaId);
            redirectAttributes.addFlashAttribute("success", "Nueva inscripción creada como 'PENDIENTE_PAGO' (ID: " + nuevaInscripcion.getId() + ").");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear nueva inscripción: " + e.getMessage());
        }
        return "redirect:/admin/miembros/historial-inscripciones/" + miembroId;
    }

    @PostMapping("/inscripciones/completar-pago/{inscripcionId}")
    public String completarPagoInscripcion(@PathVariable Long inscripcionId,
                                           @RequestParam BigDecimal montoPagado,
                                           RedirectAttributes redirectAttributes) {
        Optional<InscripcionMembresia> inscripcionOpt = inscripcionMembresiaService.obtenerInscripcionPorId(inscripcionId);
        if (inscripcionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Inscripción no encontrada.");
            return "redirect:/admin/miembros/ver";
        }
        Long miembroId = inscripcionOpt.get().getMiembro().getId();

        try {
            inscripcionMembresiaService.completarPagoInscripcion(inscripcionId, montoPagado);
            redirectAttributes.addFlashAttribute("success", "Pago completado y membresía activada exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al completar el pago: " + e.getMessage());
        }
        return "redirect:/admin/miembros/historial-inscripciones/" + miembroId;
    }

    @PostMapping("/inscripciones/cambiar-estado/{inscripcionId}")
    public String cambiarEstadoInscripcion(@PathVariable Long inscripcionId,
                                           @RequestParam String nuevoEstado,
                                           RedirectAttributes redirectAttributes) {
        Optional<InscripcionMembresia> inscripcionOpt = inscripcionMembresiaService.obtenerInscripcionPorId(inscripcionId);
        if (inscripcionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Inscripción no encontrada.");
            return "redirect:/admin/miembros/ver";
        }
        Long miembroId = inscripcionOpt.get().getMiembro().getId();

        try {
            inscripcionMembresiaService.actualizarEstadoInscripcion(inscripcionId, nuevoEstado);
            redirectAttributes.addFlashAttribute("success", "Estado de la inscripción actualizado a '" + nuevoEstado + "'.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado de la inscripción: " + e.getMessage());
        }
        return "redirect:/admin/miembros/historial-inscripciones/" + miembroId;
    }
}