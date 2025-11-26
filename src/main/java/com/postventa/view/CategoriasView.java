package com.postventa.view;

import com.postventa.dao.CategoriaDAO;
import com.postventa.model.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista para gestión de categorías (CRUD)
 */
public class CategoriasView extends JDialog {
    private JTable tablaCategorias;
    private DefaultTableModel tableModel;
    private JButton btnNuevo, btnEditar, btnEliminar, btnRefrescar;
    
    private CategoriaDAO categoriaDAO;
    private DashboardView parent;

    public CategoriasView(DashboardView parent) {
        super(parent, "Gestión de Categorías", true);
        this.parent = parent;
        this.categoriaDAO = new CategoriaDAO();
        initComponents();
        setupWindow();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Categorías");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(lblTitulo, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // Tabla de categorías
        String[] columnas = {"ID", "Nombre", "Descripción", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCategorias = new JTable(tableModel);
        tablaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCategorias.setRowHeight(25);
        tablaCategorias.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaCategorias.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaCategorias.getColumnModel().getColumn(2).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(tablaCategorias);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnNuevo = new JButton("Nueva");
        btnNuevo.setBackground(new Color(76, 175, 80));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.addActionListener(e -> nuevaCategoria());

        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(33, 150, 243));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.addActionListener(e -> editarCategoria());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarCategoria());

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
        setSize(700, 500);
        setLocationRelativeTo(parent);
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        for (Categoria c : categorias) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.isActiva() ? "Activa" : "Inactiva"
            });
        }
    }

    private void nuevaCategoria() {
        CategoriaFormDialog dialog = new CategoriaFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void editarCategoria() {
        int row = tablaCategorias.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría para editar");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Categoria categoria = categoriaDAO.obtenerPorId(id);
        if (categoria != null) {
            CategoriaFormDialog dialog = new CategoriaFormDialog(this, categoria);
            dialog.setVisible(true);
            if (dialog.isGuardado()) {
                cargarDatos();
            }
        }
    }

    private void eliminarCategoria() {
        int row = tablaCategorias.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría para eliminar");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String nombre = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar la categoría: " + nombre + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (categoriaDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Categoría eliminada correctamente");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la categoría", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Dialog interno para el formulario de categoría
    private class CategoriaFormDialog extends JDialog {
        private JTextField txtNombre;
        private JTextArea txtDescripcion;
        private JCheckBox chkActiva;
        private boolean guardado = false;
        private Categoria categoria;

        public CategoriaFormDialog(JDialog parent, Categoria categoria) {
            super(parent, categoria == null ? "Nueva Categoría" : "Editar Categoría", true);
            this.categoria = categoria;
            initFormComponents();
            if (categoria != null) {
                cargarCategoria();
            }
            setupFormWindow();
        }

        private void initFormComponents() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Nombre
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Nombre:*"), gbc);
            gbc.gridx = 1;
            txtNombre = new JTextField(25);
            panel.add(txtNombre, gbc);

            // Descripción
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            panel.add(new JLabel("Descripción:"), gbc);
            gbc.gridx = 1;
            txtDescripcion = new JTextArea(4, 25);
            txtDescripcion.setLineWrap(true);
            JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
            panel.add(scrollDesc, gbc);

            // Activa
            gbc.gridx = 0; gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.WEST;
            panel.add(new JLabel("Activa:"), gbc);
            gbc.gridx = 1;
            chkActiva = new JCheckBox();
            chkActiva.setSelected(true);
            panel.add(chkActiva, gbc);

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

            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);

            setContentPane(panel);
        }

        private void cargarCategoria() {
            txtNombre.setText(categoria.getNombre());
            txtDescripcion.setText(categoria.getDescripcion());
            chkActiva.setSelected(categoria.isActiva());
        }

        private void guardar() {
            // Validaciones
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
                return;
            }

            if (categoria == null) {
                categoria = new Categoria();
            }
            categoria.setNombre(txtNombre.getText().trim());
            categoria.setDescripcion(txtDescripcion.getText().trim());
            categoria.setActiva(chkActiva.isSelected());

            boolean exito;
            if (categoria.getId() == 0) {
                exito = categoriaDAO.insertar(categoria);
            } else {
                exito = categoriaDAO.actualizar(categoria);
            }

            if (exito) {
                guardado = true;
                JOptionPane.showMessageDialog(this, "Categoría guardada correctamente");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la categoría", "Error", JOptionPane.ERROR_MESSAGE);
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
