package com.conjunto.control_vehicular.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CrearUsuarioRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank
    private String nombreCompleto;

    @NotBlank
    private String documento;

    @Email
    private String email;

    private String telefono;

    // Ej: ["ROLE_ADMIN", "ROLE_GUARDA"]
    @Size(min = 1, message = "Debe asignar al menos un rol")
    private List<String> roles;

    // Opcional: por defecto lo dejamos activo si viene null
    private Boolean activo;
}
