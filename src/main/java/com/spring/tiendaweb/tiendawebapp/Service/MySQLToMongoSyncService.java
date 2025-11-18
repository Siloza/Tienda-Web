package com.spring.tiendaweb.tiendawebapp.Service;

import com.spring.tiendaweb.tiendawebapp.Document.AnalyticsVentas;
import com.spring.tiendaweb.tiendawebapp.Document.ReporteVentas;
import com.spring.tiendaweb.tiendawebapp.Repository.AnalyticsVentasRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.OrdenRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.ReporteVentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🔄 SERVICIO DE SINCRONIZACIÓN MySQL → MongoDB
 * 
 * Conecta los datos transaccionales de MySQL
 * con el almacenamiento de analytics en MongoDB
 */
@Service
public class MySQLToMongoSyncService {

    @Autowired
    private AnalyticsVentasRepository analyticsRepository;

    @Autowired
    private ReporteVentasRepository reporteRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private AnalisisVentasService analisisVentasService;

    /**
     * 🚀 SINCRONIZACIÓN COMPLETA
     */
    @Transactional
    public void sincronizarVentasCompleta() {
        try {
            // 1. Invalidar analytics anteriores
            invalidarAnalyticsAnteriores();
            
            // 2. Calcular nuevos analytics desde MySQL
            Map<String, Object> nuevosAnalytics = calcularAnalyticsDesdeMySQL();
            
            // 3. Guardar en MongoDB
            guardarAnalyticsEnMongoDB(nuevosAnalytics);
            
            // 4. Generar reporte consolidado
            generarReporteConsolidado(nuevosAnalytics);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 📊 CALCULAR ANALYTICS DESDE MYSQL
     */
    private Map<String, Object> calcularAnalyticsDesdeMySQL() {
        Map<String, Object> analytics = new HashMap<>();
        
        try {
            // 1. Total de ventas
            Double totalVentas = analisisVentasService.getTotalVentasGeneral();
            analytics.put("totalVentas", totalVentas != null ? totalVentas : 0.0);
            
            // 2. Ventas por categoría
            Map<String, Object> ventasPorCategoria = analisisVentasService.getVentasPorCategoria();
            analytics.put("ventasPorCategoria", ventasPorCategoria != null ? ventasPorCategoria : new HashMap<>());
            
            // 3. Análisis temporal
            Map<String, Double> analisisTemporal = analisisVentasService.getAnalisisTemporal();
            analytics.put("analisisTemporal", analisisTemporal != null ? analisisTemporal : new HashMap<>());
            
            // 4. Top productos
            List<Object[]> topProductos = analisisVentasService.getTopProductos();
            analytics.put("topProductos", topProductos != null ? topProductos : List.of());
            
        } catch (Exception e) {
            // Valores por defecto en caso de error
            analytics.put("totalVentas", 0.0);
            analytics.put("ventasPorCategoria", new HashMap<>());
            analytics.put("analisisTemporal", new HashMap<>());
            analytics.put("topProductos", List.of());
        }
        
        return analytics;
    }

    /**
     * 💾 GUARDAR ANALYTICS EN MONGODB
     */
    private void guardarAnalyticsEnMongoDB(Map<String, Object> analytics) {
        
        // Guardar total de ventas
        AnalyticsVentas totalVentasDoc = new AnalyticsVentas(
            "total_ventas", 
            "global", 
            analytics.get("totalVentas"),
            "Calculado desde MySQL"
        );
        analyticsRepository.save(totalVentasDoc);
        
        // Guardar ventas por categoría
        AnalyticsVentas categoriaDoc = new AnalyticsVentas(
            "ventas_por_categoria", 
            "global", 
            analytics.get("ventasPorCategoria"),
            "Agrupación por tipo de prenda"
        );
        analyticsRepository.save(categoriaDoc);
        
        // Guardar análisis temporal
        @SuppressWarnings("unchecked")
        Map<String, Double> temporal = (Map<String, Double>) analytics.get("analisisTemporal");
        
        temporal.forEach((periodo, valor) -> {
            AnalyticsVentas temporalDoc = new AnalyticsVentas(
                "ventas_temporales", 
                periodo.toLowerCase(), 
                valor != null ? valor : 0.0,
                "Análisis temporal automático"
            );
            analyticsRepository.save(temporalDoc);
        });
        
        // Guardar top productos
        AnalyticsVentas topProductosDoc = new AnalyticsVentas(
            "top_productos", 
            "ranking", 
            analytics.get("topProductos"),
            "Ranking de productos más vendidos"
        );
        analyticsRepository.save(topProductosDoc);
        
        System.out.println("💾 Analytics guardados en MongoDB");
    }

    /**
     * 📄 GENERAR REPORTE CONSOLIDADO
     */
    @SuppressWarnings("unchecked")
    private void generarReporteConsolidado(Map<String, Object> analytics) {
        
        ReporteVentas reporte = new ReporteVentas("Reporte Automático - " + LocalDateTime.now());
        
        // Datos principales
        reporte.setTotalVentasGlobal((Double) analytics.get("totalVentas"));
        reporte.setVentasPorCategoria((Map<String, Object>) analytics.get("ventasPorCategoria"));
        
        // Análisis temporal
        Map<String, Double> temporal = (Map<String, Double>) analytics.get("analisisTemporal");
        reporte.setVentasDiarias(temporal.getOrDefault("ventasDelDia", 0.0));
        reporte.setVentasSemanales(temporal.getOrDefault("ventasDeLaSemana", 0.0));
        reporte.setVentasMensuales(temporal.getOrDefault("ventasDelMes", 0.0));
        reporte.setVentasAnuales(temporal.getOrDefault("ventasDelAnio", 0.0));
        
        // Datos para gráficas temporales
        List<Object[]> ventasDiarias = (List<Object[]>) analytics.get("ventasDiarias");
        List<Object[]> ventasMensuales = (List<Object[]>) analytics.get("ventasMensuales");
        List<Object[]> ventasAnuales = (List<Object[]>) analytics.get("ventasAnuales");
        
        reporte.setDatosDiarios(convertirDatosTemporales(ventasDiarias));
        reporte.setDatosMensuales(convertirDatosTemporales(ventasMensuales));
        reporte.setDatosAnuales(convertirDatosTemporales(ventasAnuales));
        
        // Top productos (convertir a formato compatible)
        List<Object[]> topProductosArray = (List<Object[]>) analytics.get("topProductos");
        List<Map<String, Object>> topProductosMap = convertirTopProductos(topProductosArray);
        reporte.setTopProductos(topProductosMap);
        
        // Metadatos calculados
        calcularMetadatos(reporte, analytics);
        
        // Guardar el reporte
        reporteRepository.save(reporte);
    }

    /**
     * 🔄 CONVERTIR TOP PRODUCTOS
     */
    private List<Map<String, Object>> convertirTopProductos(List<Object[]> topProductosArray) {
        return topProductosArray.stream().map(producto -> {
            Map<String, Object> productoMap = new HashMap<>();
            if (producto != null && producto.length >= 3) {
                productoMap.put("nombre", producto[0] != null ? producto[0] : "Sin nombre");
                productoMap.put("cantidad", producto[1] != null ? producto[1] : 0);
                productoMap.put("totalVentas", producto[2] != null ? producto[2] : 0.0);
            }
            return productoMap;
        }).toList();
    }

    /**
     * 📊 CONVERTIR DATOS TEMPORALES PARA GRÁFICAS
     */
    private List<Map<String, Object>> convertirDatosTemporales(List<Object[]> datosArray) {
        if (datosArray == null) return List.of();
        
        return datosArray.stream().map(dato -> {
            Map<String, Object> datoMap = new HashMap<>();
            if (dato != null && dato.length >= 3) {
                datoMap.put("fecha", dato[0] != null ? dato[0].toString() : "");
                datoMap.put("ventas", dato[1] != null ? dato[1] : 0.0);
                datoMap.put("cantidad", dato[2] != null ? dato[2] : 0);
            }
            return datoMap;
        }).toList();
    }

    /**
     * 📊 CALCULAR METADATOS
     */
    @SuppressWarnings("unchecked")
    private void calcularMetadatos(ReporteVentas reporte, Map<String, Object> analytics) {
        
        // Total de órdenes real desde la base de datos
        Long totalOrdenesReal = ordenRepository.count();
        reporte.setTotalOrdenes(totalOrdenesReal);
        
        // Promedio de venta
        Double totalVentas = (Double) analytics.get("totalVentas");
        Double promedioVenta = (totalVentas > 0 && totalOrdenesReal > 0) ? totalVentas / totalOrdenesReal : 0.0;
        reporte.setPromedioVenta(promedioVenta);
        
        // Categoría más vendida
        Map<String, Object> ventasPorCategoria = (Map<String, Object>) analytics.get("ventasPorCategoria");
        String categoriaMasVendida = encontrarCategoriaMasVendida(ventasPorCategoria);
        reporte.setCategoriaMasVendida(categoriaMasVendida);
    }

    /**
     * 🏆 ENCONTRAR CATEGORÍA MÁS VENDIDA
     */
    private String encontrarCategoriaMasVendida(Map<String, Object> ventasPorCategoria) {
        if (ventasPorCategoria == null || ventasPorCategoria.isEmpty()) {
            return "N/A";
        }
        
        String categoriaMasVendida = "N/A";
        double maxVentas = 0.0;
        
        for (Map.Entry<String, Object> entry : ventasPorCategoria.entrySet()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> detalleCategoria = (Map<String, Object>) entry.getValue();
                Object totalVentasObj = detalleCategoria.get("totalVentas");
                
                if (totalVentasObj instanceof Number) {
                    double totalVentas = ((Number) totalVentasObj).doubleValue();
                    if (totalVentas > maxVentas) {
                        maxVentas = totalVentas;
                        categoriaMasVendida = entry.getKey();
                    }
                }
            } catch (Exception e) {
                // Error procesando categoría
            }
        }
        
        return categoriaMasVendida;
    }

    /**
     * ❌ INVALIDAR ANALYTICS ANTERIORES
     */
    private void invalidarAnalyticsAnteriores() {
        List<AnalyticsVentas> analyticsAnteriores = analyticsRepository.findByEsValidoTrue();
        analyticsAnteriores.forEach(analytics -> analytics.setEsValido(false));
        analyticsRepository.saveAll(analyticsAnteriores);
    }

    /**
     * 🎯 MÉTODOS PÚBLICOS
     */
    public String sincronizarManualmente() {
        sincronizarVentasCompleta();
        return "Sincronización completada exitosamente a las " + LocalDateTime.now();
    }

    public ReporteVentas obtenerUltimoReporte() {
        return reporteRepository.findFirstByOrderByFechaReporteDesc()
                .orElse(new ReporteVentas("No hay reportes disponibles"));
    }

    public long contarAnalyticsValidos() {
        return analyticsRepository.countByEsValidoTrue();
    }
}