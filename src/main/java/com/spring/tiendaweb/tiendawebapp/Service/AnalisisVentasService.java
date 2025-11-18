package com.spring.tiendaweb.tiendawebapp.Service;

import com.spring.tiendaweb.tiendawebapp.Repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalisisVentasService {

    @Autowired
    private VentaRepository ventaRepository;

    // 1. Total de ventas general
    public Double getTotalVentasGeneral() {
        Double total = ventaRepository.getTotalVentas();
        return total != null ? total : 0.0;
    }

    // 2. Ventas por categoría de prenda
    public Map<String, Object> getVentasPorCategoria() {
        List<Object[]> resultados = ventaRepository.getVentasPorCategoria();
        Map<String, Object> ventasPorCategoria = new HashMap<>();
        
        for (Object[] resultado : resultados) {
            String categoria = (String) resultado[0];
            Double totalVentas = (Double) resultado[1];
            Long cantidadVentas = (Long) resultado[2];
            
            Map<String, Object> detalleCategoria = new HashMap<>();
            detalleCategoria.put("totalVentas", totalVentas);
            detalleCategoria.put("cantidadVentas", cantidadVentas);
            
            ventasPorCategoria.put(categoria, detalleCategoria);
        }
        
        return ventasPorCategoria;
    }

    // 3. Análisis temporal completo
    public Map<String, Double> getAnalisisTemporal() {
        Map<String, Double> analisis = new HashMap<>();
        
        // Ventas del día
        Double ventasDelDia = ventaRepository.getVentasDelDia();
        analisis.put("ventasDelDia", ventasDelDia != null ? ventasDelDia : 0.0);
        
        // Ventas de la semana
        Double ventasDeLaSemana = ventaRepository.getVentasDeLaSemana();
        analisis.put("ventasDeLaSemana", ventasDeLaSemana != null ? ventasDeLaSemana : 0.0);
        
        // Ventas del mes
        Double ventasDelMes = ventaRepository.getVentasDelMes();
        analisis.put("ventasDelMes", ventasDelMes != null ? ventasDelMes : 0.0);
        
        // Ventas del año
        Double ventasDelAnio = ventaRepository.getVentasDelAnio();
        analisis.put("ventasDelAnio", ventasDelAnio != null ? ventasDelAnio : 0.0);
        
        return analisis;
    }

    // 4. Top productos más vendidos
    public List<Object[]> getTopProductos() {
        return ventaRepository.getTopProductos();
    }

    // 5. Ventas por período personalizado
    public Double getVentasPorPeriodoPersonalizado(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Double total = ventaRepository.getVentasPorPeriodo(fechaInicio, fechaFin);
        return total != null ? total : 0.0;
    }

    // 6. Datos temporales para gráficas
    public List<Object[]> getVentasDiariasParaGrafica() {
        return ventaRepository.getVentasDiariasRecientes();
    }

    public List<Object[]> getVentasMensualesParaGrafica() {
        return ventaRepository.getVentasPorMes();
    }

    public List<Object[]> getVentasAnualesParaGrafica() {
        return ventaRepository.getVentasPorAnio();
    }

    // 7. Dashboard completo de estadísticas
    public Map<String, Object> getDashboardCompleto() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("totalVentas", getTotalVentasGeneral());
        dashboard.put("ventasPorCategoria", getVentasPorCategoria());
        dashboard.put("analisisTemporal", getAnalisisTemporal());
        dashboard.put("topProductos", getTopProductos());
        dashboard.put("ventasDiarias", getVentasDiariasParaGrafica());
        dashboard.put("ventasMensuales", getVentasMensualesParaGrafica());
        dashboard.put("ventasAnuales", getVentasAnualesParaGrafica());
        
        return dashboard;
    }
}