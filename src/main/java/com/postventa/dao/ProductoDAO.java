package com.postventa.dao;

import com.postventa.config.DatabaseConfig;
import com.postventa.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Producto
 */
public class ProductoDAO {

    /**
     * Obtiene todos los productos
     */
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id ORDER BY p.nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    /**
     * Obtiene productos activos
     */
    public List<Producto> obtenerActivos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id WHERE p.activo = true ORDER BY p.nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos activos: " + e.getMessage());
        }
        return productos;
    }

    /**
     * Busca productos por código o nombre
     */
    public List<Producto> buscar(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.activo = true AND (p.codigo LIKE ? OR p.nombre LIKE ?) ORDER BY p.nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + termino + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                productos.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }
        return productos;
    }

    /**
     * Obtiene un producto por ID
     */
    public Producto obtenerPorId(int id) {
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id WHERE p.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene un producto por código
     */
    public Producto obtenerPorCodigo(String codigo) {
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id WHERE p.codigo = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por código: " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserta un nuevo producto
     */
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO productos (codigo, nombre, descripcion, categoria_id, precio_compra, precio_venta, stock, stock_minimo, unidad, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            if (producto.getCategoriaId() > 0) {
                stmt.setInt(4, producto.getCategoriaId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setDouble(5, producto.getPrecioCompra());
            stmt.setDouble(6, producto.getPrecioVenta());
            stmt.setInt(7, producto.getStock());
            stmt.setInt(8, producto.getStockMinimo());
            stmt.setString(9, producto.getUnidad());
            stmt.setBoolean(10, producto.isActivo());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    producto.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza un producto existente
     */
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET codigo = ?, nombre = ?, descripcion = ?, categoria_id = ?, precio_compra = ?, precio_venta = ?, stock = ?, stock_minimo = ?, unidad = ?, activo = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            if (producto.getCategoriaId() > 0) {
                stmt.setInt(4, producto.getCategoriaId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setDouble(5, producto.getPrecioCompra());
            stmt.setDouble(6, producto.getPrecioVenta());
            stmt.setInt(7, producto.getStock());
            stmt.setInt(8, producto.getStockMinimo());
            stmt.setString(9, producto.getUnidad());
            stmt.setBoolean(10, producto.isActivo());
            stmt.setInt(11, producto.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza el stock de un producto
     */
    public boolean actualizarStock(int id, int cantidad) {
        String sql = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina un producto (soft delete)
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE productos SET activo = false WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene productos con bajo stock
     */
    public List<Producto> obtenerBajoStock() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.activo = true AND p.stock <= p.stock_minimo ORDER BY p.stock";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con bajo stock: " + e.getMessage());
        }
        return productos;
    }

    /**
     * Cuenta el total de productos activos
     */
    public int contarActivos() {
        String sql = "SELECT COUNT(*) FROM productos WHERE activo = true";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar productos: " + e.getMessage());
        }
        return 0;
    }

    private Producto mapResultSet(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setCategoriaId(rs.getInt("categoria_id"));
        producto.setCategoriaNombre(rs.getString("categoria_nombre"));
        producto.setPrecioCompra(rs.getDouble("precio_compra"));
        producto.setPrecioVenta(rs.getDouble("precio_venta"));
        producto.setStock(rs.getInt("stock"));
        producto.setStockMinimo(rs.getInt("stock_minimo"));
        producto.setUnidad(rs.getString("unidad"));
        producto.setActivo(rs.getBoolean("activo"));
        producto.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        producto.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        return producto;
    }
}
