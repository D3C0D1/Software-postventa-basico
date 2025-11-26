package com.postventa.view;

import com.postventa.dao.VentaDAO;
import com.postventa.model.DetalleVenta;
import com.postventa.model.Venta;
import com.postventa.util.FormatUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista para listar y ver las ventas realizadas
 */
public class VentasListView extends JDialog {
    private JTable tablaVentas;
    private DefaultTableModel tableModel;
    private JButton btnVer, btnCancelar, btnRefrescar;
    
    private VentaDAO ventaDAO;
    private DashboardView parent;

    public VentasListView(DashboardView parent) {
        super(parent, "Registro de Ventas", true);
        this.parent = parent;
        this.ventaDAO = new VentaDAO();
        initComponents();
        setupWindow();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Historial de Ventas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(lblTitulo, BorderLayout.WEST);

        // Resumen
        JPanel resumenPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblVentasHoy = new JLabel("Ventas Hoy: " + FormatUtil.formatearMoneda(ventaDAO.obtenerTotalVentasHoy()));
        lblVentasHoy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVentasHoy.setForeground(new Color(76, 175, 80));
        resumenPanel.add(lblVentasHoy);
        topPanel.add(resumenPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Tabla de ventas
        String[] columnas = {"ID", "N° Factura", "Cliente", "Fecha", "Subtotal", "IVA", "Total", "Método Pago", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaVentas = new JTable(tableModel);
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVentas.setRowHeight(25);
        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(tablaVentas);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnVer = new JButton("Ver Detalle");
        btnVer.setBackground(new Color(33, 150, 243));
        btnVer.setForeground(Color.WHITE);
        btnVer.addActionListener(e -> verDetalle());

        btnCancelar = new JButton("Cancelar Venta");
        btnCancelar.setBackground(new Color(244, 67, 54));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.addActionListener(e -> cancelarVenta());

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        bottomPanel.add(btnVer);
        bottomPanel.add(btnCancelar);
        bottomPanel.add(btnRefrescar);
        bottomPanel.add(btnCerrar);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupWindow() {
        setSize(1000, 600);
        setLocationRelativeTo(parent);
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Venta> ventas = ventaDAO.obtenerTodas();
        for (Venta v : ventas) {
            tableModel.addRow(new Object[]{
                v.getId(),
                v.getNumeroFactura(),
                v.getClienteNombre() != null ? v.getClienteNombre() : "Público General",
                FormatUtil.formatearFechaHora(v.getFechaVenta()),
                FormatUtil.formatearMoneda(v.getSubtotal()),
                FormatUtil.formatearMoneda(v.getIva()),
                FormatUtil.formatearMoneda(v.getTotal()),
                v.getMetodoPago(),
                v.getEstado()
            });
        }
    }

    private void verDetalle() {
        int row = tablaVentas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para ver el detalle");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Venta venta = ventaDAO.obtenerPorId(id);
        if (venta != null) {
            mostrarDetalleVenta(venta);
        }
    }

    private void mostrarDetalleVenta(Venta venta) {
        JDialog dialog = new JDialog(this, "Detalle de Venta - " + venta.getNumeroFactura(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);

        // Panel de información
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Información de la Venta"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        infoPanel.add(new JLabel("N° Factura:"));
        infoPanel.add(new JLabel(venta.getNumeroFactura()));
        infoPanel.add(new JLabel("Cliente:"));
        infoPanel.add(new JLabel(venta.getClienteNombre() != null ? venta.getClienteNombre() : "Público General"));
        infoPanel.add(new JLabel("Fecha:"));
        infoPanel.add(new JLabel(FormatUtil.formatearFechaHora(venta.getFechaVenta())));
        infoPanel.add(new JLabel("Vendedor:"));
        infoPanel.add(new JLabel(venta.getUsuarioNombre()));
        infoPanel.add(new JLabel("Método de Pago:"));
        infoPanel.add(new JLabel(venta.getMetodoPago()));
        infoPanel.add(new JLabel("Estado:"));
        JLabel lblEstado = new JLabel(venta.getEstado());
        lblEstado.setForeground(venta.getEstado().equals("PAGADA") ? new Color(76, 175, 80) : Color.RED);
        infoPanel.add(lblEstado);

        dialog.add(infoPanel, BorderLayout.NORTH);

        // Tabla de detalle
        String[] columnas = {"Código", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        DefaultTableModel detailModel = new DefaultTableModel(columnas, 0);
        JTable tablaDetalle = new JTable(detailModel);
        tablaDetalle.setRowHeight(25);

        for (DetalleVenta d : venta.getDetalles()) {
            detailModel.addRow(new Object[]{
                d.getProductoCodigo(),
                d.getProductoNombre(),
                d.getCantidad(),
                FormatUtil.formatearMoneda(d.getPrecioUnitario()),
                FormatUtil.formatearMoneda(d.getSubtotal())
            });
        }

        JScrollPane scrollPane = new JScrollPane(tablaDetalle);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Productos"));
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Panel de totales
        JPanel totalesPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        totalesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalesPanel.add(createTotalLabel("Subtotal:", FormatUtil.formatearMoneda(venta.getSubtotal())));
        totalesPanel.add(createTotalLabel("Descuento:", FormatUtil.formatearMoneda(venta.getDescuento())));
        totalesPanel.add(createTotalLabel("IVA:", FormatUtil.formatearMoneda(venta.getIva())));
        JPanel totalPanel = createTotalLabel("TOTAL:", FormatUtil.formatearMoneda(venta.getTotal()));
        ((JLabel) totalPanel.getComponent(1)).setForeground(new Color(76, 175, 80));
        ((JLabel) totalPanel.getComponent(1)).setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalesPanel.add(totalPanel);

        dialog.add(totalesPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createTotalLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblLabel);
        panel.add(lblValue);
        return panel;
    }

    private void cancelarVenta() {
        int row = tablaVentas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para cancelar");
            return;
        }
        
        String estado = (String) tableModel.getValueAt(row, 8);
        if ("CANCELADA".equals(estado)) {
            JOptionPane.showMessageDialog(this, "Esta venta ya está cancelada");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String factura = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cancelar la venta " + factura + "?\nEsta acción restaurará el stock de los productos.",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (ventaDAO.cancelar(id)) {
                JOptionPane.showMessageDialog(this, "Venta cancelada correctamente");
                cargarDatos();
                parent.actualizarEstadisticas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cancelar la venta", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
