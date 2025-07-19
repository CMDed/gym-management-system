package com.gimnasio.systemgym.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CombinedUserDetailsService implements UserDetailsService {

    private final MiembroDetailsService miembroDetailsService;
    private final UsuarioDetailsService usuarioDetailsService;

    public CombinedUserDetailsService(MiembroDetailsService miembroDetailsService, UsuarioDetailsService usuarioDetailsService) {
        this.miembroDetailsService = miembroDetailsService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return miembroDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException eMiembro) {
            try {
                return usuarioDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException eUsuario) {
                throw new UsernameNotFoundException("Usuario no encontrado: " + username);
            }
        }
    }
}