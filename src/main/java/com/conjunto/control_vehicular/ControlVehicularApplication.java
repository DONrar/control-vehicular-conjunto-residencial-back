package com.conjunto.control_vehicular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(
		info = @Info(
				title = "Sistema Control Vehicular QR",
				version = "1.0.0",
				description = "API REST para control de acceso vehicular en conjuntos residenciales mediante códigos QR",
				contact = @Contact(
						name = "Equipo Desarrollo",
						email = "desarrollo@conjunto.com"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://www.apache.org/licenses/LICENSE-2.0.html"
				)
		),
		servers = {
				@Server(
						url = "http://localhost:8080",
						description = "Servidor Local"
				),
				@Server(
						url = "https://api.conjunto.com",
						description = "Servidor Producción"
				)
		}
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT",
		description = "Ingrese el token JWT obtenido del endpoint /api/v1/auth/login"
)
public class ControlVehicularApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlVehicularApplication.class, args);

		System.out.println("\n" +
				"=================================================\n" +
				"  SISTEMA CONTROL VEHICULAR QR - INICIADO ✓\n" +
				"=================================================\n" +
				"  API REST: http://localhost:8080\n" +
				"  Swagger UI: http://localhost:8080/swagger-ui.html\n" +
				"  API Docs: http://localhost:8080/v3/api-docs\n" +
				"=================================================\n" +
				"  Usuarios de prueba:\n" +
				"  - admin / admin123 (ADMIN)\n" +
				"  - guarda1 / guarda123 (GUARDA)\n" +
				"  - guarda2 / guarda123 (GUARDA)\n" +
				"  - supervisor / super123 (SUPERVISOR)\n" +
				"=================================================\n"
		);
	}
}