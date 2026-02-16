package main.java.com.inventory.view.sales;

// package com.inventory.view.sales;

import com.inventory.model.Invoice;
import com.inventory.model.InvoiceItem;
import com.inventory.service.InvoiceService;
import com.inventory.util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * InvoiceDialog - Dialog for viewing and printing invoice details
 * Features:
 * - Display complete invoice information
 * - Show invoice items in table format
 * - Calculate and display totals
 * - Print invoice
 * - Export to PDF
 */
public class InvoiceDialog extends JDialog {
    
    private Invoice invoice;
    private InvoiceService invoiceService;
    
    // Header Components
    private JLabel lblInvoiceNo;
    private JLabel lblInvoiceDate;
    private JLabel lblCustomerName;
    private JLabel lblCustomerContact;
    private JLabel lblCustomerAddress;
    
    // Invoice Items Table
    private JTable tblInvoiceItems;
    private DefaultTableModel tableModel;
    
    // Footer Components
    private JLabel lblSubTotal;
    private JLabel lblShippingCost;
    private JLabel lblTaxAmount;
    private JLabel lblDiscountAmount;
    private JLabel lblGrandTotal;
    private JLabel lblPaidAmount;
    private JLabel lblDueAmount;
    
    // Payment Info
    private JLabel lblPaymentType;
    private JLabel lblPaymentReference;
    private JTextArea txtNotes;
    
    // Buttons
    private JButton btnPrint;
    private JButton btnExportPDF;
    private JButton btnClose;
    
    public InvoiceDialog(Frame parent, Invoice invoice) {
        super(parent, "Invoice Details - " + invoice.getInvoiceNo(), true);
        this.invoice = invoice;
        this.invoiceService = new InvoiceService();
        
        initializeComponents();
        loadInvoiceData();
        
        setSize(900, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Items Panel
        mainPanel.add(createItemsPanel(), BorderLayout.CENTER);
        
        // Footer Panel
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button Panel
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Company Info
        JLabel lblCompanyName = new JLabel("YOUR COMPANY");
        lblCompanyName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblCompanyName.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblCompanyName);
        
        headerPanel.add(Box.createVerticalStrut(5));
        
        JLabel lblCompanyDetails = new JLabel("123 Business Street, City, State 12345");
        lblCompanyDetails.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCompanyDetails.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblCompanyDetails);
        
        headerPanel.add(Box.createVerticalStrut(20));
        
        // Invoice Info Grid
        JPanel invoiceInfoPanel = new JPanel(new GridLayout(2, 4, 15, 10));
        invoiceInfoPanel.setBackground(Color.WHITE);
        
        // Invoice No
        invoiceInfoPanel.add(createInfoLabel("Invoice No:", Font.BOLD));
        lblInvoiceNo = createInfoLabel("", Font.PLAIN);
        invoiceInfoPanel.add(lblInvoiceNo);
        
        // Invoice Date
        invoiceInfoPanel.add(createInfoLabel("Date:", Font.BOLD));
        lblInvoiceDate = createInfoLabel("", Font.PLAIN);
        invoiceInfoPanel.add(lblInvoiceDate);
        
        headerPanel.add(invoiceInfoPanel);
        
        headerPanel.add(Box.createVerticalStrut(15));
        
        // Customer Info Panel
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
        customerPanel.setBackground(new Color(248, 249, 250));
        customerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Customer Information"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        lblCustomerName = createInfoLabel("", Font.BOLD);
        lblCustomerName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerPanel.add(lblCustomerName);
        
        customerPanel.add(Box.createVerticalStrut(5));
        
        lblCustomerContact = createInfoLabel("", Font.PLAIN);
        customerPanel.add(lblCustomerContact);
        
        customerPanel.add(Box.createVerticalStrut(5));
        
        lblCustomerAddress = createInfoLabel("", Font.PLAIN);
        customerPanel.add(lblCustomerAddress);
        
        headerPanel.add(customerPanel);
        
        return headerPanel;
    }
    
    private JLabel createInfoLabel(String text, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, 12));
        return label;
    }
    
    private JPanel createItemsPanel() {
        JPanel itemsPanel = new JPanel(new BorderLayout(5, 5));
        itemsPanel.setBackground(Color.WHITE);
        itemsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Invoice Items"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Create table
        String[] columns = {"S.No", "Product Name", "Bar Code", "Quantity", 
                           "Unit", "Unit Price", "Total Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblInvoiceItems = new JTable(tableModel);
        tblInvoiceItems.setRowHeight(30);
        tblInvoiceItems.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblInvoiceItems.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblInvoiceItems.getTableHeader().setBackground(new Color(52, 58, 64));
        tblInvoiceItems.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(tblInvoiceItems);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        
        itemsPanel.add(scrollPane, BorderLayout.CENTER);
        
        return itemsPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout(15, 10));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Left side - Notes and Payment Info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        
        // Payment Info
        JPanel paymentInfoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        paymentInfoPanel.setBackground(Color.WHITE);
        paymentInfoPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));
        
        paymentInfoPanel.add(new JLabel("Payment Type:"));
        lblPaymentType = new JLabel();
        lblPaymentType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        paymentInfoPanel.add(lblPaymentType);
        
        paymentInfoPanel.add(new JLabel("Reference/Check No:"));
        lblPaymentReference = new JLabel();
        lblPaymentReference.setFont(new Font("Segoe UI", Font.BOLD, 12));
        paymentInfoPanel.add(lblPaymentReference);
        
        leftPanel.add(paymentInfoPanel);
        leftPanel.add(Box.createVerticalStrut(10));
        
        // Notes
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBackground(Color.WHITE);
        notesPanel.setBorder(BorderFactory.createTitledBorder("Notes"));
        
        txtNotes = new JTextArea(3, 30);
        txtNotes.setEditable(false);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtNotes.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesPanel.add(notesScroll, BorderLayout.CENTER);
        
        leftPanel.add(notesPanel);
        
        footerPanel.add(leftPanel, BorderLayout.CENTER);
        
        // Right side - Totals
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(new Color(248, 249, 250));
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        totalsPanel.setPreferredSize(new Dimension(300, 0));
        
        // Sub Total
        totalsPanel.add(createTotalRow("Sub Total:", lblSubTotal = new JLabel()));
        totalsPanel.add(Box.createVerticalStrut(8));
        
        // Shipping Cost
        totalsPanel.add(createTotalRow("Shipping Cost:", lblShippingCost = new JLabel()));
        totalsPanel.add(Box.createVerticalStrut(8));
        
        // Tax Amount
        totalsPanel.add(createTotalRow("Tax Amount:", lblTaxAmount = new JLabel()));
        totalsPanel.add(Box.createVerticalStrut(8));
        
        // Discount Amount
        totalsPanel.add(createTotalRow("Discount:", lblDiscountAmount = new JLabel()));
        totalsPanel.add(Box.createVerticalStrut(12));
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setForeground(new Color(52, 58, 64));
        totalsPanel.add(separator);
        totalsPanel.add(Box.createVerticalStrut(12));
        
        // Grand Total
        JPanel grandTotalPanel = new JPanel(new BorderLayout(5, 0));
        grandTotalPanel.setBackground(new Color(40, 167, 69));
        grandTotalPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        grandTotalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel lblGrandTotalText = new JLabel("GRAND TOTAL:");
        lblGrandTotalText.setForeground(Color.WHITE);
        lblGrandTotalText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        lblGrandTotal = new JLabel();
        lblGrandTotal.setForeground(Color.WHITE);
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblGrandTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        grandTotalPanel.add(lblGrandTotalText, BorderLayout.WEST);
        grandTotalPanel.add(lblGrandTotal, BorderLayout.EAST);
        
        totalsPanel.add(grandTotalPanel);
        totalsPanel.add(Box.createVerticalStrut(10));
        
        // Paid Amount
        JPanel paidPanel = new JPanel(new BorderLayout(5, 0));
        paidPanel.setBackground(new Color(23, 162, 184));
        paidPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        paidPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        JLabel lblPaidText = new JLabel("Paid Amount:");
        lblPaidText.setForeground(Color.WHITE);
        lblPaidText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        lblPaidAmount = new JLabel();
        lblPaidAmount.setForeground(Color.WHITE);
        lblPaidAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPaidAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        
        paidPanel.add(lblPaidText, BorderLayout.WEST);
        paidPanel.add(lblPaidAmount, BorderLayout.EAST);
        
        totalsPanel.add(paidPanel);
        totalsPanel.add(Box.createVerticalStrut(10));
        
        // Due Amount
        JPanel duePanel = new JPanel(new BorderLayout(5, 0));
        duePanel.setBackground(new Color(220, 53, 69));
        duePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        duePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        JLabel lblDueText = new JLabel("Due Amount:");
        lblDueText.setForeground(Color.WHITE);
        lblDueText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        lblDueAmount = new JLabel();
        lblDueAmount.setForeground(Color.WHITE);
        lblDueAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDueAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        
        duePanel.add(lblDueText, BorderLayout.WEST);
        duePanel.add(lblDueAmount, BorderLayout.EAST);
        
        totalsPanel.add(duePanel);
        
        footerPanel.add(totalsPanel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private JPanel createTotalRow(String label, JLabel valueLabel) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(new Color(248, 249, 250));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel lblText = new JLabel(label);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        rowPanel.add(lblText, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.EAST);
        
        return rowPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        
        btnPrint = new JButton("Print Invoice");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrint.setBackground(new Color(0, 123, 255));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setPreferredSize(new Dimension(140, 35));
        btnPrint.addActionListener(this::printInvoice);
        
        btnExportPDF = new JButton("Export PDF");
        btnExportPDF.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportPDF.setBackground(new Color(40, 167, 69));
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.setFocusPainted(false);
        btnExportPDF.setPreferredSize(new Dimension(140, 35));
        btnExportPDF.addActionListener(this::exportToPDF);
        
        btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnPrint);
        buttonPanel.add(btnExportPDF);
        buttonPanel.add(btnClose);
        
        return buttonPanel;
    }
    
    private void loadInvoiceData() {
        // Set invoice info
        lblInvoiceNo.setText(invoice.getInvoiceNo());
        lblInvoiceDate.setText(invoice.getInvoiceDate().format(
            DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        
        // Set customer info
        if (invoice.getCustomer() != null) {
            lblCustomerName.setText(invoice.getCustomer().getName());
            lblCustomerContact.setText("Phone: " + invoice.getCustomer().getPhone() + 
                                     " | Email: " + invoice.getCustomer().getEmail());
            lblCustomerAddress.setText(invoice.getCustomer().getAddress() + 
                                      ", " + invoice.getCustomer().getCity());
        }
        
        // Load invoice items
        int serialNo = 1;
        for (InvoiceItem item : invoice.getItems()) {
            Object[] rowData = {
                serialNo++,
                item.getProduct().getName(),
                item.getProduct().getBarcode(),
                item.getQuantity(),
                item.getProduct().getUnit(),
                formatCurrency(item.getUnitPrice()),
                formatCurrency(item.getTotalPrice())
            };
            tableModel.addRow(rowData);
        }
        
        // Set totals
        BigDecimal subTotal = invoice.getItems().stream()
            .map(InvoiceItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        lblSubTotal.setText(formatCurrency(subTotal));
        lblShippingCost.setText(formatCurrency(invoice.getShippingCost()));
        lblTaxAmount.setText(formatCurrency(invoice.getTaxAmount()));
        lblDiscountAmount.setText(formatCurrency(invoice.getDiscountAmount()));
        lblGrandTotal.setText(formatCurrency(invoice.getTotalAmount()));
        lblPaidAmount.setText(formatCurrency(invoice.getPaidAmount()));
        lblDueAmount.setText(formatCurrency(invoice.getDueAmount()));
        
        // Set payment info
        lblPaymentType.setText(invoice.getPaymentType());
        lblPaymentReference.setText(invoice.getPaymentReference());
        txtNotes.setText(invoice.getNotes());
    }
    
    private String formatCurrency(BigDecimal amount) {
        return amount != null ? amount.setScale(2).toString() : "0.00";
    }
    
    private void printInvoice(ActionEvent e) {
        try {
            // Use Java Print API
            boolean printed = tblInvoiceItems.print();
            if (printed) {
                JOptionPane.showMessageDialog(this,
                    "Invoice printed successfully!",
                    "Print Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error printing invoice: " + ex.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToPDF(ActionEvent e) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Invoice PDF");
            fileChooser.setSelectedFile(new java.io.File("Invoice_" + invoice.getInvoiceNo() + ".pdf"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                
                // Generate PDF
                PDFGenerator pdfGenerator = new PDFGenerator();
                pdfGenerator.generateInvoicePDF(invoice, file.getAbsolutePath());
                
                JOptionPane.showMessageDialog(this,
                    "PDF exported successfully!",
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