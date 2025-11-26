package com.postventa.dao;

import com.postventa.config.DatabaseConfig;
import com.postventa.model.DetalleVenta;
import com.postventa.model.Venta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Venta y DetalleVenta
 */
public class VentaDAO {

    /**
     * Obtiene todas las ventas
     */
    public List<Venta> obtenerTodas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, u.nombre_completo as usuario_nombre " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                     "ORDER BY v.fecha_venta DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ventas.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Obtiene ventas por rango de fechas
     */
    public List<Venta> obtenerPorFechas(Timestamp desde, Timestamp hasta) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, u.nombre_completo as usuario_nombre " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                     "WHERE v.fecha_venta BETWEEN ? AND ? " +
                     "ORDER BY v.fecha_venta DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, desde);
            stmt.setTimestamp(2, hasta);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ventas.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por fechas: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Obtiene una venta por ID con sus detalles
     */
    public Venta obtenerPorId(int id) {
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, u.nombre_completo as usuario_nombre " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                     "WHERE v.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Venta venta = mapResultSet(rs);
                venta.setDetalles(obtenerDetalles(id));
                return venta;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener venta: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene los detalles de una venta
     */
    public List<DetalleVenta> obtenerDetalles(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT d.*, p.codigo as producto_codigo, p.nombre as producto_nombre " +
                     "FROM detalle_ventas d " +
                     "JOIN productos p ON d.producto_id = p.id " +
                     "WHERE d.venta_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                detalles.add(mapDetalleResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de venta: " + e.getMessage());
        }
        return detalles;
    }

    /**
     * Inserta una nueva venta con sus detalles
     */
    public boolean insertar(Venta venta) {
        String sqlVenta = "INSERT INTO ventas (numero_factura, cliente_id, usuario_id, subtotal, iva, descuento, total, metodo_pago, estado, notas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Insertar venta
            try (PreparedStatement stmt = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, venta.getNumeroFactura());
                if (venta.getClienteId() > 0) {
                    stmt.setInt(2, venta.getClienteId());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }
                stmt.setInt(3, venta.getUsuarioId());
                stmt.setDouble(4, venta.getSubtotal());
                stmt.setDouble(5, venta.getIva());
                stmt.setDouble(6, venta.getDescuento());
                stmt.setDouble(7, venta.getTotal());
                stmt.setString(8, venta.getMetodoPago());
                stmt.setString(9, venta.getEstado());
                stmt.setString(10, venta.getNotas());
                
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    ResultSet keys = stmt.getGeneratedKeys();
                    if (keys.next()) {
                        venta.setId(keys.getInt(1));
                    }
                }
            }
            
            // Insertar detalles y actualizar stock
            ProductoDAO productoDAO = new ProductoDAO();
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetalle)) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    stmt.setInt(1, venta.getId());
                    stmt.setInt(2, detalle.getProductoId());
                    stmt.setInt(3, detalle.getCantidad());
                    stmt.setDouble(4, detalle.getPrecioUnitario());
                    stmt.setDouble(5, detalle.getDescuento());
                    stmt.setDouble(6, detalle.getSubtotal());
                    stmt.addBatch();
                    
                    // Actualizar stock (restar cantidad vendida)
                    productoDAO.actualizarStock(detalle.getProductoId(), -detalle.getCantidad());
                }
                stmt.executeBatch();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar venta: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error al restaurar autocommit: " + e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * Cancela una venta (cambia estado a CANCELADA y restaura stock)
     */
    public boolean cancelar(int id) {
        String sql = "UPDATE ventas SET estado = 'CANCELADA' WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Obtener detalles para restaurar stock
            List<DetalleVenta> detalles = obtenerDetalles(id);
            ProductoDAO productoDAO = new ProductoDAO();
            for (DetalleVenta detalle : detalles) {
                productoDAO.actualizarStock(detalle.getProductoId(), detalle.getCantidad());
            }
            
            // Actualizar estado de venta
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al cancelar venta: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error al restaurar autocommit: " + e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * Genera el siguiente número de factura
     */
    public String generarSiguienteNumeroFactura() {
        String sql = "SELECT MAX(CAST(SUBSTRING(numero_factura, 5) AS UNSIGNED)) FROM ventas WHERE numero_factura LIKE 'FAC-%'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int siguiente = rs.getInt(1) + 1;
                return String.format("FAC-%06d", siguiente);
            }
        } catch (SQLException e) {
            System.err.println("Error al generar número de factura: " + e.getMessage());
        }
        return "FAC-000001";
    }

    /**
     * Obtiene el total de ventas del día
     */
    public double obtenerTotalVentasHoy() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM ventas WHERE DATE(fecha_venta) = CURDATE() AND estado = 'PAGADA'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total de ventas de hoy: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Obtiene el total de ventas del mes
     */
    public double obtenerTotalVentasMes() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM ventas WHERE MONTH(fecha_venta) = MONTH(CURDATE()) AND YEAR(fecha_venta) = YEAR(CURDATE()) AND estado = 'PAGADA'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total de ventas del mes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Cuenta el total de ventas
     */
    public int contarVentas() {
        String sql = "SELECT COUNT(*) FROM ventas WHERE estado = 'PAGADA'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar ventas: " + e.getMessage());
        }
        return 0;
    }

    private Venta mapResultSet(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getInt("id"));
        venta.setNumeroFactura(rs.getString("numero_factura"));
        venta.setClienteId(rs.getInt("cliente_id"));
        String clienteNombre = rs.getString("cliente_nombre");
        String clienteApellido = rs.getString("cliente_apellido");
        if (clienteNombre != null) {
            venta.setClienteNombre(clienteNombre + (clienteApellido != null ? " " + clienteApellido : ""));
        }
        venta.setUsuarioId(rs.getInt("usuario_id"));
        venta.setUsuarioNombre(rs.getString("usuario_nombre"));
        venta.setFechaVenta(rs.getTimestamp("fecha_venta"));
        venta.setSubtotal(rs.getDouble("subtotal"));
        venta.setIva(rs.getDouble("iva"));
        venta.setDescuento(rs.getDouble("descuento"));
        venta.setTotal(rs.getDouble("total"));
        venta.setMetodoPago(rs.getString("metodo_pago"));
        venta.setEstado(rs.getString("estado"));
        venta.setNotas(rs.getString("notas"));
        return venta;
    }

    private DetalleVenta mapDetalleResultSet(ResultSet rs) throws SQLException {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(rs.getInt("id"));
        detalle.setVentaId(rs.getInt("venta_id"));
        detalle.setProductoId(rs.getInt("producto_id"));
        detalle.setProductoCodigo(rs.getString("producto_codigo"));
        detalle.setProductoNombre(rs.getString("producto_nombre"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
        detalle.setDescuento(rs.getDouble("descuento"));
        detalle.setSubtotal(rs.getDouble("subtotal"));
        return detalle;
    }
}
