package com.conjunto.control_vehicular.controller;

import com.conjunto.control_vehicular.dto.ApiResponse;
import com.conjunto.control_vehicular.dto.CrearUsuarioRequest;
import com.conjunto.control_vehicular.dto.UsuarioResponse;
import com.conjunto.control_vehicular.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "Gestión de usuarios y roles")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario con sus roles. Solo puede ser usado por ROLE_ADMIN."
    )
    public ResponseEntity<ApiResponse<UsuarioResponse>> crearUsuario(
            @Valid @RequestBody CrearUsuarioRequest request) {

        try {
            UsuarioResponse response = usuarioService.crearUsuario(request);
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Usuario creado exitosamente")
            );
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al crear usuario: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("Error al crear usuario: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("Error interno al crear usuario", null)
            );
        }
    }
}
