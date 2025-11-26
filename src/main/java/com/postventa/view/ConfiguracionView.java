package com.postventa.view;

import com.postventa.dao.EmpresaDAO;
import com.postventa.model.Empresa;

import javax.swing.*;
import java.awt.*;

/**
 * Vista para configuración de la empresa (facturación)
 */
public class ConfiguracionView extends JDialog {
    private JTextField txtNombre, txtRfc, txtDireccion, txtCiudad, txtEstado;
    private JTextField txtCodigoPostal, txtTelefono, txtEmail, txtSitioWeb;
    private JTextField txtMoneda, txtIvaPorcentaje;
    private JTextArea txtMensajeFactura;
    private JButton btnGuardar;
    
    private EmpresaDAO empresaDAO;
    private Empresa empresa;
    private DashboardView parent;

    public ConfiguracionView(DashboardView parent) {
        super(parent, "Configuración de Empresa", true);
        this.parent = parent;
        this.empresaDAO = new EmpresaDAO();
        initComponents();
        setupWindow();
        cargarDatos();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de título
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Información de la Empresa");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel lblSubtitulo = new JLabel("Configure los datos de su empresa para la facturación");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(Color.GRAY);
        titlePanel.add(lblTitulo, BorderLayout.NORTH);
        titlePanel.add(lblSubtitulo, BorderLayout.SOUTH);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Datos generales
        addSectionTitle(formPanel, gbc, row++, "Datos Generales");

        // Nombre
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nombre de Empresa:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtNombre = new JTextField(30);
        formPanel.add(txtNombre, gbc);
        gbc.gridwidth = 1;
        row++;

        // RFC
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("RFC:"), gbc);
        gbc.gridx = 1;
        txtRfc = new JTextField(15);
        formPanel.add(txtRfc, gbc);
        row++;

        // Dirección
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDireccion = new JTextField(30);
        formPanel.add(txtDireccion, gbc);
        gbc.gridwidth = 1;
        row++;

        // Ciudad y Estado
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ciudad:"), gbc);
        gbc.gridx = 1;
        txtCiudad = new JTextField(15);
        formPanel.add(txtCiudad, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 3;
        txtEstado = new JTextField(15);
        formPanel.add(txtEstado, gbc);
        row++;

        // Código Postal
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Código Postal:"), gbc);
        gbc.gridx = 1;
        txtCodigoPostal = new JTextField(10);
        formPanel.add(txtCodigoPostal, gbc);
        row++;

        // Datos de contacto
        addSectionTitle(formPanel, gbc, row++, "Datos de Contacto");

        // Teléfono y Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        formPanel.add(txtTelefono, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        row++;

        // Sitio Web
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Sitio Web:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtSitioWeb = new JTextField(30);
        formPanel.add(txtSitioWeb, gbc);
        gbc.gridwidth = 1;
        row++;

        // Configuración de facturación
        addSectionTitle(formPanel, gbc, row++, "Configuración de Facturación");

        // Moneda e IVA
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Moneda:"), gbc);
        gbc.gridx = 1;
        txtMoneda = new JTextField(5);
        formPanel.add(txtMoneda, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("IVA (%):"), gbc);
        gbc.gridx = 3;
        txtIvaPorcentaje = new JTextField(5);
        formPanel.add(txtIvaPorcentaje, gbc);
        row++;

        // Mensaje de factura
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Mensaje Factura:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtMensajeFactura = new JTextArea(3, 30);
        txtMensajeFactura.setLineWrap(true);
        txtMensajeFactura.setWrapStyleWord(true);
        JScrollPane scrollMsg = new JScrollPane(txtMensajeFactura);
        formPanel.add(scrollMsg, gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        row++;

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBorder(null);
        mainPanel.add(scrollForm, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(76, 175, 80));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setPreferredSize(new Dimension(150, 40));
        btnGuardar.addActionListener(e -> guardar());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 40));
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 4;
        JLabel lblSection = new JLabel(title);
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSection.setForeground(new Color(33, 150, 243));
        lblSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 0, 5, 0)
        ));
        panel.add(lblSection, gbc);
        gbc.gridwidth = 1;
    }

    private void setupWindow() {
        setSize(700, 600);
        setLocationRelativeTo(parent);
    }

    private void cargarDatos() {
        empresa = empresaDAO.obtener();
        if (empresa != null) {
            txtNombre.setText(empresa.getNombre());
            txtRfc.setText(empresa.getRfc());
            txtDireccion.setText(empresa.getDireccion());
            txtCiudad.setText(empresa.getCiudad());
            txtEstado.setText(empresa.getEstado());
            txtCodigoPostal.setText(empresa.getCodigoPostal());
            txtTelefono.setText(empresa.getTelefono());
            txtEmail.setText(empresa.getEmail());
            txtSitioWeb.setText(empresa.getSitioWeb());
            txtMoneda.setText(empresa.getMoneda());
            txtIvaPorcentaje.setText(String.valueOf(empresa.getIvaPorcentaje()));
            txtMensajeFactura.setText(empresa.getMensajeFactura());
        } else {
            empresa = new Empresa();
            txtMoneda.setText("MXN");
            txtIvaPorcentaje.setText("16.00");
        }
    }

    private void guardar() {
        // Validaciones
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la empresa es obligatorio");
            return;
        }

        try {
            empresa.setNombre(txtNombre.getText().trim());
            empresa.setRfc(txtRfc.getText().trim());
            empresa.setDireccion(txtDireccion.getText().trim());
            empresa.setCiudad(txtCiudad.getText().trim());
            empresa.setEstado(txtEstado.getText().trim());
            empresa.setCodigoPostal(txtCodigoPostal.getText().trim());
            empresa.setTelefono(txtTelefono.getText().trim());
            empresa.setEmail(txtEmail.getText().trim());
            empresa.setSitioWeb(txtSitioWeb.getText().trim());
            empresa.setMoneda(txtMoneda.getText().trim());
            empresa.setIvaPorcentaje(Double.parseDouble(txtIvaPorcentaje.getText().trim()));
            empresa.setMensajeFactura(txtMensajeFactura.getText().trim());

            boolean exito;
            if (empresa.getId() == 0) {
                exito = empresaDAO.insertar(empresa);
            } else {
                exito = empresaDAO.actualizar(empresa);
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, "Configuración guardada correctamente");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la configuración", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El porcentaje de IVA debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
