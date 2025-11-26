package com.postventa.view;

import com.postventa.dao.*;
import com.postventa.model.Usuario;
import com.postventa.util.FormatUtil;
import com.postventa.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dashboard principal del sistema con navegación a módulos
 */
public class DashboardView extends JFrame {
    private JPanel contentPanel;
    private JLabel lblUsuario;
    private JLabel lblTotalVentas;
    private JLabel lblTotalProductos;
    private JLabel lblTotalClientes;
    private JLabel lblVentasMes;
    
    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;

    public DashboardView() {
        ventaDAO = new VentaDAO();
        productoDAO = new ProductoDAO();
        clienteDAO = new ClienteDAO();
        initComponents();
        setupWindow();
        cargarEstadisticas();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior (Header)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panel lateral (Menú)
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        // Panel central (Contenido)
        contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(33, 150, 243));
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Título
        JLabel lblTitulo = new JLabel("Software Postventa");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        // Usuario y logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        Usuario usuario = SessionManager.getInstance().getUsuarioActual();
        lblUsuario = new JLabel(usuario != null ? usuario.getNombreCompleto() : "Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        userPanel.add(lblUsuario);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(btnLogout);
        panel.add(userPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(50, 50, 50));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Botones del menú
        panel.add(createMenuButton("🏠 Dashboard", e -> mostrarDashboard()));
        panel.add(createMenuButton("🛒 Nueva Venta", e -> mostrarNuevaVenta()));
        panel.add(createMenuButton("📋 Ventas", e -> mostrarVentas()));
        panel.add(createMenuButton("📦 Productos", e -> mostrarProductos()));
        panel.add(createMenuButton("👥 Clientes", e -> mostrarClientes()));
        panel.add(createMenuButton("🏷️ Categorías", e -> mostrarCategorias()));
        panel.add(createMenuButton("⚙️ Configuración", e -> mostrarConfiguracion()));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(50, 50, 50));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(50, 50, 50));
            }
        });
        
        btn.addActionListener(listener);
        return btn;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);

        // Tarjeta de ventas de hoy
        JPanel ventasHoyCard = createStatCard("Ventas de Hoy", "$0.00", new Color(76, 175, 80));
        lblTotalVentas = (JLabel) ((JPanel) ventasHoyCard.getComponent(0)).getComponent(1);
        statsPanel.add(ventasHoyCard);

        // Tarjeta de ventas del mes
        JPanel ventasMesCard = createStatCard("Ventas del Mes", "$0.00", new Color(33, 150, 243));
        lblVentasMes = (JLabel) ((JPanel) ventasMesCard.getComponent(0)).getComponent(1);
        statsPanel.add(ventasMesCard);

        // Tarjeta de productos
        JPanel productosCard = createStatCard("Total Productos", "0", new Color(255, 152, 0));
        lblTotalProductos = (JLabel) ((JPanel) productosCard.getComponent(0)).getComponent(1);
        statsPanel.add(productosCard);

        // Tarjeta de clientes
        JPanel clientesCard = createStatCard("Total Clientes", "0", new Color(156, 39, 176));
        lblTotalClientes = (JLabel) ((JPanel) clientesCard.getComponent(0)).getComponent(1);
        statsPanel.add(clientesCard);

        // Panel contenedor de estadísticas
        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setOpaque(false);
        statsContainer.add(statsPanel, BorderLayout.NORTH);

        // Bienvenida
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        JLabel lblBienvenida = new JLabel("¡Bienvenido al Sistema de Gestión Postventa!", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblBienvenida.setForeground(Color.GRAY);
        welcomePanel.add(lblBienvenida, BorderLayout.NORTH);

        JLabel lblInstrucciones = new JLabel("<html><center>Use el menú lateral para navegar entre los diferentes módulos.<br>Puede gestionar productos, clientes, categorías y registrar ventas.</center></html>", SwingConstants.CENTER);
        lblInstrucciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstrucciones.setForeground(Color.GRAY);
        lblInstrucciones.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        welcomePanel.add(lblInstrucciones, BorderLayout.CENTER);

        statsContainer.add(welcomePanel, BorderLayout.CENTER);

        panel.add(statsContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String titulo, String valor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel contentCard = new JPanel();
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
        contentCard.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(Color.GRAY);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(lblTitulo);
        contentCard.add(Box.createVerticalStrut(5));
        contentCard.add(lblValor);

        card.add(contentCard, BorderLayout.CENTER);

        // Indicador de color
        JPanel indicator = new JPanel();
        indicator.setBackground(color);
        indicator.setPreferredSize(new Dimension(5, 0));
        card.add(indicator, BorderLayout.WEST);

        return card;
    }

    private void cargarEstadisticas() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private double ventasHoy;
            private double ventasMes;
            private int totalProductos;
            private int totalClientes;

            @Override
            protected Void doInBackground() {
                ventasHoy = ventaDAO.obtenerTotalVentasHoy();
                ventasMes = ventaDAO.obtenerTotalVentasMes();
                totalProductos = productoDAO.contarActivos();
                totalClientes = clienteDAO.contarActivos();
                return null;
            }

            @Override
            protected void done() {
                lblTotalVentas.setText(FormatUtil.formatearMoneda(ventasHoy));
                lblVentasMes.setText(FormatUtil.formatearMoneda(ventasMes));
                lblTotalProductos.setText(String.valueOf(totalProductos));
                lblTotalClientes.setText(String.valueOf(totalClientes));
            }
        };
        worker.execute();
    }

    private void setupWindow() {
        setTitle("Dashboard - Software Postventa");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 700);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    DashboardView.this,
                    "¿Está seguro que desea salir?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void mostrarDashboard() {
        cargarEstadisticas();
    }

    private void mostrarNuevaVenta() {
        new VentaView(this).setVisible(true);
    }

    private void mostrarVentas() {
        new VentasListView(this).setVisible(true);
    }

    private void mostrarProductos() {
        new ProductosView(this).setVisible(true);
    }

    private void mostrarClientes() {
        new ClientesView(this).setVisible(true);
    }

    private void mostrarCategorias() {
        new CategoriasView(this).setVisible(true);
    }

    private void mostrarConfiguracion() {
        new ConfiguracionView(this).setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Cerrar Sesión",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().cerrarSesion();
            dispose();
            new LoginView().setVisible(true);
        }
    }

    public void actualizarEstadisticas() {
        cargarEstadisticas();
    }
}
