package com.postventa.view;

import com.postventa.dao.*;
import com.postventa.model.*;
import com.postventa.util.FormatUtil;
import com.postventa.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista para crear una nueva venta
 */
public class VentaView extends JDialog {
    private JComboBox<Cliente> cmbCliente;
    private JComboBox<String> cmbMetodoPago;
    private JTextField txtBuscarProducto;
    private JTable tablaDetalle;
    private DefaultTableModel tableModel;
    private JLabel lblSubtotal, lblIva, lblDescuento, lblTotal;
    private JTextField txtDescuento;
    
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;
    private VentaDAO ventaDAO;
    private EmpresaDAO empresaDAO;
    
    private Venta venta;
    private Empresa empresa;
    private DashboardView parent;

    public VentaView(DashboardView parent) {
        super(parent, "Nueva Venta", true);
        this.parent = parent;
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();
        this.empresaDAO = new EmpresaDAO();
        this.venta = new Venta();
        this.empresa = empresaDAO.obtener();
        
        initComponents();
        setupWindow();
        cargarClientes();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior - Información de la venta
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Datos de la Venta"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Número de factura
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("N° Factura:"), gbc);
        gbc.gridx = 1;
        JTextField txtFactura = new JTextField(ventaDAO.generarSiguienteNumeroFactura());
        txtFactura.setEditable(false);
        topPanel.add(txtFactura, gbc);

        // Cliente
        gbc.gridx = 2; gbc.gridy = 0;
        topPanel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 3;
        cmbCliente = new JComboBox<>();
        cmbCliente.setPreferredSize(new Dimension(200, 25));
        topPanel.add(cmbCliente, gbc);

        // Método de pago
        gbc.gridx = 4; gbc.gridy = 0;
        topPanel.add(new JLabel("Método de Pago:"), gbc);
        gbc.gridx = 5;
        cmbMetodoPago = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA", "TRANSFERENCIA", "CREDITO"});
        topPanel.add(cmbMetodoPago, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Panel central - Productos y búsqueda
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Panel de búsqueda de productos
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        txtBuscarProducto = new JTextField(20);
        txtBuscarProducto.putClientProperty("JTextField.placeholderText", "Código o nombre del producto...");
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProducto());
        txtBuscarProducto.addActionListener(e -> buscarProducto());
        searchPanel.add(new JLabel("Producto:"));
        searchPanel.add(txtBuscarProducto);
        searchPanel.add(btnBuscar);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Tabla de detalle de venta
        String[] columnas = {"ID", "Código", "Producto", "Cantidad", "Precio Unit.", "Subtotal", ""};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Solo cantidad es editable
            }
        };
        tablaDetalle = new JTable(tableModel);
        tablaDetalle.setRowHeight(30);
        tablaDetalle.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaDetalle.getColumnModel().getColumn(0).setMinWidth(0);
        tablaDetalle.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaDetalle.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaDetalle.getColumnModel().getColumn(2).setPreferredWidth(250);
        tablaDetalle.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Botón eliminar en la última columna
        tablaDetalle.getColumnModel().getColumn(6).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton("Eliminar");
            btn.setBackground(new Color(244, 67, 54));
            btn.setForeground(Color.WHITE);
            return btn;
        });

        tablaDetalle.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int column = tablaDetalle.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / tablaDetalle.getRowHeight();
                if (row < tablaDetalle.getRowCount() && column == 6) {
                    eliminarDetalle(row);
                }
            }
        });

        // Listener para cambio de cantidad
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 3) {
                actualizarSubtotalFila(e.getFirstRow());
                calcularTotales();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaDetalle);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior - Totales y acciones
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Panel de totales
        JPanel totalesPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        totalesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Totales"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        totalesPanel.setPreferredSize(new Dimension(300, 150));

        totalesPanel.add(new JLabel("Subtotal:"));
        lblSubtotal = new JLabel("$0.00");
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalesPanel.add(lblSubtotal);

        totalesPanel.add(new JLabel("Descuento:"));
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        txtDescuento = new JTextField("0.00", 10);
        txtDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                calcularTotales();
            }
        });
        descPanel.add(new JLabel("$"));
        descPanel.add(txtDescuento);
        totalesPanel.add(descPanel);

        totalesPanel.add(new JLabel("IVA (" + (empresa != null ? empresa.getIvaPorcentaje() : 16) + "%):"));
        lblIva = new JLabel("$0.00");
        lblIva.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalesPanel.add(lblIva);

        totalesPanel.add(new JLabel("TOTAL:"));
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(new Color(76, 175, 80));
        totalesPanel.add(lblTotal);

        bottomPanel.add(totalesPanel, BorderLayout.EAST);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnGuardar = new JButton("Guardar Venta");
        btnGuardar.setBackground(new Color(76, 175, 80));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setPreferredSize(new Dimension(150, 40));
        btnGuardar.addActionListener(e -> guardarVenta());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 40));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setPreferredSize(new Dimension(100, 40));
        btnLimpiar.addActionListener(e -> limpiar());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnCancelar);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupWindow() {
        setSize(1000, 700);
        setLocationRelativeTo(parent);
    }

    private void cargarClientes() {
        cmbCliente.removeAllItems();
        List<Cliente> clientes = clienteDAO.obtenerActivos();
        for (Cliente c : clientes) {
            cmbCliente.addItem(c);
        }
    }

    private void buscarProducto() {
        String termino = txtBuscarProducto.getText().trim();
        if (termino.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un código o nombre de producto");
            return;
        }

        // Buscar por código exacto primero
        Producto producto = productoDAO.obtenerPorCodigo(termino);
        if (producto != null) {
            agregarProducto(producto);
            txtBuscarProducto.setText("");
            return;
        }

        // Buscar por término
        List<Producto> productos = productoDAO.buscar(termino);
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron productos");
            return;
        }

        if (productos.size() == 1) {
            agregarProducto(productos.get(0));
            txtBuscarProducto.setText("");
        } else {
            // Mostrar diálogo de selección
            Producto seleccionado = (Producto) JOptionPane.showInputDialog(
                this,
                "Seleccione un producto:",
                "Productos encontrados",
                JOptionPane.PLAIN_MESSAGE,
                null,
                productos.toArray(),
                productos.get(0)
            );
            if (seleccionado != null) {
                agregarProducto(seleccionado);
                txtBuscarProducto.setText("");
            }
        }
    }

    private void agregarProducto(Producto producto) {
        // Verificar si ya está en la tabla
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == producto.getId()) {
                int cantidadActual = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                tableModel.setValueAt(cantidadActual + 1, i, 3);
                return;
            }
        }

        // Agregar nuevo producto
        if (producto.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "El producto no tiene stock disponible", "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.addRow(new Object[]{
            producto.getId(),
            producto.getCodigo(),
            producto.getNombre(),
            1,
            FormatUtil.formatearMoneda(producto.getPrecioVenta()),
            FormatUtil.formatearMoneda(producto.getPrecioVenta()),
            "Eliminar"
        });

        calcularTotales();
    }

    private void eliminarDetalle(int row) {
        tableModel.removeRow(row);
        calcularTotales();
    }

    private void actualizarSubtotalFila(int row) {
        try {
            int cantidad = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
            String precioStr = tableModel.getValueAt(row, 4).toString().replace("$", "").replace(",", "");
            double precio = Double.parseDouble(precioStr);
            double subtotal = cantidad * precio;
            tableModel.setValueAt(FormatUtil.formatearMoneda(subtotal), row, 5);
        } catch (NumberFormatException e) {
            // Ignorar errores de formato
        }
    }

    private void calcularTotales() {
        double subtotal = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String subtotalStr = tableModel.getValueAt(i, 5).toString().replace("$", "").replace(",", "");
            subtotal += Double.parseDouble(subtotalStr);
        }

        double descuento = 0;
        try {
            descuento = Double.parseDouble(txtDescuento.getText().trim());
        } catch (NumberFormatException e) {
            descuento = 0;
        }

        double ivaPorcentaje = empresa != null ? empresa.getIvaPorcentaje() : 16;
        double iva = (subtotal - descuento) * (ivaPorcentaje / 100);
        double total = subtotal - descuento + iva;

        lblSubtotal.setText(FormatUtil.formatearMoneda(subtotal));
        lblIva.setText(FormatUtil.formatearMoneda(iva));
        lblTotal.setText(FormatUtil.formatearMoneda(total));

        venta.setSubtotal(subtotal);
        venta.setDescuento(descuento);
        venta.setIva(iva);
        venta.setTotal(total);
    }

    private void guardarVenta() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto a la venta");
            return;
        }

        // Configurar venta
        venta.setNumeroFactura(ventaDAO.generarSiguienteNumeroFactura());
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        venta.setClienteId(cliente != null ? cliente.getId() : 0);
        venta.setUsuarioId(SessionManager.getInstance().getUsuarioActual().getId());
        venta.setMetodoPago((String) cmbMetodoPago.getSelectedItem());
        venta.setEstado("PAGADA");

        // Agregar detalles
        venta.getDetalles().clear();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId((int) tableModel.getValueAt(i, 0));
            detalle.setCantidad(Integer.parseInt(tableModel.getValueAt(i, 3).toString()));
            String precioStr = tableModel.getValueAt(i, 4).toString().replace("$", "").replace(",", "");
            detalle.setPrecioUnitario(Double.parseDouble(precioStr));
            String subtotalStr = tableModel.getValueAt(i, 5).toString().replace("$", "").replace(",", "");
            detalle.setSubtotal(Double.parseDouble(subtotalStr));
            venta.agregarDetalle(detalle);
        }

        // Verificar stock antes de guardar
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoDAO.obtenerPorId(detalle.getProductoId());
            if (producto.getStock() < detalle.getCantidad()) {
                JOptionPane.showMessageDialog(this, 
                    "Stock insuficiente para el producto: " + producto.getNombre() + 
                    "\nDisponible: " + producto.getStock() + ", Solicitado: " + detalle.getCantidad(),
                    "Stock insuficiente",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (ventaDAO.insertar(venta)) {
            JOptionPane.showMessageDialog(this, 
                "Venta registrada correctamente\nN° Factura: " + venta.getNumeroFactura(),
                "Venta Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
            parent.actualizarEstadisticas();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        tableModel.setRowCount(0);
        txtBuscarProducto.setText("");
        txtDescuento.setText("0.00");
        calcularTotales();
    }
}
