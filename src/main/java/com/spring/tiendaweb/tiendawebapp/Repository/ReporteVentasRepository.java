package com.spring.tiendaweb.tiendawebapp.Repository;

import com.spring.tiendaweb.tiendawebapp.Document.ReporteVentas;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 🗃️ REPOSITORIO MONGODB: ReporteVentas
 * 
 * Proporciona métodos para consultar y gestionar
 * los reportes consolidados almacenados en MongoDB
 */
@Repository
public interface ReporteVentasRepository extends MongoRepository<ReporteVentas, String> {

    // ✅ Obtener el reporte más reciente
    Optional<ReporteVentas> findFirstByOrderByFechaReporteDesc();

    // ✅ Obtener reportes válidos ordenados por fecha
    List<ReporteVentas> findByEsReporteValidoTrueOrderByFechaReporteDesc();

    // ✅ Obtener todos los reportes ordenados por fecha
    List<ReporteVentas> findAllByOrderByFechaReporteDesc();

    // ✅ Buscar reportes en rango de fechas
    List<ReporteVentas> findByFechaReporteBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // ✅ Buscar reportes por título
    List<ReporteVentas> findByTituloReporteContainingIgnoreCase(String titulo);

    // ✅ Contar reportes válidos
    long countByEsReporteValidoTrue();

    // ✅ Obtener reportes de hoy
    @Query("{ 'fechaReporte': { $gte: ?0, $lt: ?1 }, 'esReporteValido': true }")
    List<ReporteVentas> findReportesDeHoy(LocalDateTime inicioDia, LocalDateTime finDia);

    // ✅ Eliminar reportes antiguos (más de 30 días)
    @Query(value = "{ 'fechaReporte': { $lt: ?0 } }", delete = true)
    void deleteReportesAntiguos(LocalDateTime fechaLimite);
}