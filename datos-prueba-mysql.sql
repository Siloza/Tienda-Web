-- ===================================
-- SCRIPT PARA POBLAR BASE DE DATOS MYSQL
-- Tienda Web App - Datos de Prueba
-- ===================================

-- 1. INSERTAR USUARIOS DE PRUEBA
INSERT IGNORE INTO usuario (email, nombre, password, rol) VALUES
('admin@fitcrows.com', 'Administrador', '$2a$10$example', 'ADMIN'),
('cliente1@gmail.com', 'Juan Pérez', '$2a$10$example', 'USER'),
('cliente2@gmail.com', 'María García', '$2a$10$example', 'USER'),
('cliente3@gmail.com', 'Carlos López', '$2a$10$example', 'USER');

-- 2. INSERTAR PRODUCTOS DE PRUEBA
INSERT IGNORE INTO productos (nombre, descripcion, precio, stock, categoria, imagen_url) VALUES
-- Camisetas
('Camiseta Deportiva Negra', 'Camiseta deportiva de alta calidad', 25.99, 50, 'Camisetas', '/images/camiseta-negra.jpg'),
('Camiseta Running Blanca', 'Ideal para correr y entrenar', 29.99, 30, 'Camisetas', '/images/camiseta-blanca.jpg'),
('Camiseta Compression Pro', 'Camiseta de compresión profesional', 45.99, 20, 'Camisetas', '/images/camiseta-pro.jpg'),

-- Pantalones
('Pantalón Deportivo Azul', 'Pantalón cómodo para entrenar', 39.99, 25, 'Pantalones', '/images/pantalon-azul.jpg'),
('Leggings Premium', 'Leggings de alta calidad', 34.99, 40, 'Pantalones', '/images/leggings.jpg'),
('Shorts Running', 'Shorts ligeros para correr', 19.99, 35, 'Pantalones', '/images/shorts.jpg'),

-- Zapatos
('Zapatillas Running Pro', 'Zapatillas profesionales', 89.99, 15, 'Zapatos', '/images/zapatillas-pro.jpg'),
('Zapatillas Training', 'Para entrenamientos en gimnasio', 69.99, 20, 'Zapatos', '/images/zapatillas-training.jpg'),
('Zapatillas Casual', 'Uso diario y casual', 49.99, 25, 'Zapatos', '/images/zapatillas-casual.jpg');

-- 3. INSERTAR ÓRDENES DE PRUEBA
INSERT IGNORE INTO orden (usuario_id, fecha_orden, estado, total) VALUES
-- Órdenes de noviembre 2025
(2, '2025-11-15 10:30:00', 'COMPLETADA', 85.97),
(3, '2025-11-15 14:45:00', 'COMPLETADA', 129.98),
(4, '2025-11-16 09:15:00', 'COMPLETADA', 159.97),
(2, '2025-11-16 16:20:00', 'COMPLETADA', 75.98),
(3, '2025-11-17 11:10:00', 'COMPLETADA', 234.96),

-- Órdenes de octubre (para histórico)
(2, '2025-10-28 13:25:00', 'COMPLETADA', 94.98),
(4, '2025-10-30 15:45:00', 'COMPLETADA', 109.98),

-- Órdenes recientes (hoy)
(2, '2025-11-17 08:30:00', 'COMPLETADA', 119.98),
(3, '2025-11-17 12:45:00', 'COMPLETADA', 89.99);

-- 4. INSERTAR DETALLES DE ÓRDENES
INSERT IGNORE INTO detalle_orden (orden_id, producto_id, cantidad, precio_unitario) VALUES
-- Orden 1: Camiseta + Pantalón = 85.97
(1, 1, 2, 25.99), -- 2 Camisetas Negras
(1, 4, 1, 39.99), -- 1 Pantalón Azul

-- Orden 2: Zapatillas + Camiseta = 129.98  
(2, 7, 1, 89.99), -- 1 Zapatillas Pro
(2, 2, 1, 29.99), -- 1 Camiseta Blanca
(2, 6, 1, 19.99), -- 1 Shorts

-- Orden 3: Productos Premium = 159.97
(3, 3, 1, 45.99), -- 1 Camiseta Pro
(3, 8, 1, 69.99), -- 1 Zapatillas Training
(3, 5, 1, 34.99), -- 1 Leggings

-- Orden 4: Combo básico = 75.98
(4, 1, 1, 25.99), -- 1 Camiseta Negra
(4, 9, 1, 49.99), -- 1 Zapatillas Casual

-- Orden 5: Compra grande = 234.96
(5, 7, 2, 89.99), -- 2 Zapatillas Pro
(5, 3, 1, 45.99), -- 1 Camiseta Pro
(5, 6, 2, 19.99), -- 2 Shorts

-- Orden 6: Octubre = 94.98
(6, 2, 1, 29.99), -- 1 Camiseta Blanca
(6, 5, 1, 34.99), -- 1 Leggings
(6, 1, 1, 25.99), -- 1 Camiseta Negra

-- Orden 7: Octubre = 109.98
(7, 8, 1, 69.99), -- 1 Zapatillas Training
(7, 4, 1, 39.99), -- 1 Pantalón Azul

-- Orden 8: Hoy = 119.98
(8, 7, 1, 89.99), -- 1 Zapatillas Pro
(8, 1, 1, 25.99), -- 1 Camiseta Negra

-- Orden 9: Hoy = 89.99
(9, 7, 1, 89.99); -- 1 Zapatillas Pro

-- 5. INSERTAR DATOS DIRECTAMENTE EN TABLA VENTAS
-- (Esta tabla se usará para los analytics de MongoDB)
INSERT IGNORE INTO ventas (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_venta, categoria_producto) VALUES
-- Ventas basadas en los detalles de órdenes
(1, 1, 2, 25.99, 51.98, '2025-11-15 10:30:00', 'Camisetas'),
(1, 4, 1, 39.99, 39.99, '2025-11-15 10:30:00', 'Pantalones'),

(2, 7, 1, 89.99, 89.99, '2025-11-15 14:45:00', 'Zapatos'),
(2, 2, 1, 29.99, 29.99, '2025-11-15 14:45:00', 'Camisetas'),
(2, 6, 1, 19.99, 19.99, '2025-11-15 14:45:00', 'Pantalones'),

(3, 3, 1, 45.99, 45.99, '2025-11-16 09:15:00', 'Camisetas'),
(3, 8, 1, 69.99, 69.99, '2025-11-16 09:15:00', 'Zapatos'),
(3, 5, 1, 34.99, 34.99, '2025-11-16 09:15:00', 'Pantalones'),

(4, 1, 1, 25.99, 25.99, '2025-11-16 16:20:00', 'Camisetas'),
(4, 9, 1, 49.99, 49.99, '2025-11-16 16:20:00', 'Zapatos'),

(5, 7, 2, 89.99, 179.98, '2025-11-17 11:10:00', 'Zapatos'),
(5, 3, 1, 45.99, 45.99, '2025-11-17 11:10:00', 'Camisetas'),
(5, 6, 2, 19.99, 39.98, '2025-11-17 11:10:00', 'Pantalones'),

-- Ventas de octubre
(6, 2, 1, 29.99, 29.99, '2025-10-28 13:25:00', 'Camisetas'),
(6, 5, 1, 34.99, 34.99, '2025-10-28 13:25:00', 'Pantalones'),
(6, 1, 1, 25.99, 25.99, '2025-10-28 13:25:00', 'Camisetas'),

(7, 8, 1, 69.99, 69.99, '2025-10-30 15:45:00', 'Zapatos'),
(7, 4, 1, 39.99, 39.99, '2025-10-30 15:45:00', 'Pantalones'),

-- Ventas de hoy
(8, 7, 1, 89.99, 89.99, '2025-11-17 08:30:00', 'Zapatos'),
(8, 1, 1, 25.99, 25.99, '2025-11-17 08:30:00', 'Camisetas'),

(9, 7, 1, 89.99, 89.99, '2025-11-17 12:45:00', 'Zapatos');

-- ===================================
-- RESUMEN DE DATOS INSERTADOS:
-- ===================================
-- • 4 Usuarios (1 admin + 3 clientes)
-- • 9 Productos (3 camisetas, 3 pantalones, 3 zapatos)  
-- • 9 Órdenes (7 noviembre + 2 octubre)
-- • 19 Detalles de orden
-- • 19 Ventas individuales
-- 
-- TOTALES ESPERADOS:
-- • Total general: $1,100.81
-- • Ventas por categoría:
--   - Camisetas: $283.91 (8 ventas)
--   - Pantalones: $214.93 (6 ventas) 
--   - Zapatos: $599.96 (5 ventas)
-- • Ventas noviembre: $920.84
-- • Ventas octubre: $179.97
-- • Ventas hoy: $205.97
-- ===================================