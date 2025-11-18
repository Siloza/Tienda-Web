package com.spring.tiendaweb.tiendawebapp.Repository;

import com.spring.tiendaweb.tiendawebapp.Entity.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    
}