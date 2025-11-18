package com.conjunto.control_vehicular.repository;
import com.conjunto.control_vehicular.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ========== UsuarioRepository ==========
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByDocumento(String documento);

    List<Usuario> findByActivoTrue();

    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :rol AND u.activo = true")
    List<Usuario> findByRol(@Param("rol") Usuario.Rol rol);

    boolean existsByUsername(String username);

    boolean existsByDocumento(String documento);
}