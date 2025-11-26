package com.postventa.dao;

import com.postventa.config.DatabaseConfig;
import com.postventa.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Cliente
 */
public class ClienteDAO {

    /**
     * Obtiene todos los clientes
     */
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return clientes;
    }

    /**
     * Obtiene clientes activos
     */
    public List<Cliente> obtenerActivos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE activo = true ORDER BY nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes activos: " + e.getMessage());
        }
        return clientes;
    }

    /**
     * Busca clientes por código o nombre
     */
    public List<Cliente> buscar(String termino) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE activo = true AND (codigo LIKE ? OR nombre LIKE ? OR apellido LIKE ?) ORDER BY nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + termino + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clientes.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
        }
        return clientes;
    }

    /**
     * Obtiene un cliente por ID
     */
    public Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene un cliente por código
     */
    public Cliente obtenerPorCodigo(String codigo) {
        String sql = "SELECT * FROM clientes WHERE codigo = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por código: " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserta un nuevo cliente
     */
    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes (codigo, nombre, apellido, rfc, email, telefono, direccion, ciudad, estado, codigo_postal, notas, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getCodigo());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getApellido());
            stmt.setString(4, cliente.getRfc());
            stmt.setString(5, cliente.getEmail());
            stmt.setString(6, cliente.getTelefono());
            stmt.setString(7, cliente.getDireccion());
            stmt.setString(8, cliente.getCiudad());
            stmt.setString(9, cliente.getEstado());
            stmt.setString(10, cliente.getCodigoPostal());
            stmt.setString(11, cliente.getNotas());
            stmt.setBoolean(12, cliente.isActivo());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    cliente.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza un cliente existente
     */
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET codigo = ?, nombre = ?, apellido = ?, rfc = ?, email = ?, telefono = ?, direccion = ?, ciudad = ?, estado = ?, codigo_postal = ?, notas = ?, activo = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getCodigo());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getApellido());
            stmt.setString(4, cliente.getRfc());
            stmt.setString(5, cliente.getEmail());
            stmt.setString(6, cliente.getTelefono());
            stmt.setString(7, cliente.getDireccion());
            stmt.setString(8, cliente.getCiudad());
            stmt.setString(9, cliente.getEstado());
            stmt.setString(10, cliente.getCodigoPostal());
            stmt.setString(11, cliente.getNotas());
            stmt.setBoolean(12, cliente.isActivo());
            stmt.setInt(13, cliente.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina un cliente (soft delete)
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE clientes SET activo = false WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
        }
        return false;
    }

    /**
     * Genera el siguiente código de cliente
     */
    public String generarSiguienteCodigo() {
        String sql = "SELECT MAX(CAST(SUBSTRING(codigo, 4) AS UNSIGNED)) FROM clientes WHERE codigo LIKE 'CLI%'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int siguiente = rs.getInt(1) + 1;
                return String.format("CLI%03d", siguiente);
            }
        } catch (SQLException e) {
            System.err.println("Error al generar código de cliente: " + e.getMessage());
        }
        return "CLI001";
    }

    /**
     * Cuenta el total de clientes activos
     */
    public int contarActivos() {
        String sql = "SELECT COUNT(*) FROM clientes WHERE activo = true";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar clientes: " + e.getMessage());
        }
        return 0;
    }

    private Cliente mapResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setCodigo(rs.getString("codigo"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setApellido(rs.getString("apellido"));
        cliente.setRfc(rs.getString("rfc"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setCiudad(rs.getString("ciudad"));
        cliente.setEstado(rs.getString("estado"));
        cliente.setCodigoPostal(rs.getString("codigo_postal"));
        cliente.setNotas(rs.getString("notas"));
        cliente.setActivo(rs.getBoolean("activo"));
        cliente.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return cliente;
    }
}
