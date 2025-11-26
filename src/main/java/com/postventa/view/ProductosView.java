package com.postventa.view;

import com.postventa.dao.CategoriaDAO;
import com.postventa.dao.ProductoDAO;
import com.postventa.model.Categoria;
import com.postventa.model.Producto;
import com.postventa.util.FormatUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista para gestión de productos (CRUD)
 */
public class ProductosView extends JDialog {
    private JTable tablaProductos;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JButton btnNuevo, btnEditar, btnEliminar, btnRefrescar;
    
    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;
    private DashboardView parent;

    public ProductosView(DashboardView parent) {
        super(parent, "Gestión de Productos", true);
        this.parent = parent;
        this.productoDAO = new ProductoDAO();
        this.categoriaDAO = new CategoriaDAO();
        initComponents();
        setupWindow();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior con búsqueda
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(lblTitulo, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtBuscar = new JTextField(20);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto...");
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());
        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Tabla de productos
        String[] columnas = {"ID", "Código", "Nombre", "Categoría", "Precio Compra", "Precio Venta", "Stock", "Unidad", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(tableModel);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setRowHeight(25);
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBackground(new Color(76, 175, 80));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.addActionListener(e -> nuevoProducto());

        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(33, 150, 243));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.addActionListener(e -> editarProducto());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarProducto());

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        bottomPanel.add(btnNuevo);
        bottomPanel.add(btnEditar);
        bottomPanel.add(btnEliminar);
        bottomPanel.add(btnRefrescar);
        bottomPanel.add(btnCerrar);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupWindow() {
        setSize(900, 600);
        setLocationRelativeTo(parent);
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Producto> productos = productoDAO.obtenerTodos();
        for (Producto p : productos) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getCategoriaNombre(),
                FormatUtil.formatearMoneda(p.getPrecioCompra()),
                FormatUtil.formatearMoneda(p.getPrecioVenta()),
                p.getStock(),
                p.getUnidad(),
                p.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    private void buscar() {
        String termino = txtBuscar.getText().trim();
        tableModel.setRowCount(0);
        List<Producto> productos = termino.isEmpty() ? 
            productoDAO.obtenerTodos() : 
            productoDAO.buscar(termino);
        for (Producto p : productos) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getCategoriaNombre(),
                FormatUtil.formatearMoneda(p.getPrecioCompra()),
                FormatUtil.formatearMoneda(p.getPrecioVenta()),
                p.getStock(),
                p.getUnidad(),
                p.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    private void nuevoProducto() {
        ProductoFormDialog dialog = new ProductoFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarDatos();
            parent.actualizarEstadisticas();
        }
    }

    private void editarProducto() {
        int row = tablaProductos.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Producto producto = productoDAO.obtenerPorId(id);
        if (producto != null) {
            ProductoFormDialog dialog = new ProductoFormDialog(this, producto);
            dialog.setVisible(true);
            if (dialog.isGuardado()) {
                cargarDatos();
            }
        }
    }

    private void eliminarProducto() {
        int row = tablaProductos.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String nombre = (String) tableModel.getValueAt(row, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el producto: " + nombre + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (productoDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente");
                cargarDatos();
                parent.actualizarEstadisticas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el producto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Dialog interno para el formulario de producto
    private class ProductoFormDialog extends JDialog {
        private JTextField txtCodigo, txtNombre, txtDescripcion;
        private JTextField txtPrecioCompra, txtPrecioVenta, txtStock, txtStockMinimo;
        private JComboBox<Categoria> cmbCategoria;
        private JComboBox<String> cmbUnidad;
        private JCheckBox chkActivo;
        private boolean guardado = false;
        private Producto producto;

        public ProductoFormDialog(JDialog parent, Producto producto) {
            super(parent, producto == null ? "Nuevo Producto" : "Editar Producto", true);
            this.producto = producto;
            initFormComponents();
            if (producto != null) {
                cargarProducto();
            }
            setupFormWindow();
        }

        private void initFormComponents() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Código
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Código:*"), gbc);
            gbc.gridx = 1;
            txtCodigo = new JTextField(20);
            panel.add(txtCodigo, gbc);

            // Nombre
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Nombre:*"), gbc);
            gbc.gridx = 1;
            txtNombre = new JTextField(20);
            panel.add(txtNombre, gbc);

            // Descripción
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Descripción:"), gbc);
            gbc.gridx = 1;
            txtDescripcion = new JTextField(20);
            panel.add(txtDescripcion, gbc);

            // Categoría
            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(new JLabel("Categoría:"), gbc);
            gbc.gridx = 1;
            cmbCategoria = new JComboBox<>();
            cmbCategoria.addItem(null);
            for (Categoria c : categoriaDAO.obtenerActivas()) {
                cmbCategoria.addItem(c);
            }
            panel.add(cmbCategoria, gbc);

            // Precio Compra
            gbc.gridx = 0; gbc.gridy = 4;
            panel.add(new JLabel("Precio Compra:"), gbc);
            gbc.gridx = 1;
            txtPrecioCompra = new JTextField(20);
            txtPrecioCompra.setText("0.00");
            panel.add(txtPrecioCompra, gbc);

            // Precio Venta
            gbc.gridx = 0; gbc.gridy = 5;
            panel.add(new JLabel("Precio Venta:*"), gbc);
            gbc.gridx = 1;
            txtPrecioVenta = new JTextField(20);
            panel.add(txtPrecioVenta, gbc);

            // Stock
            gbc.gridx = 0; gbc.gridy = 6;
            panel.add(new JLabel("Stock:"), gbc);
            gbc.gridx = 1;
            txtStock = new JTextField(20);
            txtStock.setText("0");
            panel.add(txtStock, gbc);

            // Stock Mínimo
            gbc.gridx = 0; gbc.gridy = 7;
            panel.add(new JLabel("Stock Mínimo:"), gbc);
            gbc.gridx = 1;
            txtStockMinimo = new JTextField(20);
            txtStockMinimo.setText("5");
            panel.add(txtStockMinimo, gbc);

            // Unidad
            gbc.gridx = 0; gbc.gridy = 8;
            panel.add(new JLabel("Unidad:"), gbc);
            gbc.gridx = 1;
            cmbUnidad = new JComboBox<>(new String[]{"PZA", "KG", "LT", "MT", "CJ", "PAQ"});
            panel.add(cmbUnidad, gbc);

            // Activo
            gbc.gridx = 0; gbc.gridy = 9;
            panel.add(new JLabel("Activo:"), gbc);
            gbc.gridx = 1;
            chkActivo = new JCheckBox();
            chkActivo.setSelected(true);
            panel.add(chkActivo, gbc);

            // Botones
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnGuardar = new JButton("Guardar");
            btnGuardar.setBackground(new Color(76, 175, 80));
            btnGuardar.setForeground(Color.WHITE);
            btnGuardar.addActionListener(e -> guardar());
            
            JButton btnCancelar = new JButton("Cancelar");
            btnCancelar.addActionListener(e -> dispose());
            
            buttonPanel.add(btnGuardar);
            buttonPanel.add(btnCancelar);

            gbc.gridx = 0; gbc.gridy = 10;
            gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);

            setContentPane(panel);
        }

        private void cargarProducto() {
            txtCodigo.setText(producto.getCodigo());
            txtNombre.setText(producto.getNombre());
            txtDescripcion.setText(producto.getDescripcion());
            txtPrecioCompra.setText(String.valueOf(producto.getPrecioCompra()));
            txtPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));
            txtStock.setText(String.valueOf(producto.getStock()));
            txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
            chkActivo.setSelected(producto.isActivo());
            
            // Seleccionar categoría
            for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                Categoria c = cmbCategoria.getItemAt(i);
                if (c != null && c.getId() == producto.getCategoriaId()) {
                    cmbCategoria.setSelectedIndex(i);
                    break;
                }
            }
            
            // Seleccionar unidad
            cmbUnidad.setSelectedItem(producto.getUnidad());
        }

        private void guardar() {
            // Validaciones
            if (txtCodigo.getText().trim().isEmpty() || 
                txtNombre.getText().trim().isEmpty() || 
                txtPrecioVenta.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete los campos obligatorios (*)");
                return;
            }

            try {
                if (producto == null) {
                    producto = new Producto();
                }
                producto.setCodigo(txtCodigo.getText().trim());
                producto.setNombre(txtNombre.getText().trim());
                producto.setDescripcion(txtDescripcion.getText().trim());
                
                Categoria cat = (Categoria) cmbCategoria.getSelectedItem();
                producto.setCategoriaId(cat != null ? cat.getId() : 0);
                
                producto.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
                producto.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));
                producto.setStock(Integer.parseInt(txtStock.getText().trim()));
                producto.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
                producto.setUnidad((String) cmbUnidad.getSelectedItem());
                producto.setActivo(chkActivo.isSelected());

                boolean exito;
                if (producto.getId() == 0) {
                    exito = productoDAO.insertar(producto);
                } else {
                    exito = productoDAO.actualizar(producto);
                }

                if (exito) {
                    guardado = true;
                    JOptionPane.showMessageDialog(this, "Producto guardado correctamente");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Verifique los valores numéricos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void setupFormWindow() {
            pack();
            setLocationRelativeTo(getOwner());
            setResizable(false);
        }

        public boolean isGuardado() {
            return guardado;
        }
    }
}
