package com.spring.tiendaweb.tiendawebapp.Controller;

import com.spring.tiendaweb.tiendawebapp.Service.MySQLToMongoSyncService;
import com.spring.tiendaweb.tiendawebapp.Repository.AnalyticsVentasRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.ReporteVentasRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @Autowired
    private MySQLToMongoSyncService syncService;
    
    @Autowired
    private ReporteVentasRepository reporteRepository;
    
    @Autowired
    private AnalyticsVentasRepository analyticsRepository;
    
    @Autowired
    private VentaRepository ventaRepository;

    @GetMapping("/debug/sync")
    public String testSync() {
        try {
            syncService.sincronizarVentasCompleta();
            return "Sincronización completada";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/debug/mongodb")
    public String testMongoDB() {
        try {
            long reportes = reporteRepository.count();
            long analytics = analyticsRepository.count();
            
            return "Reportes: " + reportes + " | Analytics: " + analytics;
        } catch (Exception e) {
            return "Error MongoDB: " + e.getMessage();
        }
    }
    
    @GetMapping("/debug/limpiar-mongodb")
    public String limpiarMongoDB() {
        try {
            long reportesAntes = reporteRepository.count();
            long analyticsAntes = analyticsRepository.count();
            
            reporteRepository.deleteAll();
            analyticsRepository.deleteAll();
            
            return "MongoDB limpiado. Reportes eliminados: " + reportesAntes + ", Analytics eliminados: " + analyticsAntes;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/debug/limpiar-analytics")
    public String limpiarSoloAnalytics() {
        try {
            long analyticsAntes = analyticsRepository.count();
            analyticsRepository.deleteAll();
            return "Analytics eliminados: " + analyticsAntes + ". Reportes mantenidos.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }



    @GetMapping("/debug/sync-automatico")
    public String activarSyncAutomatico() {
        try {
            long totalVentasMySQL = ventaRepository.count();
            
            if (totalVentasMySQL == 0) {
                return "Sin datos en MySQL. Inserta datos primero.";
            }
            
            syncService.sincronizarVentasCompleta();
            
            long totalReportes = reporteRepository.count();
            long totalAnalytics = analyticsRepository.count();
            
            return "Sincronización completada. MySQL: " + totalVentasMySQL + 
                   " | Reportes: " + totalReportes + " | Analytics: " + totalAnalytics;
        } catch (Exception e) {
            return "Error en sincronización: " + e.getMessage();
        }
    }
}