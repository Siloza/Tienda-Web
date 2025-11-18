package com.spring.tiendaweb.tiendawebapp.Repository;

import com.spring.tiendaweb.tiendawebapp.Document.AnalyticsVentas;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 🗃️ REPOSITORIO MONGODB: AnalyticsVentas
 * 
 * Proporciona métodos para consultar y gestionar
 * los analytics individuales almacenados en MongoDB
 */
@Repository
public interface AnalyticsVentasRepository extends MongoRepository<AnalyticsVentas, String> {

    // ✅ Obtener analytics válidos
    List<AnalyticsVentas> findByEsValidoTrue();

    // ✅ Obtener por tipo de consulta
    List<AnalyticsVentas> findByTipoConsultaAndEsValidoTrue(String tipoConsulta);

    // ✅ Obtener por periodo
    List<AnalyticsVentas> findByPeriodoAndEsValidoTrue(String periodo);

    // ✅ Obtener por tipo y periodo
    List<AnalyticsVentas> findByTipoConsultaAndPeriodoAndEsValidoTrue(String tipoConsulta, String periodo);

    // ✅ Obtener analytics en rango de fechas
    List<AnalyticsVentas> findByFechaConsultaBetweenAndEsValidoTrue(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // ✅ Contar analytics válidos
    @Query("{ 'esValido': true }")
    long countByEsValidoTrue();

    // ✅ Encontrar el más reciente por tipo
    AnalyticsVentas findFirstByTipoConsultaAndEsValidoTrueOrderByFechaConsultaDesc(String tipoConsulta);

    // ✅ Eliminar analytics inválidos (limpieza)
    void deleteByEsValidoFalse();
}