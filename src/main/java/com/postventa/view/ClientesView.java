package com.postventa.view;

import com.postventa.dao.ClienteDAO;
import com.postventa.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista para gestión de clientes (CRUD)
 */
public class ClientesView extends JDialog {
    private JTable tablaClientes;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JButton btnNuevo, btnEditar, btnEliminar, btnRefrescar;
    
    private ClienteDAO clienteDAO;
    private DashboardView parent;

    public ClientesView(DashboardView parent) {
        super(parent, "Gestión de Clientes", true);
        this.parent = parent;
        this.clienteDAO = new ClienteDAO();
        initComponents();
        setupWindow();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior con búsqueda
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(lblTitulo, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtBuscar = new JTextField(20);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar cliente...");
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());
        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Tabla de clientes
        String[] columnas = {"ID", "Código", "Nombre", "Apellido", "RFC", "Email", "Teléfono", "Ciudad", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaClientes = new JTable(tableModel);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setRowHeight(25);
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBackground(new Color(76, 175, 80));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.addActionListener(e -> nuevoCliente());

        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(33, 150, 243));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.addActionListener(e -> editarCliente());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarCliente());

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
        List<Cliente> clientes = clienteDAO.obtenerTodos();
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getCodigo(),
                c.getNombre(),
                c.getApellido(),
                c.getRfc(),
                c.getEmail(),
                c.getTelefono(),
                c.getCiudad(),
                c.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    private void buscar() {
        String termino = txtBuscar.getText().trim();
        tableModel.setRowCount(0);
        List<Cliente> clientes = termino.isEmpty() ? 
            clienteDAO.obtenerTodos() : 
            clienteDAO.buscar(termino);
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getCodigo(),
                c.getNombre(),
                c.getApellido(),
                c.getRfc(),
                c.getEmail(),
                c.getTelefono(),
                c.getCiudad(),
                c.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    private void nuevoCliente() {
        ClienteFormDialog dialog = new ClienteFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarDatos();
            parent.actualizarEstadisticas();
        }
    }

    private void editarCliente() {
        int row = tablaClientes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para editar");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Cliente cliente = clienteDAO.obtenerPorId(id);
        if (cliente != null) {
            ClienteFormDialog dialog = new ClienteFormDialog(this, cliente);
            dialog.setVisible(true);
            if (dialog.isGuardado()) {
                cargarDatos();
            }
        }
    }

    private void eliminarCliente() {
        int row = tablaClientes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String nombre = (String) tableModel.getValueAt(row, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el cliente: " + nombre + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente");
                cargarDatos();
                parent.actualizarEstadisticas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Dialog interno para el formulario de cliente
    private class ClienteFormDialog extends JDialog {
        private JTextField txtCodigo, txtNombre, txtApellido, txtRfc;
        private JTextField txtEmail, txtTelefono, txtDireccion;
        private JTextField txtCiudad, txtEstado, txtCodigoPostal;
        private JTextArea txtNotas;
        private JCheckBox chkActivo;
        private boolean guardado = false;
        private Cliente cliente;

        public ClienteFormDialog(JDialog parent, Cliente cliente) {
            super(parent, cliente == null ? "Nuevo Cliente" : "Editar Cliente", true);
            this.cliente = cliente;
            initFormComponents();
            if (cliente != null) {
                cargarCliente();
            } else {
                txtCodigo.setText(clienteDAO.generarSiguienteCodigo());
            }
            setupFormWindow();
        }

        private void initFormComponents() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            int row = 0;

            // Código
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Código:*"), gbc);
            gbc.gridx = 1;
            txtCodigo = new JTextField(20);
            panel.add(txtCodigo, gbc);

            // Nombre
            gbc.gridx = 2; gbc.gridy = row;
            panel.add(new JLabel("Nombre:*"), gbc);
            gbc.gridx = 3;
            txtNombre = new JTextField(20);
            panel.add(txtNombre, gbc);

            row++;

            // Apellido
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Apellido:"), gbc);
            gbc.gridx = 1;
            txtApellido = new JTextField(20);
            panel.add(txtApellido, gbc);

            // RFC
            gbc.gridx = 2; gbc.gridy = row;
            panel.add(new JLabel("RFC:"), gbc);
            gbc.gridx = 3;
            txtRfc = new JTextField(20);
            panel.add(txtRfc, gbc);

            row++;

            // Email
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            txtEmail = new JTextField(20);
            panel.add(txtEmail, gbc);

            // Teléfono
            gbc.gridx = 2; gbc.gridy = row;
            panel.add(new JLabel("Teléfono:"), gbc);
            gbc.gridx = 3;
            txtTelefono = new JTextField(20);
            panel.add(txtTelefono, gbc);

            row++;

            // Dirección
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Dirección:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3;
            txtDireccion = new JTextField(20);
            panel.add(txtDireccion, gbc);
            gbc.gridwidth = 1;

            row++;

            // Ciudad
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Ciudad:"), gbc);
            gbc.gridx = 1;
            txtCiudad = new JTextField(20);
            panel.add(txtCiudad, gbc);

            // Estado
            gbc.gridx = 2; gbc.gridy = row;
            panel.add(new JLabel("Estado:"), gbc);
            gbc.gridx = 3;
            txtEstado = new JTextField(20);
            panel.add(txtEstado, gbc);

            row++;

            // Código Postal
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("C.P.:"), gbc);
            gbc.gridx = 1;
            txtCodigoPostal = new JTextField(20);
            panel.add(txtCodigoPostal, gbc);

            // Activo
            gbc.gridx = 2; gbc.gridy = row;
            panel.add(new JLabel("Activo:"), gbc);
            gbc.gridx = 3;
            chkActivo = new JCheckBox();
            chkActivo.setSelected(true);
            panel.add(chkActivo, gbc);

            row++;

            // Notas
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Notas:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3;
            txtNotas = new JTextArea(3, 20);
            txtNotas.setLineWrap(true);
            JScrollPane scrollNotas = new JScrollPane(txtNotas);
            panel.add(scrollNotas, gbc);
            gbc.gridwidth = 1;

            row++;

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

            gbc.gridx = 0; gbc.gridy = row;
            gbc.gridwidth = 4;
            panel.add(buttonPanel, gbc);

            setContentPane(panel);
        }

        private void cargarCliente() {
            txtCodigo.setText(cliente.getCodigo());
            txtNombre.setText(cliente.getNombre());
            txtApellido.setText(cliente.getApellido());
            txtRfc.setText(cliente.getRfc());
            txtEmail.setText(cliente.getEmail());
            txtTelefono.setText(cliente.getTelefono());
            txtDireccion.setText(cliente.getDireccion());
            txtCiudad.setText(cliente.getCiudad());
            txtEstado.setText(cliente.getEstado());
            txtCodigoPostal.setText(cliente.getCodigoPostal());
            txtNotas.setText(cliente.getNotas());
            chkActivo.setSelected(cliente.isActivo());
        }

        private void guardar() {
            // Validaciones
            if (txtCodigo.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete los campos obligatorios (*)");
                return;
            }

            if (cliente == null) {
                cliente = new Cliente();
            }
            cliente.setCodigo(txtCodigo.getText().trim());
            cliente.setNombre(txtNombre.getText().trim());
            cliente.setApellido(txtApellido.getText().trim());
            cliente.setRfc(txtRfc.getText().trim());
            cliente.setEmail(txtEmail.getText().trim());
            cliente.setTelefono(txtTelefono.getText().trim());
            cliente.setDireccion(txtDireccion.getText().trim());
            cliente.setCiudad(txtCiudad.getText().trim());
            cliente.setEstado(txtEstado.getText().trim());
            cliente.setCodigoPostal(txtCodigoPostal.getText().trim());
            cliente.setNotas(txtNotas.getText().trim());
            cliente.setActivo(chkActivo.isSelected());

            boolean exito;
            if (cliente.getId() == 0) {
                exito = clienteDAO.insertar(cliente);
            } else {
                exito = clienteDAO.actualizar(cliente);
            }

            if (exito) {
                guardado = true;
                JOptionPane.showMessageDialog(this, "Cliente guardado correctamente");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
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
