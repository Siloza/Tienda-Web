package com.spring.tiendaweb.tiendawebapp.Repository;

import com.spring.tiendaweb.tiendawebapp.Entity.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Productos, Long> {
    
    Optional<Productos> findByNombre(String nombre);
    
}