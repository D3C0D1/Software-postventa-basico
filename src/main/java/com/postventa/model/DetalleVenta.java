package com.postventa.model;

/**
 * Modelo de Detalle de Venta para los items de cada transacción
 */
public class DetalleVenta {
    private int id;
    private int ventaId;
    private int productoId;
    private String productoCodigo;
    private String productoNombre;
    private int cantidad;
    private double precioUnitario;
    private double descuento;
    private double subtotal;

    public DetalleVenta() {}

    public DetalleVenta(int productoId, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuento = 0;
        calcularSubtotal();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVentaId() { return ventaId; }
    public void setVentaId(int ventaId) { this.ventaId = ventaId; }

    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }

    public String getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad; 
        calcularSubtotal();
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { 
        this.precioUnitario = precioUnitario; 
        calcularSubtotal();
    }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { 
        this.descuento = descuento; 
        calcularSubtotal();
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public void calcularSubtotal() {
        this.subtotal = (cantidad * precioUnitario) - descuento;
    }

    @Override
    public String toString() {
        return productoCodigo + " x " + cantidad + " = $" + String.format("%.2f", subtotal);
    }
}
