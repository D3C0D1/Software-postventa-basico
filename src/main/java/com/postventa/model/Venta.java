package com.postventa.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de Venta para registro de transacciones
 */
public class Venta {
    private int id;
    private String numeroFactura;
    private int clienteId;
    private String clienteNombre;
    private int usuarioId;
    private String usuarioNombre;
    private Timestamp fechaVenta;
    private double subtotal;
    private double iva;
    private double descuento;
    private double total;
    private String metodoPago;
    private String estado;
    private String notas;
    private List<DetalleVenta> detalles;

    public Venta() {
        this.metodoPago = "EFECTIVO";
        this.estado = "PAGADA";
        this.detalles = new ArrayList<>();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public Timestamp getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(Timestamp fechaVenta) { this.fechaVenta = fechaVenta; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }

    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
    }

    public void calcularTotales(double ivaPorcentaje) {
        this.subtotal = detalles.stream()
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();
        this.iva = (this.subtotal - this.descuento) * (ivaPorcentaje / 100);
        this.total = this.subtotal - this.descuento + this.iva;
    }

    @Override
    public String toString() {
        return numeroFactura + " - $" + String.format("%.2f", total);
    }
}
