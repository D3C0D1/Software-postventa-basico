package com.postventa.dao;

import com.postventa.config.DatabaseConfig;
import com.postventa.model.Empresa;

import java.sql.*;

/**
 * Data Access Object para Empresa
 */
public class EmpresaDAO {

    /**
     * Obtiene la configuración de la empresa
     */
    public Empresa obtener() {
        String sql = "SELECT * FROM empresa LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener empresa: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza la configuración de la empresa
     */
    public boolean actualizar(Empresa empresa) {
        String sql = "UPDATE empresa SET nombre = ?, rfc = ?, direccion = ?, ciudad = ?, estado = ?, codigo_postal = ?, telefono = ?, email = ?, sitio_web = ?, logo_path = ?, moneda = ?, iva_porcentaje = ?, mensaje_factura = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getRfc());
            stmt.setString(3, empresa.getDireccion());
            stmt.setString(4, empresa.getCiudad());
            stmt.setString(5, empresa.getEstado());
            stmt.setString(6, empresa.getCodigoPostal());
            stmt.setString(7, empresa.getTelefono());
            stmt.setString(8, empresa.getEmail());
            stmt.setString(9, empresa.getSitioWeb());
            stmt.setString(10, empresa.getLogoPath());
            stmt.setString(11, empresa.getMoneda());
            stmt.setDouble(12, empresa.getIvaPorcentaje());
            stmt.setString(13, empresa.getMensajeFactura());
            stmt.setInt(14, empresa.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar empresa: " + e.getMessage());
        }
        return false;
    }

    /**
     * Inserta la configuración inicial de la empresa si no existe
     */
    public boolean insertar(Empresa empresa) {
        String sql = "INSERT INTO empresa (nombre, rfc, direccion, ciudad, estado, codigo_postal, telefono, email, sitio_web, logo_path, moneda, iva_porcentaje, mensaje_factura) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getRfc());
            stmt.setString(3, empresa.getDireccion());
            stmt.setString(4, empresa.getCiudad());
            stmt.setString(5, empresa.getEstado());
            stmt.setString(6, empresa.getCodigoPostal());
            stmt.setString(7, empresa.getTelefono());
            stmt.setString(8, empresa.getEmail());
            stmt.setString(9, empresa.getSitioWeb());
            stmt.setString(10, empresa.getLogoPath());
            stmt.setString(11, empresa.getMoneda());
            stmt.setDouble(12, empresa.getIvaPorcentaje());
            stmt.setString(13, empresa.getMensajeFactura());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    empresa.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar empresa: " + e.getMessage());
        }
        return false;
    }

    private Empresa mapResultSet(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setId(rs.getInt("id"));
        empresa.setNombre(rs.getString("nombre"));
        empresa.setRfc(rs.getString("rfc"));
        empresa.setDireccion(rs.getString("direccion"));
        empresa.setCiudad(rs.getString("ciudad"));
        empresa.setEstado(rs.getString("estado"));
        empresa.setCodigoPostal(rs.getString("codigo_postal"));
        empresa.setTelefono(rs.getString("telefono"));
        empresa.setEmail(rs.getString("email"));
        empresa.setSitioWeb(rs.getString("sitio_web"));
        empresa.setLogoPath(rs.getString("logo_path"));
        empresa.setMoneda(rs.getString("moneda"));
        empresa.setIvaPorcentaje(rs.getDouble("iva_porcentaje"));
        empresa.setMensajeFactura(rs.getString("mensaje_factura"));
        empresa.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        return empresa;
    }
}
