package com.conjunto.control_vehicular.controller;

import com.conjunto.control_vehicular.dto.ApiResponse;
import com.conjunto.control_vehicular.dto.*;
import com.conjunto.control_vehicular.security.JwtUtil;
import com.conjunto.control_vehicular.entity.Usuario;
import com.conjunto.control_vehicular.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints para login y gestión de autenticación")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    /**
     * Login - Endpoint para autenticar guardas
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Autentica un usuario (guarda) y retorna un token JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsername());

        try {
            // Autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Obtener usuario
            Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar que esté activo
            if (!usuario.getActivo()) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Usuario inactivo", null)
                );
            }

            if (usuario.getBloqueado()) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Usuario bloqueado por múltiples intentos fallidos", null)
                );
            }

            // Generar token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Actualizar último acceso
            usuario.registrarAccesoExitoso();
            usuarioRepository.save(usuario);

            // Preparar respuesta
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tipo("Bearer")
                    .username(usuario.getUsername())
                    .nombreCompleto(usuario.getNombreCompleto())
                    .roles(usuario.getRoles().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList()))
                    .build();

            log.info("Login exitoso para usuario: {}", request.getUsername());
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Login exitoso")
            );

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());

            // Registrar intento fallido
            usuarioRepository.findByUsername(request.getUsername())
                    .ifPresent(usuario -> {
                        usuario.registrarAccesoFallido();
                        usuarioRepository.save(usuario);
                    });

            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Credenciales inválidas", null)
            );
        }
    }

    /**
     * Validar token
     */
    @GetMapping("/validate")
    @Operation(summary = "Validar token",
            description = "Verifica si un token JWT es válido")
    public ResponseEntity<ApiResponse<ValidateTokenResponse>> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);

                    ValidateTokenResponse response = ValidateTokenResponse.builder()
                            .valido(true)
                            .username(username)
                            .build();

                    return ResponseEntity.ok(
                            ApiResponse.success(response, "Token válido")
                    );
                }
            }

            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Token inválido o expirado", null)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error validando token", null)
            );
        }
    }

    /**
     * Refresh token (renovar token)
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar token",
            description = "Genera un nuevo token JWT a partir de uno válido existente")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);

                Usuario usuario = usuarioRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                if (jwtUtil.validateToken(token, usuario)) {
                    String nuevoToken = jwtUtil.generateToken(usuario);

                    LoginResponse response = LoginResponse.builder()
                            .token(nuevoToken)
                            .tipo("Bearer")
                            .username(usuario.getUsername())
                            .nombreCompleto(usuario.getNombreCompleto())
                            .roles(usuario.getRoles().stream()
                                    .map(Enum::name)
                                    .collect(Collectors.toList()))
                            .build();

                    return ResponseEntity.ok(
                            ApiResponse.success(response, "Token renovado exitosamente")
                    );
                }
            }

            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Token inválido", null)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error renovando token", null)
            );
        }
    }
}



