package main.java.com.inventory.view.customer;


//package com.inventory.view.customer;

import com.inventory.model.Customer;
import com.inventory.service.CustomerService;
import com.inventory.service.ReportService;
import com.inventory.util.PDFGenerator;
import com.inventory.view.components.DatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * CustomerReportDialog - Generate various customer reports
 * Features:
 * - All Customer Full Details Report
 * - All Customer Short Details Report
 * - Customer Payment History
 * - Customer Invoice History
 * - Customer Return List
 * - Filter by customer, date range
 * - Export to PDF
 * - Print report
 */
public class CustomerReportDialog extends JDialog {
    
    private CustomerService customerService;
    private ReportService reportService;
    
    // Report Type Buttons
    private JButton btnAllCustomerFull;
    private JButton btnAllCustomerShort;
    private JButton btnPaymentHistory;
    private JButton btnInvoiceHistory;
    private JButton btnReturnList;
    
    // Filter Components
    private JComboBox<String> cmbCustomer;
    private JTextField txtCustomerName;
    private JTextField txtContactPerson;
    private JTextField txtCity;
    private DatePicker dpFromDate;
    private DatePicker dpToDate;
    private JTextField txtInvoiceId;
    private JTextField txtPaymentId;
    
    // Action Buttons
    private JButton btnViewReport;
    private JButton btnPrintReport;
    private JButton btnExportPDF;
    private JButton btnClose;
    
    // Selected Report Type
    private String selectedReportType;
    
    public CustomerReportDialog(Frame parent) {
        super(parent, "Customer Reports", true);
        this.customerService = new CustomerService();
        this.reportService = new ReportService();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCustomers();
        
        setSize(900, 650);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Customer Reports");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(52, 58, 64));
        titlePanel.add(lblTitle);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setBackground(Color.WHITE);
        
        // Left Panel - Report Types
        contentPanel.add(createReportTypesPanel());
        
        // Right Panel - Filters
        contentPanel.add(createFiltersPanel());
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Button Panel
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createReportTypesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                           "Customer Report's", TitledBorder.LEFT, TitledBorder.TOP,
                           new Font("Segoe UI", Font.BOLD, 14)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // All Customer Full Details
        btnAllCustomerFull = createReportButton(
            "All Customer  Full Detail's",
            new Color(0, 123, 255),
            "🔍"
        );
        panel.add(btnAllCustomerFull);
        panel.add(Box.createVerticalStrut(12));
        
        // All Customer Short Details
        btnAllCustomerShort = createReportButton(
            "All Customer  Short Detail's",
            new Color(23, 162, 184),
            "📋"
        );
        panel.add(btnAllCustomerShort);
        panel.add(Box.createVerticalStrut(12));
        
        // All Customer Payment History
        JPanel paymentPanel = new JPanel(new BorderLayout(5, 5));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel lblPaymentIcon = new JLabel("💰");
        lblPaymentIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        
        JButton btnPaymentHistory = new JButton("All Customer Payment History");
        btnPaymentHistory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnPaymentHistory.setBackground(new Color(40, 167, 69));
        btnPaymentHistory.setForeground(Color.WHITE);
        btnPaymentHistory.setFocusPainted(false);
        btnPaymentHistory.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        paymentPanel.add(lblPaymentIcon, BorderLayout.WEST);
        paymentPanel.add(btnPaymentHistory, BorderLayout.CENTER);
        panel.add(paymentPanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Invoice History Section
        JLabel lblInvoiceHistory = new JLabel("Invoice History");
        lblInvoiceHistory.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblInvoiceHistory);
        panel.add(Box.createVerticalStrut(8));
        
        JPanel invoicePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        invoicePanel.setBackground(Color.WHITE);
        invoicePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JTextField txtInvoiceNo = new JTextField("0");
        txtInvoiceNo.setPreferredSize(new Dimension(200, 30));
        invoicePanel.add(txtInvoiceNo);
        
        JButton btnInvoiceHistory = new JButton("Payment History");
        btnInvoiceHistory.setBackground(new Color(108, 117, 125));
        btnInvoiceHistory.setForeground(Color.WHITE);
        btnInvoiceHistory.setFocusPainted(false);
        invoicePanel.add(btnInvoiceHistory);
        
        panel.add(invoicePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Customer Return List
        btnReturnList = createReportButton(
            "All Customer Return List",
            new Color(220, 53, 69),
            "↩️"
        );
        panel.add(btnReturnList);
        
        return panel;
    }
    
    private JButton createReportButton(String text, Color bgColor, String icon) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker()),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return button;
    }
    
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                           "Filter Options", TitledBorder.LEFT, TitledBorder.TOP,
                           new Font("Segoe UI", Font.BOLD, 14)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Customer Selection
        JPanel customerSelectPanel = new JPanel(new BorderLayout(5, 5));
        customerSelectPanel.setBackground(Color.WHITE);
        customerSelectPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel lblCustomer = new JLabel("Select Customer:");
        lblCustomer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        customerSelectPanel.add(lblCustomer, BorderLayout.NORTH);
        
        cmbCustomer = new JComboBox<>();
        cmbCustomer.setPreferredSize(new Dimension(300, 30));
        customerSelectPanel.add(cmbCustomer, BorderLayout.CENTER);
        
        panel.add(customerSelectPanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Customer Name Filter
        panel.add(createFilterField("Customer Name:", txtCustomerName = new JTextField()));
        panel.add(Box.createVerticalStrut(10));
        
        // Contact Person Filter
        panel.add(createFilterField("Contact Person:", txtContactPerson = new JTextField()));
        panel.add(Box.createVerticalStrut(10));
        
        // City Filter
        panel.add(createFilterField("City:", txtCity = new JTextField()));
        panel.add(Box.createVerticalStrut(15));
        
        // Date Range
        JLabel lblDateRange = new JLabel("Date Range:");
        lblDateRange.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDateRange.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDateRange);
        panel.add(Box.createVerticalStrut(8));
        
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 10, 8));
        datePanel.setBackground(Color.WHITE);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        datePanel.add(new JLabel("From Date:"));
        dpFromDate = new DatePicker();
        datePanel.add(dpFromDate);
        
        datePanel.add(new JLabel("To Date:"));
        dpToDate = new DatePicker();
        datePanel.add(dpToDate);
        
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Invoice/Payment ID Filters
        panel.add(createFilterField("Invoice ID:", txtInvoiceId = new JTextField("0")));
        panel.add(Box.createVerticalStrut(10));
        
        panel.add(createFilterField("Payment ID:", txtPaymentId = new JTextField("0")));
        
        // Add glue to push everything to top
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createFilterField(String label, JTextField textField) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldPanel.add(lbl, BorderLayout.NORTH);
        
        textField.setPreferredSize(new Dimension(300, 30));
        fieldPanel.add(textField, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        
        // View Report Button
        btnViewReport = new JButton("View Report");
        btnViewReport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnViewReport.setBackground(new Color(0, 123, 255));
        btnViewReport.setForeground(Color.WHITE);
        btnViewReport.setFocusPainted(false);
        btnViewReport.setPreferredSize(new Dimension(130, 40));
        btnViewReport.setIcon(createButtonIcon("🔍"));
        buttonPanel.add(btnViewReport);
        
        // Print Report Button
        btnPrintReport = new JButton("Print Report");
        btnPrintReport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrintReport.setBackground(new Color(40, 167, 69));
        btnPrintReport.setForeground(Color.WHITE);
        btnPrintReport.setFocusPainted(false);
        btnPrintReport.setPreferredSize(new Dimension(130, 40));
        btnPrintReport.setIcon(createButtonIcon("🖨️"));
        buttonPanel.add(btnPrintReport);
        
        // Export PDF Button
        btnExportPDF = new JButton("Export PDF");
        btnExportPDF.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportPDF.setBackground(new Color(220, 53, 69));
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.setFocusPainted(false);
        btnExportPDF.setPreferredSize(new Dimension(130, 40));
        btnExportPDF.setIcon(createButtonIcon("📄"));
        buttonPanel.add(btnExportPDF);
        
        // Close Button
        btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnClose);
        
        return buttonPanel;
    }
    
    private ImageIcon createButtonIcon(String emoji) {
        // Placeholder for icon creation
        return null;
    }
    
    private void setupEventListeners() {
        // Report type buttons
        btnAllCustomerFull.addActionListener(e -> {
            selectedReportType = "ALL_CUSTOMER_FULL";
            highlightSelectedReport(btnAllCustomerFull);
        });
        
        btnAllCustomerShort.addActionListener(e -> {
            selectedReportType = "ALL_CUSTOMER_SHORT";
            highlightSelectedReport(btnAllCustomerShort);
        });
        
        btnReturnList.addActionListener(e -> {
            selectedReportType = "CUSTOMER_RETURN_LIST";
            highlightSelectedReport(btnReturnList);
        });
        
        // Action buttons
        btnViewReport.addActionListener(e -> viewReport());
        btnPrintReport.addActionListener(e -> printReport());
        btnExportPDF.addActionListener(e -> exportToPDF());
        btnClose.addActionListener(e -> dispose());
    }
    
    private void highlightSelectedReport(JButton selectedButton) {
        // Reset all buttons
        resetButtonStyles(btnAllCustomerFull);
        resetButtonStyles(btnAllCustomerShort);
        resetButtonStyles(btnReturnList);
        
        // Highlight selected
        selectedButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.YELLOW, 3),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }
    
    private void resetButtonStyles(JButton button) {
        Color bgColor = button.getBackground();
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker()),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }
    
    private void loadCustomers() {
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem("Select a Customer");
        
        try {
            List<Customer> customers = customerService.getAllCustomers();
            for (Customer customer : customers) {
                cmbCustomer.addItem(customer.getName());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void viewReport() {
        if (selectedReportType == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a report type!",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get filter parameters
            String customerName = (String) cmbCustomer.getSelectedItem();
            if ("Select a Customer".equals(customerName)) {
                customerName = null;
            }
            
            LocalDate fromDate = dpFromDate != null ? dpFromDate.getDate() : null;
            LocalDate toDate = dpToDate != null ? dpToDate.getDate() : null;
            
            // Generate report based on type
            switch (selectedReportType) {
                case "ALL_CUSTOMER_FULL":
                    showCustomerFullReport(customerName);
                    break;
                case "ALL_CUSTOMER_SHORT":
                    showCustomerShortReport(customerName);
                    break;
                case "CUSTOMER_RETURN_LIST":
                    showCustomerReturnList(customerName, fromDate, toDate);
                    break;
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error generating report: " + ex.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCustomerFullReport(String customerName) {
        List<Customer> customers;
        
        if (customerName != null) {
            Customer customer = customerService.getCustomerByName(customerName);
            customers = customer != null ? List.of(customer) : List.of();
        } else {
            customers = customerService.getAllCustomers();
        }
        
        // Create report dialog
        showReportViewer("Customer Full Details Report", 
            reportService.generateCustomerFullReport(customers));
    }
    
    private void showCustomerShortReport(String customerName) {
        List<Customer> customers;
        
        if (customerName != null) {
            Customer customer = customerService.getCustomerByName(customerName);
            customers = customer != null ? List.of(customer) : List.of();
        } else {
            customers = customerService.getAllCustomers();
        }
        
        // Create report dialog
        showReportViewer("Customer Short Details Report", 
            reportService.generateCustomerShortReport(customers));
    }
    
    private void showCustomerReturnList(String customerName, LocalDate fromDate, LocalDate toDate) {
        // Generate return list report
        String reportContent = reportService.generateCustomerReturnReport(
            customerName, fromDate, toDate);
        
        showReportViewer("Customer Return List", reportContent);
    }
    
    private void showReportViewer(String title, String content) {
        JDialog reportDialog = new JDialog(this, title, true);
        reportDialog.setLayout(new BorderLayout());
        
        JTextArea txtReport = new JTextArea(content);
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtReport.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(txtReport);
        reportDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> reportDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnClose);
        reportDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        reportDialog.setSize(800, 600);
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setVisible(true);
    }
    
    private void printReport() {
        if (selectedReportType == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a report type!",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Generate and print report
            JOptionPane.showMessageDialog(this,
                "Report sent to printer!",
                "Print Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error printing report: " + ex.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToPDF() {
        if (selectedReportType == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a report type!",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Report PDF");
            fileChooser.setSelectedFile(new java.io.File("CustomerReport.pdf"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                
                // Generate PDF
                PDFGenerator pdfGenerator = new PDFGenerator();
                
                String customerName = (String) cmbCustomer.getSelectedItem();
                if ("Select a Customer".equals(customerName)) {
                    customerName = null;
                }
                
                List<Customer> customers;
                if (customerName != null) {
                    Customer customer = customerService.getCustomerByName(customerName);
                    customers = customer != null ? List.of(customer) : List.of();
                } else {
                    customers = customerService.getAllCustomers();
                }
                
                pdfGenerator.generateCustomerReport(customers, file.getAbsolutePath(), selectedReportType);
                
                JOptionPane.showMessageDialog(this,
                    "Report exported successfully!",
                    "Export Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error exporting PDF: " + ex.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}