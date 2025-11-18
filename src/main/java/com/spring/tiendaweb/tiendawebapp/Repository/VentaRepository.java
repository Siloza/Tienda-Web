package com.spring.tiendaweb.tiendawebapp.Repository;

import com.spring.tiendaweb.tiendawebapp.Entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // 1. Total de ventas
    @Query("SELECT SUM(v.subtotal) FROM Venta v")
    Double getTotalVentas();

    // 2. Ventas por tipo de prenda
    @Query("SELECT v.categoriaProducto, SUM(v.subtotal), COUNT(v) FROM Venta v GROUP BY v.categoriaProducto")
    List<Object[]> getVentasPorCategoria();

    // 3. Ventas semanales
    @Query("SELECT SUM(v.subtotal) FROM Venta v WHERE v.fechaVenta >= :fechaInicio AND v.fechaVenta <= :fechaFin")
    Double getVentasPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, 
                              @Param("fechaFin") LocalDateTime fechaFin);

    // Ventas del día actual
    @Query("SELECT SUM(v.subtotal) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    Double getVentasDelDia();

    // Ventas de la semana actual
    @Query("SELECT SUM(v.subtotal) FROM Venta v WHERE YEARWEEK(v.fechaVenta, 1) = YEARWEEK(CURRENT_DATE, 1)")
    Double getVentasDeLaSemana();

    // Ventas del mes actual
    @Query("SELECT SUM(v.subtotal) FROM Venta v WHERE YEAR(v.fechaVenta) = YEAR(CURRENT_DATE) AND MONTH(v.fechaVenta) = MONTH(CURRENT_DATE)")
    Double getVentasDelMes();

    // Ventas del año actual
    @Query("SELECT SUM(v.subtotal) FROM Venta v WHERE YEAR(v.fechaVenta) = YEAR(CURRENT_DATE)")
    Double getVentasDelAnio();

    // Top productos más vendidos
    @Query("SELECT v.producto.nombre, SUM(v.cantidad), SUM(v.subtotal) FROM Venta v GROUP BY v.producto.id ORDER BY SUM(v.subtotal) DESC")
    List<Object[]> getTopProductos();

    // Ventas por rango de fechas
    @Query("SELECT DATE(v.fechaVenta), SUM(v.subtotal) FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin GROUP BY DATE(v.fechaVenta) ORDER BY DATE(v.fechaVenta)")
    List<Object[]> getVentasPorDia(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                  @Param("fechaFin") LocalDateTime fechaFin);

    // Ventas por mes de los últimos 12 meses (SQL nativo)
    @Query(value = "SELECT CONCAT(YEAR(fecha_venta), '-', LPAD(MONTH(fecha_venta), 2, '0')), SUM(subtotal), COUNT(*) FROM ventas WHERE fecha_venta >= DATE_SUB(CURRENT_DATE, INTERVAL 12 MONTH) GROUP BY YEAR(fecha_venta), MONTH(fecha_venta) ORDER BY YEAR(fecha_venta), MONTH(fecha_venta)", nativeQuery = true)
    List<Object[]> getVentasPorMes();

    // Ventas por año de los últimos 5 años (SQL nativo)
    @Query(value = "SELECT YEAR(fecha_venta), SUM(subtotal), COUNT(*) FROM ventas WHERE fecha_venta >= DATE_SUB(CURRENT_DATE, INTERVAL 5 YEAR) GROUP BY YEAR(fecha_venta) ORDER BY YEAR(fecha_venta)", nativeQuery = true)
    List<Object[]> getVentasPorAnio();

    // Ventas diarias de los últimos 30 días (SQL nativo)
    @Query(value = "SELECT DATE(fecha_venta), SUM(subtotal), COUNT(*) FROM ventas WHERE fecha_venta >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) GROUP BY DATE(fecha_venta) ORDER BY DATE(fecha_venta)", nativeQuery = true)
    List<Object[]> getVentasDiariasRecientes();
}