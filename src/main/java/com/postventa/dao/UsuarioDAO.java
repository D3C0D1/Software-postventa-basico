package com.postventa.dao;

import com.postventa.config.DatabaseConfig;
import com.postventa.model.Usuario;
import com.postventa.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Usuario
 */
public class UsuarioDAO {

    /**
     * Autentica un usuario por username y password
     * Soporta contraseñas hasheadas y contraseñas legacy (texto plano)
     */
    public Usuario autenticar(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND activo = true";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = mapResultSet(rs);
                String storedPassword = usuario.getPassword();
                
                // Verificar contraseña (soporta hash y texto plano para migración)
                if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    // Si la contraseña está en texto plano, migrarla a hash
                    if (!PasswordUtil.isHashed(storedPassword)) {
                        migratePasswordToHash(usuario.getId(), password);
                    }
                    actualizarUltimoAcceso(usuario.getId());
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
        }
        return null;
    }

    /**
     * Migra una contraseña de texto plano a hash
     */
    private void migratePasswordToHash(int userId, String plainPassword) {
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        String sql = "UPDATE usuarios SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al migrar contraseña: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_completo";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Obtiene un usuario por ID
     */
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserta un nuevo usuario con contraseña hasheada
     */
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (username, password, nombre_completo, email, rol, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getUsername());
            // Hashear la contraseña antes de guardar
            String hashedPassword = PasswordUtil.hashPassword(usuario.getPassword());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, usuario.getNombreCompleto());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getRol());
            stmt.setBoolean(6, usuario.isActivo());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    usuario.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza un usuario existente
     * Si la contraseña no está hasheada, la hashea antes de guardar
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET username = ?, password = ?, nombre_completo = ?, email = ?, rol = ?, activo = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getUsername());
            // Si la contraseña no está hasheada, hashearla
            String password = usuario.getPassword();
            if (!PasswordUtil.isHashed(password)) {
                password = PasswordUtil.hashPassword(password);
            }
            stmt.setString(2, password);
            stmt.setString(3, usuario.getNombreCompleto());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getRol());
            stmt.setBoolean(6, usuario.isActivo());
            stmt.setInt(7, usuario.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina un usuario (soft delete)
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE usuarios SET activo = false WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
        return false;
    }

    private void actualizarUltimoAcceso(int id) {
        String sql = "UPDATE usuarios SET ultimo_acceso = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar último acceso: " + e.getMessage());
        }
    }

    private Usuario mapResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setEmail(rs.getString("email"));
        usuario.setRol(rs.getString("rol"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        usuario.setUltimoAcceso(rs.getTimestamp("ultimo_acceso"));
        return usuario;
    }
}
