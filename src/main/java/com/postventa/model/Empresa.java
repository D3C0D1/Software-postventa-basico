package com.postventa.model;

import java.sql.Timestamp;

/**
 * Modelo de Configuración de Empresa para facturación
 */
public class Empresa {
    private int id;
    private String nombre;
    private String rfc;
    private String direccion;
    private String ciudad;
    private String estado;
    private String codigoPostal;
    private String telefono;
    private String email;
    private String sitioWeb;
    private String logoPath;
    private String moneda;
    private double ivaPorcentaje;
    private String mensajeFactura;
    private Timestamp fechaActualizacion;

    public Empresa() {
        this.moneda = "MXN";
        this.ivaPorcentaje = 16.00;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public double getIvaPorcentaje() { return ivaPorcentaje; }
    public void setIvaPorcentaje(double ivaPorcentaje) { this.ivaPorcentaje = ivaPorcentaje; }

    public String getMensajeFactura() { return mensajeFactura; }
    public void setMensajeFactura(String mensajeFactura) { this.mensajeFactura = mensajeFactura; }

    public Timestamp getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

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
        return nombre;
    }
}
