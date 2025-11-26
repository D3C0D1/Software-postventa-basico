package com.postventa.model;

import java.sql.Timestamp;

/**
 * Modelo de Producto para gestión de inventario
 */
public class Producto {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private int categoriaId;
    private String categoriaNombre;
    private double precioCompra;
    private double precioVenta;
    private int stock;
    private int stockMinimo;
    private String unidad;
    private boolean activo;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;

    public Producto() {
        this.activo = true;
        this.stockMinimo = 5;
        this.unidad = "PZA";
    }

    public Producto(String codigo, String nombre, double precioVenta) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Timestamp getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public boolean isBajoStock() {
        return stock <= stockMinimo;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
