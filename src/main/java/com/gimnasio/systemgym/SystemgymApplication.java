package com.gimnasio.systemgym;

import com.gimnasio.systemgym.model.Usuario;
import com.gimnasio.systemgym.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class SystemgymApplication {

	private static final Logger log = LoggerFactory.getLogger(SystemgymApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SystemgymApplication.class, args);
	}

	@Bean
	public CommandLineRunner initAdminUser(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepository.findByUsername("admin").isEmpty()) {
				log.info("Creando usuario administrador...");
				String encodedPassword = passwordEncoder.encode("123456789");

				Usuario admin = new Usuario(
						null,
						"admin",
						encodedPassword,
						"ADMIN",
						"Admin",
						"General",
						"admin@systemgym.com",
						true
				);
				usuarioRepository.save(admin);
				log.info("Usuario administrador creado: " + admin.getUsername());
			} else {
				log.info("El usuario administrador ya existe. No se creará de nuevo.");
			}
		};
	}
}