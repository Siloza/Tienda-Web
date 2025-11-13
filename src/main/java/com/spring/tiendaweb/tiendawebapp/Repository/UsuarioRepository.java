package com.spring.tiendaweb.tiendawebapp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import com.spring.tiendaweb.tiendawebapp.Entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    Optional<Usuario> findByUsuario(String usuario);
    Optional<Usuario> findByEmail(String email);      
    boolean existsByEmail(String email);
    boolean existsByUsuario(String usuario);

}
