package com.spring.tiendaweb.tiendawebapp.Document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 📄 DOCUMENTO MONGODB: Reporte completo de ventas
 * 
 * Almacena un reporte consolidado con todos los analytics
 * calculados desde MySQL en un momento específico
 */
@Document(collection = "reporte_ventas")
public class ReporteVentas {

    @Id
    private String id;

    @Field("fecha_reporte")
    private LocalDateTime fechaReporte;

    @Field("titulo_reporte")
    private String tituloReporte;

    // === ANALYTICS PRINCIPALES ===
    
    // 1. Total de ventas
    @Field("total_ventas_global")
    private Double totalVentasGlobal;

    // 2. Ventas por categoría
    @Field("ventas_por_categoria")
    private Map<String, Object> ventasPorCategoria;

    // 3. Análisis temporal
    @Field("ventas_diarias")
    private Double ventasDiarias;

    @Field("ventas_semanales")
    private Double ventasSemanales;

    @Field("ventas_mensuales")
    private Double ventasMensuales;

    @Field("ventas_anuales")
    private Double ventasAnuales;

    // 4. Top productos
    @Field("top_productos")
    private List<Map<String, Object>> topProductos;

    // === METADATOS ADICIONALES ===
    
    @Field("total_ordenes")
    private Long totalOrdenes;

    @Field("promedio_venta")
    private Double promedioVenta;

    @Field("categoria_mas_vendida")
    private String categoriaMasVendida;

    @Field("es_reporte_valido")
    private Boolean esReporteValido = true;

    // === DATOS PARA GRÁFICAS TEMPORALES ===
    
    @Field("datos_diarios")
    private List<Map<String, Object>> datosDiarios;

    @Field("datos_mensuales") 
    private List<Map<String, Object>> datosMensuales;

    @Field("datos_anuales")
    private List<Map<String, Object>> datosAnuales;

    // Constructores
    public ReporteVentas() {
        this.fechaReporte = LocalDateTime.now();
        this.tituloReporte = "Reporte de Ventas - " + LocalDateTime.now().toString();
        this.esReporteValido = true;
    }

    public ReporteVentas(String titulo) {
        this();
        this.tituloReporte = titulo;
    }

    // === GETTERS Y SETTERS ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(LocalDateTime fechaReporte) { this.fechaReporte = fechaReporte; }

    public String getTituloReporte() { return tituloReporte; }
    public void setTituloReporte(String tituloReporte) { this.tituloReporte = tituloReporte; }

    public Double getTotalVentasGlobal() { return totalVentasGlobal; }
    public void setTotalVentasGlobal(Double totalVentasGlobal) { this.totalVentasGlobal = totalVentasGlobal; }

    public Map<String, Object> getVentasPorCategoria() { return ventasPorCategoria; }
    public void setVentasPorCategoria(Map<String, Object> ventasPorCategoria) { this.ventasPorCategoria = ventasPorCategoria; }

    public Double getVentasDiarias() { return ventasDiarias; }
    public void setVentasDiarias(Double ventasDiarias) { this.ventasDiarias = ventasDiarias; }

    public Double getVentasSemanales() { return ventasSemanales; }
    public void setVentasSemanales(Double ventasSemanales) { this.ventasSemanales = ventasSemanales; }

    public Double getVentasMensuales() { return ventasMensuales; }
    public void setVentasMensuales(Double ventasMensuales) { this.ventasMensuales = ventasMensuales; }

    public Double getVentasAnuales() { return ventasAnuales; }
    public void setVentasAnuales(Double ventasAnuales) { this.ventasAnuales = ventasAnuales; }

    public List<Map<String, Object>> getTopProductos() { return topProductos; }
    public void setTopProductos(List<Map<String, Object>> topProductos) { this.topProductos = topProductos; }

    public Long getTotalOrdenes() { return totalOrdenes; }
    public void setTotalOrdenes(Long totalOrdenes) { this.totalOrdenes = totalOrdenes; }

    public Double getPromedioVenta() { return promedioVenta; }
    public void setPromedioVenta(Double promedioVenta) { this.promedioVenta = promedioVenta; }

    public String getCategoriaMasVendida() { return categoriaMasVendida; }
    public void setCategoriaMasVendida(String categoriaMasVendida) { this.categoriaMasVendida = categoriaMasVendida; }

    public Boolean getEsReporteValido() { return esReporteValido; }
    public void setEsReporteValido(Boolean esReporteValido) { this.esReporteValido = esReporteValido; }

    public List<Map<String, Object>> getDatosDiarios() { return datosDiarios; }
    public void setDatosDiarios(List<Map<String, Object>> datosDiarios) { this.datosDiarios = datosDiarios; }

    public List<Map<String, Object>> getDatosMensuales() { return datosMensuales; }
    public void setDatosMensuales(List<Map<String, Object>> datosMensuales) { this.datosMensuales = datosMensuales; }

    public List<Map<String, Object>> getDatosAnuales() { return datosAnuales; }
    public void setDatosAnuales(List<Map<String, Object>> datosAnuales) { this.datosAnuales = datosAnuales; }

    @Override
    public String toString() {
        return "ReporteVentas{" +
                "id='" + id + '\'' +
                ", fechaReporte=" + fechaReporte +
                ", tituloReporte='" + tituloReporte + '\'' +
                ", totalVentasGlobal=" + totalVentasGlobal +
                ", totalOrdenes=" + totalOrdenes +
                ", categoriaMasVendida='" + categoriaMasVendida + '\'' +
                '}';
    }
}