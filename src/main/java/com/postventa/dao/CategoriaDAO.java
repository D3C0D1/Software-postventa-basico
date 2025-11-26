package com.postventa.dao;

import com.postventa.config.DatabaseConfig;
import com.postventa.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Categoría
 */
public class CategoriaDAO {

    /**
     * Obtiene todas las categorías
     */
    public List<Categoria> obtenerTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categorias.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
        }
        return categorias;
    }

    /**
     * Obtiene categorías activas
     */
    public List<Categoria> obtenerActivas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE activa = true ORDER BY nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categorias.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías activas: " + e.getMessage());
        }
        return categorias;
    }

    /**
     * Obtiene una categoría por ID
     */
    public Categoria obtenerPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener categoría: " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserta una nueva categoría
     */
    public boolean insertar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre, descripcion, activa) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setBoolean(3, categoria.isActiva());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    categoria.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar categoría: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza una categoría existente
     */
    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, activa = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setBoolean(3, categoria.isActiva());
            stmt.setInt(4, categoria.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina una categoría (soft delete)
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE categorias SET activa = false WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
        }
        return false;
    }

    private Categoria mapResultSet(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        categoria.setActiva(rs.getBoolean("activa"));
        categoria.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return categoria;
    }
}
