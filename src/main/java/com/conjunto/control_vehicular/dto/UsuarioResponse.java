package com.conjunto.control_vehicular.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String username;
    private String nombreCompleto;
    private String documento;
    private String email;
    private String telefono;
    private Boolean activo;
    private Boolean bloqueado;
    private List<String> roles;
}
