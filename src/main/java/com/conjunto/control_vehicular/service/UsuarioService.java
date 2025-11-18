package com.conjunto.control_vehicular.service;

import com.conjunto.control_vehicular.dto.CrearUsuarioRequest;
import com.conjunto.control_vehicular.dto.UsuarioResponse;
import com.conjunto.control_vehicular.entity.Usuario;
import com.conjunto.control_vehicular.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponse crearUsuario(CrearUsuarioRequest request) {
        log.info("Creando usuario nuevo: {}", request.getUsername());

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        // Mapear roles desde la lista de Strings al Enum de tu entidad
        // Asumo que tienes algo como: public enum Rol { ROLE_ADMIN, ROLE_GUARDA, ROLE_SUPERVISOR }
        Set<Usuario.Rol> roles = request.getRoles().stream()
                .map(rolStr -> {
                    try {
                        return Usuario.Rol.valueOf(rolStr);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Rol inválido: " + rolStr);
                    }
                })
                .collect(Collectors.toSet());

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setDocumento(request.getDocumento());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setActivo(request.getActivo() == null ? true : request.getActivo());
        usuario.setBloqueado(false);
        usuario.setIntentosFallidos(0);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuario.setRoles(roles);

        Usuario guardado = usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .id(guardado.getId())
                .username(guardado.getUsername())
                .nombreCompleto(guardado.getNombreCompleto())
                .documento(guardado.getDocumento())
                .email(guardado.getEmail())
                .telefono(guardado.getTelefono())
                .activo(guardado.getActivo())
                .bloqueado(guardado.getBloqueado())
                .roles(
                        guardado.getRoles().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
