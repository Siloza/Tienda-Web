package com.spring.tiendaweb.tiendawebapp.Repository;

import com.spring.tiendaweb.tiendawebapp.Entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    
}