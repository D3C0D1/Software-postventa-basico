package com.postventa;

import com.formdev.flatlaf.FlatLightLaf;
import com.postventa.view.LoginView;

import javax.swing.*;

/**
 * Clase principal del Software Postventa
 * Sistema de gestión de ventas con login, dashboard, productos, clientes,
 * categorías, registro de ventas y configuración de empresa.
 */
public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 5);
            UIManager.put("Component.arc", 5);
            UIManager.put("TextComponent.arc", 5);
        } catch (Exception e) {
            System.err.println("Error al configurar Look and Feel: " + e.getMessage());
        }

        // Iniciar aplicación
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}
