package main.java.com.inventory.view.sales;


// package com.inventory.view.sales;

import com.inventory.model.Invoice;
import com.inventory.util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * InvoiceSettingsDialog - Configure invoice printing and display settings
 * Features:
 * - Choose invoice print format (A4, A5, Thermal)
 * - Select invoice type (Full Invoice, Short Invoice, Mini Bill)
 * - Include/exclude various invoice elements
 * - Add company logo and banner
 * - Preview invoice before printing
 */
public class InvoiceSettingsDialog extends JDialog {
    
    private Invoice invoice;
    
    // Settings Components
    private JRadioButton rbFullInvoice;
    private JRadioButton rbA4Invoice;
    private JRadioButton rbA5Invoice;
    private JRadioButton rbShortInvoice;
    private JRadioButton rbMiniBill;
    private JRadioButton rbOtherBill;
    private JRadioButton rbBarcode;
    
    private JCheckBox chkWithBarcode;
    
    // Logo and Banner
    private JTextField txtLogoPath;
    private JButton btnBrowseLogo;
    private JLabel lblLogoPreview;
    
    // Action Buttons
    private JButton btnPreview;
    private JButton btnPrint;
    private JButton btnSaveSettings;
    private JButton btnCancel;
    
    // Invoice Format Settings
    private String selectedFormat;
    private boolean includeBarcode;
    private String logoPath;
    
    public InvoiceSettingsDialog(Frame parent, Invoice invoice) {
        super(parent, "Invoice Settings", true);
        this.invoice = invoice;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        setSize(700, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Settings Panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(Color.WHITE);
        
        // Invoice Format Panel
        settingsPanel.add(createInvoiceFormatPanel());
        settingsPanel.add(Box.createVerticalStrut(15));
        
        // Print Invoice Name Panel
        settingsPanel.add(createInvoiceTypePanel());
        settingsPanel.add(Box.createVerticalStrut(15));
        
        // Logo and Branding Panel
        settingsPanel.add(createBrandingPanel());
        
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        
        // Buttons Panel
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInvoiceFormatPanel() {
        JPanel formatPanel = new JPanel();
        formatPanel.setLayout(new BoxLayout(formatPanel, BoxLayout.Y_AXIS));
        formatPanel.setBackground(Color.WHITE);
        formatPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                           "Print Invoice Name", TitledBorder.LEFT, TitledBorder.TOP,
                           new Font("Segoe UI", Font.BOLD, 13)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        ButtonGroup formatGroup = new ButtonGroup();
        
        // Full Invoice (A4)
        JPanel fullInvoicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        fullInvoicePanel.setBackground(Color.WHITE);
        fullInvoicePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        rbFullInvoice = new JRadioButton("Full Invoice");
        rbFullInvoice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rbFullInvoice.setBackground(Color.WHITE);
        rbFullInvoice.setSelected(true);
        formatGroup.add(rbFullInvoice);
        fullInvoicePanel.add(rbFullInvoice);
        
        // A4 Invoice Radio
        rbA4Invoice = new JRadioButton("A4 Invoice");
        rbA4Invoice.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbA4Invoice.setBackground(Color.WHITE);
        formatGroup.add(rbA4Invoice);
        fullInvoicePanel.add(rbA4Invoice);
        
        // A5 Invoice Radio
        rbA5Invoice = new JRadioButton("A5 Invoice");
        rbA5Invoice.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbA5Invoice.setBackground(Color.WHITE);
        formatGroup.add(rbA5Invoice);
        fullInvoicePanel.add(rbA5Invoice);
        
        formatPanel.add(fullInvoicePanel);
        
        // Short Invoice
        JPanel shortInvoicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        shortInvoicePanel.setBackground(Color.WHITE);
        shortInvoicePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        rbShortInvoice = new JRadioButton("A4 Short Invoice Voucher");
        rbShortInvoice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rbShortInvoice.setBackground(Color.WHITE);
        formatGroup.add(rbShortInvoice);
        shortInvoicePanel.add(rbShortInvoice);
        
        formatPanel.add(shortInvoicePanel);
        
        // Mini Bill
        JPanel miniBillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        miniBillPanel.setBackground(Color.WHITE);
        miniBillPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        rbMiniBill = new JRadioButton("Mini Bill");
        rbMiniBill.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rbMiniBill.setBackground(Color.WHITE);
        formatGroup.add(rbMiniBill);
        miniBillPanel.add(rbMiniBill);
        
        formatPanel.add(miniBillPanel);
        
        // Other Bill
        JPanel otherBillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        otherBillPanel.setBackground(Color.WHITE);
        otherBillPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        rbOtherBill = new JRadioButton("Other Bill");
        rbOtherBill.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rbOtherBill.setBackground(Color.WHITE);
        formatGroup.add(rbOtherBill);
        otherBillPanel.add(rbOtherBill);
        
        formatPanel.add(otherBillPanel);
        
        return formatPanel;
    }
    
    private JPanel createInvoiceTypePanel() {
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
        typePanel.setBackground(Color.WHITE);
        typePanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                           "Invoice Options", TitledBorder.LEFT, TitledBorder.TOP,
                           new Font("Segoe UI", Font.BOLD, 13)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // With Barcode Checkbox
        JPanel barcodePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        barcodePanel.setBackground(Color.WHITE);
        barcodePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        chkWithBarcode = new JCheckBox("With Barcode A4");
        chkWithBarcode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkWithBarcode.setBackground(Color.WHITE);
        barcodePanel.add(chkWithBarcode);
        
        typePanel.add(barcodePanel);
        
        return typePanel;
    }
    
    private JPanel createBrandingPanel() {
        JPanel brandingPanel = new JPanel();
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setBackground(Color.WHITE);
        brandingPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                           "Branding & Logo", TitledBorder.LEFT, TitledBorder.TOP,
                           new Font("Segoe UI", Font.BOLD, 13)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Logo Selection Panel
        JPanel logoSelectionPanel = new JPanel(new BorderLayout(10, 5));
        logoSelectionPanel.setBackground(Color.WHITE);
        logoSelectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel lblLogo = new JLabel("Company Logo:");
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoSelectionPanel.add(lblLogo, BorderLayout.WEST);
        
        txtLogoPath = new JTextField();
        txtLogoPath.setPreferredSize(new Dimension(350, 30));
        txtLogoPath.setEditable(false);
        logoSelectionPanel.add(txtLogoPath, BorderLayout.CENTER);
        
        btnBrowseLogo = new JButton("Browse");
        btnBrowseLogo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnBrowseLogo.setPreferredSize(new Dimension(80, 30));
        logoSelectionPanel.add(btnBrowseLogo, BorderLayout.EAST);
        
        brandingPanel.add(logoSelectionPanel);
        brandingPanel.add(Box.createVerticalStrut(10));
        
        // Logo Preview Panel
        JPanel logoPreviewPanel = new JPanel(new BorderLayout());
        logoPreviewPanel.setBackground(new Color(248, 249, 250));
        logoPreviewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        logoPreviewPanel.setPreferredSize(new Dimension(0, 120));
        
        lblLogoPreview = new JLabel("No logo selected", SwingConstants.CENTER);
        lblLogoPreview.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblLogoPreview.setForeground(Color.GRAY);
        logoPreviewPanel.add(lblLogoPreview, BorderLayout.CENTER);
        
        brandingPanel.add(logoPreviewPanel);
        
        return brandingPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        
        btnPreview = new JButton("Preview");
        btnPreview.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPreview.setBackground(new Color(23, 162, 184));
        btnPreview.setForeground(Color.WHITE);
        btnPreview.setFocusPainted(false);
        btnPreview.setPreferredSize(new Dimension(100, 35));
        
        btnPrint = new JButton("Print");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrint.setBackground(new Color(40, 167, 69));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setPreferredSize(new Dimension(100, 35));
        
        btnSaveSettings = new JButton("Save Settings");
        btnSaveSettings.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSaveSettings.setBackground(new Color(0, 123, 255));
        btnSaveSettings.setForeground(Color.WHITE);
        btnSaveSettings.setFocusPainted(false);
        btnSaveSettings.setPreferredSize(new Dimension(130, 35));
        
        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        
        buttonPanel.add(btnPreview);
        buttonPanel.add(btnSaveSettings);
        buttonPanel.add(btnPrint);
        buttonPanel.add(btnCancel);
        
        return buttonPanel;
    }
    
    private void setupEventListeners() {
        // Browse Logo Button
        btnBrowseLogo.addActionListener(e -> browseForLogo());
        
        // Preview Button
        btnPreview.addActionListener(e -> previewInvoice());
        
        // Print Button
        btnPrint.addActionListener(e -> printInvoice());
        
        // Save Settings Button
        btnSaveSettings.addActionListener(e -> saveSettings());
        
        // Cancel Button
        btnCancel.addActionListener(e -> dispose());
        
        // Format Radio Buttons
        rbFullInvoice.addActionListener(e -> selectedFormat = "FULL_INVOICE_A4");
        rbA4Invoice.addActionListener(e -> selectedFormat = "A4_INVOICE");
        rbA5Invoice.addActionListener(e -> selectedFormat = "A5_INVOICE");
        rbShortInvoice.addActionListener(e -> selectedFormat = "SHORT_INVOICE");
        rbMiniBill.addActionListener(e -> selectedFormat = "MINI_BILL");
        rbOtherBill.addActionListener(e -> selectedFormat = "OTHER_BILL");
        
        // Initialize default format
        selectedFormat = "FULL_INVOICE_A4";
    }
    
    private void browseForLogo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Company Logo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            logoPath = selectedFile.getAbsolutePath();
            txtLogoPath.setText(logoPath);
            
            // Load and display logo preview
            try {
                ImageIcon icon = new ImageIcon(logoPath);
                Image img = icon.getImage().getScaledInstance(200, 80, Image.SCALE_SMOOTH);
                lblLogoPreview.setIcon(new ImageIcon(img));
                lblLogoPreview.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error loading logo: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void previewInvoice() {
        try {
            // Apply current settings
            includeBarcode = chkWithBarcode.isSelected();
            
            // Create and show invoice dialog with preview
            InvoiceDialog invoiceDialog = new InvoiceDialog(
                (Frame) getOwner(), invoice);
            invoiceDialog.setVisible(true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error previewing invoice: " + ex.getMessage(),
                "Preview Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void printInvoice() {
        try {
            // Apply settings
            includeBarcode = chkWithBarcode.isSelected();
            
            // Validate format selection
            if (selectedFormat == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select an invoice format!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Create PDF Generator
            PDFGenerator pdfGenerator = new PDFGenerator();
            
            // Set settings
            pdfGenerator.setInvoiceFormat(selectedFormat);
            pdfGenerator.setIncludeBarcode(includeBarcode);
            if (logoPath != null && !logoPath.isEmpty()) {
                pdfGenerator.setLogoPath(logoPath);
            }
            
            // Generate temporary PDF
            String tempPath = System.getProperty("java.io.tmpdir") + 
                            File.separator + "invoice_" + invoice.getInvoiceNo() + ".pdf";
            pdfGenerator.generateInvoicePDF(invoice, tempPath);
            
            // Print the PDF
            File pdfFile = new File(tempPath);
            if (pdfFile.exists()) {
                Desktop.getDesktop().print(pdfFile);
                
                JOptionPane.showMessageDialog(this,
                    "Invoice sent to printer successfully!",
                    "Print Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error printing invoice: " + ex.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveSettings() {
        try {
            // Save settings to preferences or configuration file
            java.util.prefs.Preferences prefs = 
                java.util.prefs.Preferences.userNodeForPackage(InvoiceSettingsDialog.class);
            
            prefs.put("invoice_format", selectedFormat);
            prefs.putBoolean("include_barcode", chkWithBarcode.isSelected());
            if (logoPath != null) {
                prefs.put("logo_path", logoPath);
            }
            
            JOptionPane.showMessageDialog(this,
                "Settings saved successfully!",
                "Save Success",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving settings: " + ex.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Load saved settings from preferences
     */
    public void loadSavedSettings() {
        try {
            java.util.prefs.Preferences prefs = 
                java.util.prefs.Preferences.userNodeForPackage(InvoiceSettingsDialog.class);
            
            selectedFormat = prefs.get("invoice_format", "FULL_INVOICE_A4");
            includeBarcode = prefs.getBoolean("include_barcode", false);
            logoPath = prefs.get("logo_path", "");
            
            // Apply loaded settings to UI
            chkWithBarcode.setSelected(includeBarcode);
            
            if (!logoPath.isEmpty()) {
                txtLogoPath.setText(logoPath);
                try {
                    ImageIcon icon = new ImageIcon(logoPath);
                    Image img = icon.getImage().getScaledInstance(200, 80, Image.SCALE_SMOOTH);
                    lblLogoPreview.setIcon(new ImageIcon(img));
                    lblLogoPreview.setText("");
                } catch (Exception ex) {
                    // Logo file may not exist anymore
                    logoPath = "";
                }
            }
            
            // Select appropriate radio button
            switch (selectedFormat) {
                case "FULL_INVOICE_A4":
                    rbFullInvoice.setSelected(true);
                    break;
                case "A4_INVOICE":
                    rbA4Invoice.setSelected(true);
                    break;
                case "A5_INVOICE":
                    rbA5Invoice.setSelected(true);
                    break;
                case "SHORT_INVOICE":
                    rbShortInvoice.setSelected(true);
                    break;
                case "MINI_BILL":
                    rbMiniBill.setSelected(true);
                    break;
                case "OTHER_BILL":
                    rbOtherBill.setSelected(true);
                    break;
            }
            
        } catch (Exception ex) {
            // Use default settings if loading fails
            selectedFormat = "FULL_INVOICE_A4";
            includeBarcode = false;
        }
    }
}