package com.postventa.model;

import java.sql.Timestamp;

/**
 * Modelo de Cliente para gestión de clientes
 */
public class Cliente {
    private int id;
    private String codigo;
    private String nombre;
    private String apellido;
    private String rfc;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String estado;
    private String codigoPostal;
    private String notas;
    private boolean activo;
    private Timestamp fechaRegistro;

    public Cliente() {
        this.activo = true;
    }

    public Cliente(String codigo, String nombre, String apellido) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getNombreCompleto() {
        if (apellido != null && !apellido.isEmpty()) {
            return nombre + " " + apellido;
        }
        return nombre;
    }

    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder();
        if (direccion != null) sb.append(direccion);
        if (ciudad != null) sb.append(", ").append(ciudad);
        if (estado != null) sb.append(", ").append(estado);
        if (codigoPostal != null) sb.append(" C.P. ").append(codigoPostal);
        return sb.toString();
    }

    @Override
    public String toString() {
        return codigo + " - " + getNombreCompleto();
    }
}
