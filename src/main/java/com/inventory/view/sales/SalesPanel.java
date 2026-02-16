package main.java.com.inventory.view.sales;

import com.inventory.controller.SalesController;
import com.inventory.model.Customer;
import com.inventory.model.Invoice;
import com.inventory.model.InvoiceItem;
import com.inventory.model.Product;
import com.inventory.service.CustomerService;
import com.inventory.service.ProductService;
import com.inventory.util.ValidationUtil;
import com.inventory.view.components.CustomButton;
import com.inventory.view.components.CustomTable;
import com.inventory.view.components.DatePicker;
import com.inventory.view.components.SearchTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SalesPanel - Main panel for creating sales invoices
 * Features:
 * - Customer selection
 * - Product selection with barcode support
 * - Dynamic invoice item management
 * - Real-time calculation of totals, discounts, tax
 * - Payment processing
 * - Invoice printing
 */
public class SalesPanel extends JPanel {
    
    private SalesController salesController;
    private CustomerService customerService;
    private ProductService productService;
    
    // Header Components
    private JLabel lblInvoiceNo;
    private JLabel lblDate;
    private JLabel lblTime;
    private JLabel lblArrears;
    private JComboBox<String> cmbCustomer;
    private JComboBox<String> cmbCity;
    private JButton btnNewCustomer;
    
    // Product Selection Components
    private JTextField txtProductId;
    private JComboBox<String> cmbProduct;
    private JTextField txtQuantity;
    private JComboBox<String> cmbCapacity;
    private JLabel lblSymbol;
    
    // Invoice Items Table
    private CustomTable tblInvoiceItems;
    private DefaultTableModel tableModel;
    private List<InvoiceItem> invoiceItems;
    
    // Calculation Components
    private JTextField txtShippingCost;
    private JTextField txtTaxAmount;
    private JComboBox<String> cmbTaxType;
    private JTextField txtDiscountRate;
    private JComboBox<String> cmbDiscountType;
    private JTextField txtPaidAmount;
    private JComboBox<String> cmbPaymentType;
    private JTextField txtCheckNo;
    private JTextArea txtNotes;
    
    // Total Display Labels
    private JLabel lblSubTotal;
    private JLabel lblShippingCost;
    private JLabel lblTaxAmount;
    private JLabel lblDiscountAmount;
    private JLabel lblGrandTotal;
    private JLabel lblTotalDue;
    
    // Action Buttons
    private CustomButton btnAdd;
    private CustomButton btnRemove;
    private CustomButton btnRemoveAll;
    private CustomButton btnPayAndPrint;
    
    // Current Invoice Data
    private String currentInvoiceNo;
    private Customer selectedCustomer;
    private BigDecimal arrearsBalance;
    
    public SalesPanel() {
        initializeServices();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadInitialData();
        startTimeUpdater();
    }
    
    private void initializeServices() {
        this.salesController = new SalesController();
        this.customerService = new CustomerService();
        this.productService = new ProductService();
        this.invoiceItems = new ArrayList<>();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 245));
    }
    
    private void setupLayout() {
        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Center Panel with Product Selection and Table
        add(createCenterPanel(), BorderLayout.CENTER);
        
        // Bottom Panel with Calculations and Payment
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Invoice Info Panel (Left)
        JPanel invoiceInfoPanel = new JPanel(new GridLayout(3, 2, 10, 8));
        invoiceInfoPanel.setBackground(Color.WHITE);
        
        invoiceInfoPanel.add(new JLabel("Invoice No:"));
        lblInvoiceNo = new JLabel("Loading...");
        lblInvoiceNo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInvoiceNo.setForeground(new Color(220, 53, 69));
        invoiceInfoPanel.add(lblInvoiceNo);
        
        invoiceInfoPanel.add(new JLabel("Date:"));
        lblDate = new JLabel();
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        invoiceInfoPanel.add(lblDate);
        
        invoiceInfoPanel.add(new JLabel("Time:"));
        lblTime = new JLabel();
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        invoiceInfoPanel.add(lblTime);
        
        // Customer Selection Panel (Center)
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        customerPanel.add(new JLabel("Select Customer:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbCustomer = new JComboBox<>();
        cmbCustomer.setPreferredSize(new Dimension(250, 30));
        customerPanel.add(cmbCustomer, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        customerPanel.add(new JLabel("City:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbCity = new JComboBox<>();
        cmbCity.setPreferredSize(new Dimension(250, 30));
        customerPanel.add(cmbCity, gbc);
        
        // Arrears Balance Panel (Right)
        JPanel arrearsPanel = new JPanel(new BorderLayout(5, 5));
        arrearsPanel.setBackground(new Color(255, 248, 220));
        arrearsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblArrearsTitle = new JLabel("ARREARS_BALANCE:");
        lblArrearsTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        arrearsPanel.add(lblArrearsTitle, BorderLayout.NORTH);
        
        lblArrears = new JLabel("0.00");
        lblArrears.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblArrears.setForeground(new Color(220, 53, 69));
        lblArrears.setHorizontalAlignment(SwingConstants.CENTER);
        arrearsPanel.add(lblArrears, BorderLayout.CENTER);
        
        headerPanel.add(invoiceInfoPanel, BorderLayout.WEST);
        headerPanel.add(customerPanel, BorderLayout.CENTER);
        headerPanel.add(arrearsPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 240, 245));
        
        // Product Selection Panel
        centerPanel.add(createProductSelectionPanel(), BorderLayout.NORTH);
        
        // Invoice Items Table
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    private JPanel createProductSelectionPanel() {
        JPanel productPanel = new JPanel(new GridBagLayout());
        productPanel.setBackground(Color.WHITE);
        productPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Product Selection"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Product ID and Product Selection
        gbc.gridx = 0; gbc.gridy = 0;
        productPanel.add(new JLabel("Product ID:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtProductId = new JTextField();
        txtProductId.setPreferredSize(new Dimension(150, 30));
        productPanel.add(txtProductId, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        productPanel.add(new JLabel("Select Product:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7;
        cmbProduct = new JComboBox<>();
        cmbProduct.setPreferredSize(new Dimension(300, 30));
        productPanel.add(cmbProduct, gbc);
        
        // Row 2: Quantity and Capacity
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        productPanel.add(new JLabel("Quantity:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtQuantity = new JTextField("0");
        txtQuantity.setPreferredSize(new Dimension(150, 30));
        productPanel.add(txtQuantity, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        productPanel.add(new JLabel("Capacity:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.5;
        cmbCapacity = new JComboBox<>();
        cmbCapacity.setPreferredSize(new Dimension(200, 30));
        productPanel.add(cmbCapacity, gbc);
        
        gbc.gridx = 4; gbc.weightx = 0.2;
        lblSymbol = new JLabel();
        lblSymbol.setFont(new Font("Segoe UI", Font.BOLD, 14));
        productPanel.add(lblSymbol, gbc);
        
        // Action Buttons
        gbc.gridx = 5; gbc.gridy = 0; gbc.gridheight = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        btnAdd = new CustomButton("Add", new Color(40, 167, 69));
        btnRemove = new CustomButton("Remove", new Color(220, 53, 69));
        btnRemoveAll = new CustomButton("Remove All", new Color(255, 193, 7));
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnRemoveAll);
        
        productPanel.add(buttonPanel, gbc);
        
        return productPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Invoice Items"),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        // Create table columns
        String[] columns = {"PO", "Product Name", "Bar Code", "Quantity", 
                           "Symbol", "Unit Price", "Total Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblInvoiceItems = new CustomTable(tableModel);
        tblInvoiceItems.setRowHeight(30);
        
        JScrollPane scrollPane = new JScrollPane(tblInvoiceItems);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(240, 240, 245));
        
        // Calculation and Payment Panel (Left)
        bottomPanel.add(createCalculationPanel(), BorderLayout.CENTER);
        
        // Totals Display Panel (Right)
        bottomPanel.add(createTotalsPanel(), BorderLayout.EAST);
        
        return bottomPanel;
    }
    
    private JPanel createCalculationPanel() {
        JPanel calcPanel = new JPanel(new GridBagLayout());
        calcPanel.setBackground(Color.WHITE);
        calcPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Calculation & Payment"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Shipping Cost
        gbc.gridx = 0; gbc.gridy = row;
        calcPanel.add(new JLabel("Shipping Cost:"), gbc);
        gbc.gridx = 1;
        txtShippingCost = new JTextField("0");
        txtShippingCost.setPreferredSize(new Dimension(150, 30));
        calcPanel.add(txtShippingCost, gbc);
        
        // No of Quantity
        gbc.gridx = 2;
        calcPanel.add(new JLabel("No of Quantity:"), gbc);
        gbc.gridx = 3;
        JTextField txtNoOfQuantity = new JTextField("0");
        txtNoOfQuantity.setEditable(false);
        txtNoOfQuantity.setPreferredSize(new Dimension(150, 30));
        calcPanel.add(txtNoOfQuantity, gbc);
        
        row++;
        
        // Tax Amount
        gbc.gridx = 0; gbc.gridy = row;
        calcPanel.add(new JLabel("Tax Amount:"), gbc);
        gbc.gridx = 1;
        txtTaxAmount = new JTextField("0");
        txtTaxAmount.setPreferredSize(new Dimension(100, 30));
        calcPanel.add(txtTaxAmount, gbc);
        
        cmbTaxType = new JComboBox<>(new String[]{"%", "Fixed"});
        cmbTaxType.setPreferredSize(new Dimension(50, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        calcPanel.add(cmbTaxType, gbc);
        
        // No of Items
        gbc.gridx = 2; gbc.weightx = 0;
        calcPanel.add(new JLabel("No of Items:"), gbc);
        gbc.gridx = 3;
        JTextField txtNoOfItems = new JTextField("0");
        txtNoOfItems.setEditable(false);
        txtNoOfItems.setPreferredSize(new Dimension(150, 30));
        calcPanel.add(txtNoOfItems, gbc);
        
        row++;
        
        // Discount Rate
        gbc.gridx = 0; gbc.gridy = row;
        calcPanel.add(new JLabel("Discount Rate:"), gbc);
        gbc.gridx = 1;
        txtDiscountRate = new JTextField("0");
        txtDiscountRate.setPreferredSize(new Dimension(100, 30));
        calcPanel.add(txtDiscountRate, gbc);
        
        cmbDiscountType = new JComboBox<>(new String[]{"%", "Fixed"});
        cmbDiscountType.setPreferredSize(new Dimension(50, 30));
        calcPanel.add(cmbDiscountType, gbc);
        
        row++;
        
        // Payment Section
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        
        paymentPanel.add(new JLabel("Cash:"));
        JRadioButton rbCash = new JRadioButton("Cash");
        rbCash.setSelected(true);
        paymentPanel.add(rbCash);
        
        paymentPanel.add(new JLabel("Voucher:"));
        JRadioButton rbVoucher = new JRadioButton("Voucher");
        paymentPanel.add(rbVoucher);
        
        paymentPanel.add(new JLabel("Card Payment:"));
        JRadioButton rbCard = new JRadioButton("Card");
        paymentPanel.add(rbCard);
        
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(rbCash);
        paymentGroup.add(rbVoucher);
        paymentGroup.add(rbCard);
        
        calcPanel.add(paymentPanel, gbc);
        
        row++;
        
        // Paid Amount
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        calcPanel.add(new JLabel("Paid Amount:"), gbc);
        gbc.gridx = 1;
        txtPaidAmount = new JTextField("0");
        txtPaidAmount.setPreferredSize(new Dimension(150, 30));
        calcPanel.add(txtPaidAmount, gbc);
        
        // Payment Type
        gbc.gridx = 2;
        calcPanel.add(new JLabel("Payment Type:"), gbc);
        gbc.gridx = 3;
        cmbPaymentType = new JComboBox<>(new String[]{"CASH", "CARD", "CHEQUE", "ONLINE"});
        cmbPaymentType.setPreferredSize(new Dimension(150, 30));
        calcPanel.add(cmbPaymentType, gbc);
        
        row++;
        
        // Check/Reference Number
        gbc.gridx = 2; gbc.gridy = row;
        calcPanel.add(new JLabel("Ref No/Check No:"), gbc);
        gbc.gridx = 3;
        txtCheckNo = new JTextField("0");
        txtCheckNo.setPreferredSize(new Dimension(150, 30));
        calcPanel.add(txtCheckNo, gbc);
        
        row++;
        
        // Notes
        gbc.gridx = 0; gbc.gridy = row;
        calcPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtNotes = new JTextArea(2, 20);
        txtNotes.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesScroll.setPreferredSize(new Dimension(400, 50));
        calcPanel.add(notesScroll, gbc);
        
        return calcPanel;
    }
    
    private JPanel createTotalsPanel() {
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(new Color(248, 249, 250));
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        totalsPanel.setPreferredSize(new Dimension(300, 0));
        
        // Sub Total
        totalsPanel.add(createTotalRow("SUB TOTAL:", "0.0", Color.BLACK));
        totalsPanel.add(Box.createVerticalStrut(10));
        
        // Shipping Cost
        totalsPanel.add(createTotalRow("SHIPPING COST:", "0", Color.BLACK));
        totalsPanel.add(Box.createVerticalStrut(10));
        
        // Tax Amount
        totalsPanel.add(createTotalRow("TAX AMOUNT:", "0.0", Color.BLACK));
        totalsPanel.add(Box.createVerticalStrut(10));
        
        // Discount Amount
        totalsPanel.add(createTotalRow("DISCOUNT AMOUNT:", "0.0", Color.BLACK));
        totalsPanel.add(Box.createVerticalStrut(15));
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        totalsPanel.add(separator);
        totalsPanel.add(Box.createVerticalStrut(15));
        
        // Grand Total
        JPanel grandTotalPanel = new JPanel(new BorderLayout());
        grandTotalPanel.setBackground(new Color(40, 167, 69));
        grandTotalPanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        grandTotalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel lblGrandTotalText = new JLabel("GRAND TOTAL AMOUNT:");
        lblGrandTotalText.setForeground(Color.WHITE);
        lblGrandTotalText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        lblGrandTotal = new JLabel("00.00");
        lblGrandTotal.setForeground(Color.WHITE);
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGrandTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        grandTotalPanel.add(lblGrandTotalText, BorderLayout.NORTH);
        grandTotalPanel.add(lblGrandTotal, BorderLayout.CENTER);
        
        totalsPanel.add(grandTotalPanel);
        totalsPanel.add(Box.createVerticalStrut(15));
        
        // Total Due/Balance
        JPanel duePanel = new JPanel(new BorderLayout());
        duePanel.setBackground(new Color(220, 53, 69));
        duePanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        duePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel lblTotalDueText = new JLabel("TOTAL DUE/BALANCE:");
        lblTotalDueText.setForeground(Color.WHITE);
        lblTotalDueText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        lblTotalDue = new JLabel("00.00");
        lblTotalDue.setForeground(Color.WHITE);
        lblTotalDue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalDue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        duePanel.add(lblTotalDueText, BorderLayout.NORTH);
        duePanel.add(lblTotalDue, BorderLayout.CENTER);
        
        totalsPanel.add(duePanel);
        totalsPanel.add(Box.createVerticalStrut(20));
        
        // Pay & Print Button
        btnPayAndPrint = new CustomButton("PAY & PRINT", new Color(255, 193, 7));
        btnPayAndPrint.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPayAndPrint.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnPayAndPrint.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalsPanel.add(btnPayAndPrint);
        
        return totalsPanel;
    }
    
    private JPanel createTotalRow(String label, String value, Color color) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(new Color(248, 249, 250));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel lblText = new JLabel(label);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblText.setForeground(color);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValue.setForeground(color);
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        rowPanel.add(lblText, BorderLayout.WEST);
        rowPanel.add(lblValue, BorderLayout.EAST);
        
        return rowPanel;
    }
    
    private void setupEventListeners() {
        // Customer selection
        cmbCustomer.addActionListener(e -> onCustomerSelected());
        
        // Product selection
        txtProductId.addActionListener(e -> onProductIdEntered());
        cmbProduct.addActionListener(e -> onProductSelected());
        
        // Add/Remove buttons
        btnAdd.addActionListener(e -> addInvoiceItem());
        btnRemove.addActionListener(e -> removeSelectedItem());
        btnRemoveAll.addActionListener(e -> removeAllItems());
        
        // Calculation fields
        txtShippingCost.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotals();
            }
        });
        
        txtTaxAmount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotals();
            }
        });
        
        txtDiscountRate.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotals();
            }
        });
        
        txtPaidAmount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotals();
            }
        });
        
        // Pay and Print button
        btnPayAndPrint.addActionListener(e -> processPaymentAndPrint());
    }
    
    private void loadInitialData() {
        // Generate new invoice number
        currentInvoiceNo = salesController.generateInvoiceNumber();
        lblInvoiceNo.setText(currentInvoiceNo);
        
        // Set current date
        lblDate.setText(LocalDate.now().toString());
        
        // Load customers
        loadCustomers();
        
        // Load products
        loadProducts();
    }
    
    private void loadCustomers() {
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem("Select a Customer");
        
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer customer : customers) {
            cmbCustomer.addItem(customer.getName());
        }
    }
    
    private void loadProducts() {
        cmbProduct.removeAllItems();
        cmbProduct.addItem("Select a Product");
        
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            cmbProduct.addItem(product.getName());
        }
    }
    
    private void startTimeUpdater() {
        Timer timer = new Timer(1000, e -> {
            lblTime.setText(LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss a")
            ));
        });
        timer.start();
    }
    
    private void onCustomerSelected() {
        String selectedName = (String) cmbCustomer.getSelectedItem();
        if (selectedName != null && !selectedName.equals("Select a Customer")) {
            selectedCustomer = customerService.getCustomerByName(selectedName);
            if (selectedCustomer != null) {
                cmbCity.removeAllItems();
                cmbCity.addItem(selectedCustomer.getCity());
                
                // Load arrears balance
                arrearsBalance = salesController.getCustomerArrears(selectedCustomer.getId());
                lblArrears.setText(arrearsBalance.toString());
            }
        }
    }
    
    private void onProductIdEntered() {
        String productId = txtProductId.getText().trim();
        if (!productId.isEmpty()) {
            Product product = productService.getProductById(productId);
            if (product != null) {
                selectProductInComboBox(product.getName());
                loadProductDetails(product);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onProductSelected() {
        String selectedName = (String) cmbProduct.getSelectedItem();
        if (selectedName != null && !selectedName.equals("Select a Product")) {
            Product product = productService.getProductByName(selectedName);
            if (product != null) {
                loadProductDetails(product);
            }
        }
    }
    
    private void selectProductInComboBox(String productName) {
        for (int i = 0; i < cmbProduct.getItemCount(); i++) {
            if (cmbProduct.getItemAt(i).equals(productName)) {
                cmbProduct.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void loadProductDetails(Product product) {
        txtProductId.setText(product.getId());
        
        // Load capacities
        cmbCapacity.removeAllItems();
        // Assuming product has capacity options
        cmbCapacity.addItem(product.getDefaultCapacity());
        
        lblSymbol.setText(product.getUnit());
    }
    
    private void addInvoiceItem() {
        try {
            // Validate inputs
            if (cmbProduct.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a product!", "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String quantityStr = txtQuantity.getText().trim();
            if (quantityStr.isEmpty() || Integer.parseInt(quantityStr) <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid quantity!", "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get selected product
            String productName = (String) cmbProduct.getSelectedItem();
            Product product = productService.getProductByName(productName);
            
            int quantity = Integer.parseInt(quantityStr);
            
            // Check stock availability
            if (product.getStockQuantity() < quantity) {
                JOptionPane.showMessageDialog(this, 
                    "Insufficient stock! Available: " + product.getStockQuantity(), 
                    "Stock Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create invoice item
            InvoiceItem item = new InvoiceItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getSellingPrice());
            item.setTotalPrice(product.getSellingPrice().multiply(new BigDecimal(quantity)));
            
            invoiceItems.add(item);
            
            // Add to table
            Object[] rowData = {
                invoiceItems.size(),
                product.getName(),
                product.getBarcode(),
                quantity,
                product.getUnit(),
                product.getSellingPrice(),
                item.getTotalPrice()
            };
            tableModel.addRow(rowData);
            
            // Clear inputs
            clearProductInputs();
            
            // Recalculate totals
            calculateTotals();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric values!", "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeSelectedItem() {
        int selectedRow = tblInvoiceItems.getSelectedRow();
        if (selectedRow >= 0) {
            invoiceItems.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            calculateTotals();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to remove!", "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void removeAllItems() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove all items?", 
            "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            invoiceItems.clear();
            tableModel.setRowCount(0);
            calculateTotals();
        }
    }
    
    private void calculateTotals() {
        BigDecimal subTotal = BigDecimal.ZERO;
        
        // Calculate sub total
        for (InvoiceItem item : invoiceItems) {
            subTotal = subTotal.add(item.getTotalPrice());
        }
        
        // Get shipping cost
        BigDecimal shippingCost = getBigDecimalValue(txtShippingCost.getText(), BigDecimal.ZERO);
        
        // Calculate tax
        BigDecimal taxAmount = getBigDecimalValue(txtTaxAmount.getText(), BigDecimal.ZERO);
        BigDecimal taxValue;
        if (cmbTaxType.getSelectedItem().equals("%")) {
            taxValue = subTotal.multiply(taxAmount).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        } else {
            taxValue = taxAmount;
        }
        
        // Calculate discount
        BigDecimal discountRate = getBigDecimalValue(txtDiscountRate.getText(), BigDecimal.ZERO);
        BigDecimal discountValue;
        if (cmbDiscountType.getSelectedItem().equals("%")) {
            discountValue = subTotal.multiply(discountRate).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        } else {
            discountValue = discountRate;
        }
        
        // Calculate grand total
        BigDecimal grandTotal = subTotal.add(shippingCost).add(taxValue).subtract(discountValue);
        
        // Calculate due amount
        BigDecimal paidAmount = getBigDecimalValue(txtPaidAmount.getText(), BigDecimal.ZERO);
        BigDecimal dueAmount = grandTotal.subtract(paidAmount);
        
        // Update labels
        updateTotalLabel(lblSubTotal, subTotal);
        updateTotalLabel(lblShippingCost, shippingCost);
        updateTotalLabel(lblTaxAmount, taxValue);
        updateTotalLabel(lblDiscountAmount, discountValue);
        updateTotalLabel(lblGrandTotal, grandTotal);
        updateTotalLabel(lblTotalDue, dueAmount);
    }
    
    private BigDecimal getBigDecimalValue(String text, BigDecimal defaultValue) {
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private void updateTotalLabel(JLabel label, BigDecimal value) {
        if (label != null) {
            label.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
        }
    }
    
    private void processPaymentAndPrint() {
        try {
            // Validate customer selection
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a customer!", "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate invoice items
            if (invoiceItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please add at least one item!", "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Create invoice
            Invoice invoice = new Invoice();
            invoice.setInvoiceNo(currentInvoiceNo);
            invoice.setCustomer(selectedCustomer);
            invoice.setInvoiceDate(LocalDate.now());
            invoice.setItems(invoiceItems);
            
            // Set amounts
            BigDecimal grandTotal = new BigDecimal(lblGrandTotal.getText());
            BigDecimal paidAmount = getBigDecimalValue(txtPaidAmount.getText(), BigDecimal.ZERO);
            BigDecimal dueAmount = new BigDecimal(lblTotalDue.getText());
            
            invoice.setTotalAmount(grandTotal);
            invoice.setPaidAmount(paidAmount);
            invoice.setDueAmount(dueAmount);
            invoice.setShippingCost(getBigDecimalValue(txtShippingCost.getText(), BigDecimal.ZERO));
            invoice.setTaxAmount(new BigDecimal(lblTaxAmount.getText()));
            invoice.setDiscountAmount(new BigDecimal(lblDiscountAmount.getText()));
            
            // Set payment details
            invoice.setPaymentType((String) cmbPaymentType.getSelectedItem());
            invoice.setPaymentReference(txtCheckNo.getText());
            invoice.setNotes(txtNotes.getText());
            
            // Save invoice
            boolean saved = salesController.saveInvoice(invoice);
            
            if (saved) {
                JOptionPane.showMessageDialog(this, 
                    "Invoice saved successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Show invoice settings dialog
                InvoiceSettingsDialog settingsDialog = new InvoiceSettingsDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), invoice);
                settingsDialog.setVisible(true);
                
                // Reset form
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save invoice!", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error processing payment: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearProductInputs() {
        txtProductId.setText("");
        cmbProduct.setSelectedIndex(0);
        txtQuantity.setText("0");
        cmbCapacity.removeAllItems();
        lblSymbol.setText("");
    }
    
    private void resetForm() {
        // Generate new invoice number
        currentInvoiceNo = salesController.generateInvoiceNumber();
        lblInvoiceNo.setText(currentInvoiceNo);
        
        // Reset customer selection
        cmbCustomer.setSelectedIndex(0);
        cmbCity.removeAllItems();
        lblArrears.setText("0.00");
        selectedCustomer = null;
        
        // Clear product inputs
        clearProductInputs();
        
        // Clear invoice items
        invoiceItems.clear();
        tableModel.setRowCount(0);
        
        // Reset calculation fields
        txtShippingCost.setText("0");
        txtTaxAmount.setText("0");
        txtDiscountRate.setText("0");
        txtPaidAmount.setText("0");
        txtCheckNo.setText("0");
        txtNotes.setText("");
        
        // Reset totals
        calculateTotals();
    }
}