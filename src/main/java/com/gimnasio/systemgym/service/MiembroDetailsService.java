package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.repository.MiembroRepository;
import com.gimnasio.systemgym.service.MiembroDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MiembroDetailsService implements UserDetailsService {

    private final MiembroRepository miembroRepository;

    public MiembroDetailsService(MiembroRepository miembroRepository) {
        this.miembroRepository = miembroRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String numeroIdentificacion) throws UsernameNotFoundException {
        Miembro miembro = miembroRepository.findByNumeroIdentificacion(numeroIdentificacion)
                .orElseThrow(() -> new UsernameNotFoundException("Miembro no encontrado con número de identificación: " + numeroIdentificacion));

        return new MiembroDetails(miembro);
    }
}