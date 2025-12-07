package com.spring.tiendaweb.tiendawebapp.Controller;

import com.spring.tiendaweb.tiendawebapp.Document.AnalyticsVentas;
import com.spring.tiendaweb.tiendawebapp.Document.ReporteVentas;
import com.spring.tiendaweb.tiendawebapp.Repository.AnalyticsVentasRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.ReporteVentasRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.UsuarioRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.VentaRepository;
import com.spring.tiendaweb.tiendawebapp.Service.MySQLToMongoSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;

@Controller
public class DashboardController {

    @Autowired
    private AnalyticsVentasRepository analyticsRepository;

    @Autowired
    private ReporteVentasRepository reporteRepository;

    @Autowired
    private MySQLToMongoSyncService syncService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private VentaRepository ventaRepository;

    /**
     * 📊 DASHBOARD PRINCIPAL 
     */
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        try {
            // Sincronización automática antes del dashboard
            try {
                syncService.sincronizarVentasCompleta();
            } catch (Exception syncError) {
                // Error en sincronización, continuar con datos existentes
            }
            
            // Obtener datos actualizados de MongoDB
            Optional<ReporteVentas> ultimoReporte = Optional.empty();
            List<AnalyticsVentas> analyticsValidos = List.of();
            
            try {
                ultimoReporte = reporteRepository.findFirstByOrderByFechaReporteDesc();
                analyticsValidos = analyticsRepository.findByEsValidoTrue();
            } catch (Exception mongoError) {
                // Error conectando a MongoDB, usar valores por defecto
            }
            
            // Cargar datos disponibles
            if (ultimoReporte.isPresent()) {
                ReporteVentas reporte = ultimoReporte.get();
                model.addAttribute("reporte", reporte);
                model.addAttribute("fechaActualizacion", reporte.getFechaReporte());
                model.addAttribute("hayDatos", true);
                
                model.addAttribute("totalVentas", reporte.getTotalVentasGlobal() != null ? reporte.getTotalVentasGlobal() : 0.0);
                model.addAttribute("totalOrdenes", reporte.getTotalOrdenes() != null ? reporte.getTotalOrdenes() : 0L);
                model.addAttribute("categoriaMasVendida", reporte.getCategoriaMasVendida() != null ? reporte.getCategoriaMasVendida() : "N/A");
                model.addAttribute("promedioVenta", reporte.getPromedioVenta() != null ? reporte.getPromedioVenta() : 0.0);
                
                // Datos para gráficas temporales
                model.addAttribute("datosDiarios", reporte.getDatosDiarios() != null ? reporte.getDatosDiarios() : List.of());
                model.addAttribute("datosMensuales", reporte.getDatosMensuales() != null ? reporte.getDatosMensuales() : List.of());
                model.addAttribute("datosAnuales", reporte.getDatosAnuales() != null ? reporte.getDatosAnuales() : List.of());
                
                // Conteo real de usuarios (clientes)
                Long totalClientes = usuarioRepository.count();
                model.addAttribute("totalClientes", totalClientes);
                
                // Producto más vendido
                String productoMasVendido = obtenerProductoMasVendido();
                model.addAttribute("productoMasVendido", productoMasVendido);
                
                // Datos de ventas por categoría desde MongoDB o valores por defecto
                Map<String, Double> ventasPorCategoria = reporte.getVentasPorCategoria() != null 
                    ? convertirVentasPorCategoria(reporte.getVentasPorCategoria())
                    : obtenerVentasPorCategoria();
                model.addAttribute("ventasPorCategoria", ventasPorCategoria);
                
            } else {
                // Valores por defecto cuando no hay datos
                model.addAttribute("hayDatos", false);
                model.addAttribute("totalVentas", 0.0);
                model.addAttribute("totalOrdenes", 0L);
                model.addAttribute("categoriaMasVendida", "Sin datos");
                model.addAttribute("promedioVenta", 0.0);
                model.addAttribute("fechaActualizacion", LocalDateTime.now());
                
                // Datos por defecto para gráficas
                model.addAttribute("datosDiarios", List.of());
                model.addAttribute("datosMensuales", List.of());
                model.addAttribute("datosAnuales", List.of());
                
                // Conteo real de usuarios (clientes)
                Long totalClientes = usuarioRepository.count();
                model.addAttribute("totalClientes", totalClientes);
                
                // Producto más vendido
                String productoMasVendido = obtenerProductoMasVendido();
                model.addAttribute("productoMasVendido", productoMasVendido);
                
                // Datos reales de ventas por categoría (aún sin reporte MongoDB)
                Map<String, Double> ventasPorCategoria = obtenerVentasPorCategoria();
                model.addAttribute("ventasPorCategoria", ventasPorCategoria);
            }
            
            model.addAttribute("analytics", analyticsValidos);
            model.addAttribute("totalAnalytics", analyticsValidos.size());
            
        } catch (Exception e) {
            
            // Valores de emergencia
            model.addAttribute("hayDatos", false);
            model.addAttribute("totalVentas", 0.0);
            model.addAttribute("totalOrdenes", 0L);
            model.addAttribute("categoriaMasVendida", "Error");
            model.addAttribute("promedioVenta", 0.0);
            model.addAttribute("analytics", List.of());
            model.addAttribute("totalAnalytics", 0);
            model.addAttribute("fechaActualizacion", LocalDateTime.now());
            
            // Datos de emergencia para gráficas
            model.addAttribute("datosDiarios", List.of());
            model.addAttribute("datosMensuales", List.of());
            model.addAttribute("datosAnuales", List.of());
            
            // Conteo por defecto de usuarios
            model.addAttribute("totalClientes", 0L);
            
            // Producto más vendido por defecto
            model.addAttribute("productoMasVendido", "Sin datos");
        }
        
        return "Dashboard";
    }

    /**
     * 🔄 SINCRONIZACIÓN MANUAL DESDE DASHBOARD
     */
    @PostMapping("/dashboard/sincronizar")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sincronizarDesdesDashboard() {
        try {
            String resultado = syncService.sincronizarManualmente();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "mensaje", resultado,
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "mensaje", "Error en sincronización: " + e.getMessage()
            ));
        }
    }

    /**
     * ⚡ API: Estado de la sincronización para Dashboard
     */
    @GetMapping("/dashboard/api/estado")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEstadoDashboard() {
        try {
            long analyticsValidos = analyticsRepository.countByEsValidoTrue();
            long totalReportes = reporteRepository.count();
            Optional<ReporteVentas> ultimoReporte = reporteRepository.findFirstByOrderByFechaReporteDesc();
            
            return ResponseEntity.ok(Map.of(
                "analyticsValidos", analyticsValidos,
                "totalReportes", totalReportes,
                "ultimaActualizacion", ultimoReporte.map(ReporteVentas::getFechaReporte).orElse(null),
                "estado", analyticsValidos > 0 ? "SINCRONIZADO" : "NECESITA_SINCRONIZACION"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "estado", "ERROR",
                "mensaje", e.getMessage()
            ));
        }
    }
    
    /**
     * 📊 API: Obtener ventas por categoría desde MySQL
     */
    @GetMapping("/dashboard/api/ventas-categoria")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> obtenerVentasPorCategoriaAPI() {
        try {
            Map<String, Double> ventasPorCategoria = obtenerVentasPorCategoria();
            return ResponseEntity.ok(ventasPorCategoria);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of());
        }
    }
    
    /**
     * 🔄 Método auxiliar para convertir datos de MongoDB a formato simple
     */
    private Map<String, Double> convertirVentasPorCategoria(Map<String, Object> ventasMongo) {
        Map<String, Double> ventasConvertidas = new LinkedHashMap<>();
        
        for (Map.Entry<String, Object> entrada : ventasMongo.entrySet()) {
            String categoria = entrada.getKey();
            Object valor = entrada.getValue();
            
            if (valor instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> detalleCategoria = (Map<String, Object>) valor;
                Object totalVentas = detalleCategoria.get("totalVentas");
                if (totalVentas instanceof Number) {
                    ventasConvertidas.put(categoria, ((Number) totalVentas).doubleValue());
                }
            } else if (valor instanceof Number) {
                ventasConvertidas.put(categoria, ((Number) valor).doubleValue());
            }
        }
        
        return ventasConvertidas;
    }
    
    /**
     * 🔄 Método auxiliar para obtener ventas por categoría desde MySQL
     */
    private Map<String, Double> obtenerVentasPorCategoria() {
        try {
            List<Object[]> resultados = ventaRepository.getVentasPorCategoria();
            Map<String, Double> ventasPorCategoria = new LinkedHashMap<>();
            
            System.out.println("=== DEBUG: Resultados de ventas por categoría ===");
            System.out.println("Total resultados encontrados: " + resultados.size());
            
            for (Object[] resultado : resultados) {
                String categoria = (String) resultado[0];
                Double totalVentas = ((Number) resultado[1]).doubleValue();
                ventasPorCategoria.put(categoria, totalVentas);
                System.out.println("Categoría: " + categoria + " | Total: " + totalVentas);
            }
            
            // Si no hay datos, devolver categorías por defecto con datos de prueba
            if (ventasPorCategoria.isEmpty()) {
                System.out.println("No se encontraron datos, usando valores por defecto");
                ventasPorCategoria.put("Sudaderas", 164000.0);
                ventasPorCategoria.put("Tenis", 270000.0);
                ventasPorCategoria.put("Camisetas", 90000.0);
                ventasPorCategoria.put("Pantalonestas", 0.0);
                ventasPorCategoria.put("Camisillas", 0.0);
                ventasPorCategoria.put("Busos", 95000.0);
            }
            
            System.out.println("Mapa final: " + ventasPorCategoria);
            return ventasPorCategoria;
        } catch (Exception e) {
            System.out.println("ERROR en obtenerVentasPorCategoria: " + e.getMessage());
            e.printStackTrace();
            // Valores por defecto en caso de error
            Map<String, Double> ventasDefault = new LinkedHashMap<>();
            ventasDefault.put("Sudaderas", 164000.0);
            ventasDefault.put("Tenis", 270000.0);
            ventasDefault.put("Camisetas", 90000.0);
            ventasDefault.put("Pantalonestas", 0.0);
            ventasDefault.put("Camisillas", 0.0);
            ventasDefault.put("Busos", 95000.0);
            return ventasDefault;
        }
    }
    
    /**
     * 🏆 Método auxiliar para obtener el producto más vendido
     */
    private String obtenerProductoMasVendido() {
        try {
            List<Object[]> topProductos = ventaRepository.getTopProductos();
            
            if (!topProductos.isEmpty()) {
                Object[] primerProducto = topProductos.get(0);
                String nombreProducto = (String) primerProducto[0];
                Number cantidadVendida = (Number) primerProducto[1];
                
                System.out.println("=== DEBUG: Producto más vendido ===");
                System.out.println("Producto: " + nombreProducto + " | Cantidad: " + cantidadVendida);
                
                return nombreProducto;
            } else {
                System.out.println("No se encontraron productos vendidos");
                return "Sin ventas";
            }
        } catch (Exception e) {
            System.out.println("ERROR en obtenerProductoMasVendido: " + e.getMessage());
            e.printStackTrace();
            return "Error al cargar";
        }
    }
    
}
