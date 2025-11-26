package com.postventa.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilidades para formateo de datos
 */
public class FormatUtil {
    private static final DecimalFormat FORMATO_MONEDA = new DecimalFormat("$#,##0.00");
    private static final DecimalFormat FORMATO_NUMERO = new DecimalFormat("#,##0.00");
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FORMATO_FECHA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat FORMATO_HORA = new SimpleDateFormat("HH:mm:ss");

    public static String formatearMoneda(double valor) {
        return FORMATO_MONEDA.format(valor);
    }

    public static String formatearNumero(double valor) {
        return FORMATO_NUMERO.format(valor);
    }

    public static String formatearFecha(Date fecha) {
        return fecha != null ? FORMATO_FECHA.format(fecha) : "";
    }

    public static String formatearFechaHora(Date fecha) {
        return fecha != null ? FORMATO_FECHA_HORA.format(fecha) : "";
    }

    public static String formatearHora(Date fecha) {
        return fecha != null ? FORMATO_HORA.format(fecha) : "";
    }

    public static boolean esEmailValido(String email) {
        if (email == null || email.isEmpty()) return true; // Campo opcional
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean esRfcValido(String rfc) {
        if (rfc == null || rfc.isEmpty()) return true; // Campo opcional
        return rfc.matches("^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$");
    }

    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.isEmpty()) return true; // Campo opcional
        return telefono.replaceAll("[^0-9]", "").length() >= 10;
    }

    public static String limpiarTelefono(String telefono) {
        return telefono != null ? telefono.replaceAll("[^0-9]", "") : "";
    }
}
