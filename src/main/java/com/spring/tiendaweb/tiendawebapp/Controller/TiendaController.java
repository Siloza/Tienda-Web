package com.spring.tiendaweb.tiendawebapp.Controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.transaction.Transactional;
import jakarta.servlet.http.HttpSession;

import com.spring.tiendaweb.tiendawebapp.Entity.DetalleOrden;
import com.spring.tiendaweb.tiendawebapp.Entity.Orden;
import com.spring.tiendaweb.tiendawebapp.Entity.Productos;
import com.spring.tiendaweb.tiendawebapp.Entity.Usuario;
import com.spring.tiendaweb.tiendawebapp.Entity.Venta;
import com.spring.tiendaweb.tiendawebapp.Repository.DetalleOrdenRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.OrdenRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.ProductoRepository;
import com.spring.tiendaweb.tiendawebapp.Repository.VentaRepository;
import com.spring.tiendaweb.tiendawebapp.Service.UsuarioService;
import com.spring.tiendaweb.tiendawebapp.Service.MySQLToMongoSyncService;

@Controller
public class TiendaController {

    private final OrdenRepository ordenRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioService usuarioService;
    private final MySQLToMongoSyncService syncService;

    public TiendaController(OrdenRepository ordenRepository,
                            DetalleOrdenRepository detalleOrdenRepository,
                            ProductoRepository productoRepository,
                            VentaRepository ventaRepository,
                            UsuarioService usuarioService,
                            MySQLToMongoSyncService syncService) {
        this.ordenRepository = ordenRepository;
        this.detalleOrdenRepository = detalleOrdenRepository;
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.usuarioService = usuarioService;
        this.syncService = syncService;
    }

    @GetMapping("/tienda")
    public String mostrarTienda() {
        return "Tienda";
    }

    // Checkout rápido desde el frontend estático de tienda
    @PostMapping("/api/checkout")
    @Transactional
    public ResponseEntity<String> crearOrdenDesdeTienda(@RequestBody CheckoutRequest request, HttpSession session) {
        // ✅ PASO 1: Obtener usuario desde sesión
        String usuarioAutenticado = (String) session.getAttribute("usuarioAutenticado");
        
        if (usuarioAutenticado == null || usuarioAutenticado.trim().isEmpty()) {
            return ResponseEntity.status(401).body("❌ No hay sesión activa. Por favor, inicia sesión antes de comprar.");
        }
        
        // ✅ PASO 2: Obtener usuario desde BD
        java.util.Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsuario(usuarioAutenticado);
        
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ Usuario no encontrado en la base de datos.");
        }
        
        Usuario usuario = usuarioOpt.get();
        System.out.println("✅ Usuario obtenido de sesión: " + usuario.getUsuario() + " (ID: " + usuario.getusuario_id() + ")");
        
        // ✅ PASO 3: Crear orden con datos de compra
        int cantidad = request.cantidad() != null && request.cantidad() > 0 ? request.cantidad() : 1;
        double precio = request.precio() != null ? request.precio() : 0d;
        double total = precio * cantidad;

        // 1) Recuperar o crear producto base
        Productos producto = productoRepository.findByNombre(request.nombre())
                .orElseGet(() -> {
                    Productos nuevo = new Productos();
                    nuevo.setNombre(request.nombre());
                    nuevo.setDescripcion(request.nombre());
                    nuevo.setPrecio(precio);
                    nuevo.setCantidad(999); // stock virtual
                    nuevo.setImagen(request.imagen());
                    nuevo.setCategoria(request.categoria());
                    return productoRepository.save(nuevo);
                });

        // 2) Crear orden
        Orden orden = new Orden();
        orden.setNumero(UUID.randomUUID().toString());
        orden.setFechaCreacion(Date.valueOf(LocalDate.now()));
        orden.setTotal(total);
        orden.setUsuario(usuario);  // ✅ ASIGNAR USUARIO A LA ORDEN
        ordenRepository.save(orden);
        
        System.out.println("✅ Orden creada para usuario: " + usuario.getUsuario() + " | Orden ID: " + orden.getId());

        // 3) Crear detalle
        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden);
        detalle.setProducto(producto);
        detalle.setNombre(request.nombre());
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        detalle.setTotal(total);
        detalleOrdenRepository.save(detalle);

        // 4) Crear venta
        Venta venta = new Venta(orden, producto, cantidad, precio);
        ventaRepository.save(venta);
        
        // ✅ SINCRONIZAR DATOS A MONGODB DESPUÉS DE LA COMPRA
        try {
            syncService.sincronizarVentasCompleta();
            System.out.println("✅ Dashboard actualizado en MongoDB");
        } catch (Exception e) {
            System.err.println("⚠️ Error al sincronizar con MongoDB: " + e.getMessage());
            // No fallar la compra si MongoDB falla
        }

        return ResponseEntity.ok("✅ Pago exitoso. Compra registrada para: " + usuario.getUsuario());
    }

    public record CheckoutRequest(String nombre, Double precio, String categoria, Integer cantidad, String imagen) {}
}
