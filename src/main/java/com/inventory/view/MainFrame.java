package main.java.com.inventory.view;


// package com.inventory.view;

import com.inventory.model.User;
import com.inventory.view.sales.SalesPanel;
import com.inventory.view.customer.CustomerPanel;
import com.inventory.view.invoice.InvoiceListPanel;
import com.inventory.view.supplier.SupplierPanel;
import com.inventory.view.product.ProductPanel;
import com.inventory.view.purchase.PurchasePanel;
import com.inventory.view.pobill.POBillPanel;
import com.inventory.view.returns.ReturnsPanel;
import com.inventory.view.account.AccountPanel;
import com.inventory.view.employee.EmployeePanel;
import com.inventory.view.reports.ReportsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MainFrame - Main application window with navigation and content panels
 * Features:
 * - Left sidebar navigation with icons
 * - Module buttons: Sales, Customer, Invoice, Supplier, Product, Purchase, PO/Bill, Returns, Account, Employee, Reports
 * - Top menu bar with File, Edit, Navigation, Others, Settings, About
 * - Status bar with user info and date/time
 * - Dynamic content area that changes based on selected module
 * - Logout functionality
 */
public class MainFrame extends JFrame {
    
    private User currentUser;
    
    // Navigation Components
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Menu Components
    private JMenuBar menuBar;
    
    // Status Bar Components
    private JLabel lblStatusUser;
    private JLabel lblStatusDateTime;
    
    // Navigation Buttons
    private JButton btnSales;
    private JButton btnCustomer;
    private JButton btnInvoice;
    private JButton btnSupplier;
    private JButton btnProduct;
    private JButton btnPurchase;
    private JButton btnPOBill;
    private JButton btnReturns;
    private JButton btnAccount;
    private JButton btnEmployee;
    private JButton btnReports;
    
    // Content Panels
    private SalesPanel salesPanel;
    private CustomerPanel customerPanel;
    private InvoiceListPanel invoicePanel;
    private SupplierPanel supplierPanel;
    private ProductPanel productPanel;
    private PurchasePanel purchasePanel;
    private POBillPanel poBillPanel;
    private ReturnsPanel returnsPanel;
    private AccountPanel accountPanel;
    private EmployeePanel employeePanel;
    private ReportsPanel reportsPanel;
    
    // Currently selected button
    private JButton selectedButton;
    
    public MainFrame(User user) {
        this.currentUser = user;
        
        initializeComponents();
        setupLayout();
        setupMenuBar();
        setupEventListeners();
        startTimeUpdater();
        
        // Set default panel
        showPanel("SALES");
        selectButton(btnSales);
        
        setTitle("Inventory Management System - " + user.getUsername());
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // Initialize card layout for content area
        cardLayout = new CardLayout();
    }
    
    private void setupLayout() {
        // Create main container
        JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
        mainContainer.setBackground(Color.WHITE);
        
        // Add sidebar
        mainContainer.add(createSidebar(), BorderLayout.WEST);
        
        // Add content area
        mainContainer.add(createContentArea(), BorderLayout.CENTER);
        
        add(mainContainer, BorderLayout.CENTER);
        
        // Add status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }
    
    private JPanel createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(52, 58, 64));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Logo/Brand Section
        JPanel brandPanel = new JPanel();
        brandPanel.setBackground(new Color(52, 58, 64));
        brandPanel.setMaximumSize(new Dimension(200, 100));
        brandPanel.setLayout(new BorderLayout());
        brandPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        
        JLabel lblBrand = new JLabel("EXPIRED PRODUCT", SwingConstants.CENTER);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBrand.setForeground(Color.WHITE);
        brandPanel.add(lblBrand, BorderLayout.CENTER);
        
        sidebarPanel.add(brandPanel);
        
        // Navigation Buttons
        btnSales = createNavButton("Sales", "💰", new Color(255, 193, 7));
        btnCustomer = createNavButton("Customer", "👥", new Color(52, 152, 219));
        btnInvoice = createNavButton("Invoice", "📄", new Color(52, 152, 219));
        btnSupplier = createNavButton("Supplier", "🏭", new Color(255, 152, 0));
        btnProduct = createNavButton("Product", "📦", new Color(76, 175, 80));
        btnPurchase = createNavButton("Purchase", "🛒", new Color(156, 39, 176));
        btnPOBill = createNavButton("PO/Bill", "📋", new Color(63, 81, 181));
        btnReturns = createNavButton("Return's", "↩️", new Color(244, 67, 54));
        btnAccount = createNavButton("Account", "💳", new Color(233, 30, 99));
        btnEmployee = createNavButton("Employee", "👤", new Color(0, 188, 212));
        btnReports = createNavButton("Reports", "📊", new Color(255, 87, 34));
        
        sidebarPanel.add(btnSales);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnCustomer);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnInvoice);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnSupplier);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnProduct);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnPurchase);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnPOBill);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnReturns);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnAccount);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnEmployee);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnReports);
        
        // Add glue to push everything to top
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Logout button at bottom
        JButton btnLogout = new JButton("🚪 I am baby");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(180, 40));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());
        sidebarPanel.add(btnLogout);
        sidebarPanel.add(Box.createVerticalStrut(10));
        
        return sidebarPanel;
    }
    
    private JButton createNavButton(String text, String emoji, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker()),
            new EmptyBorder(10, 15, 10, 15)
        ));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Set icon if available
        if (emoji != null && !emoji.isEmpty()) {
            button.setText(emoji + "  " + text);
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(color.brighter());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(color);
                }
            }
        });
        
        return button;
    }
    
    private JPanel createContentArea() {
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(new Color(240, 240, 245));
        
        // Content panel with card layout
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(240, 240, 245));
        
        // Initialize all panels
        salesPanel = new SalesPanel();
        customerPanel = new CustomerPanel();
        invoicePanel = new InvoiceListPanel();
        supplierPanel = new SupplierPanel();
        productPanel = new ProductPanel();
        purchasePanel = new PurchasePanel();
        poBillPanel = new POBillPanel();
        returnsPanel = new ReturnsPanel();
        accountPanel = new AccountPanel();
        employeePanel = new EmployeePanel();
        reportsPanel = new ReportsPanel();
        
        // Add panels to card layout
        contentPanel.add(salesPanel, "SALES");
        contentPanel.add(customerPanel, "CUSTOMER");
        contentPanel.add(invoicePanel, "INVOICE");
        contentPanel.add(supplierPanel, "SUPPLIER");
        contentPanel.add(productPanel, "PRODUCT");
        contentPanel.add(purchasePanel, "PURCHASE");
        contentPanel.add(poBillPanel, "POBILL");
        contentPanel.add(returnsPanel, "RETURNS");
        contentPanel.add(accountPanel, "ACCOUNT");
        contentPanel.add(employeePanel, "EMPLOYEE");
        contentPanel.add(reportsPanel, "REPORTS");
        
        contentContainer.add(contentPanel, BorderLayout.CENTER);
        
        return contentContainer;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(248, 249, 250));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        statusBar.setPreferredSize(new Dimension(0, 30));
        
        // Left side - User info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leftPanel.setBackground(new Color(248, 249, 250));
        
        lblStatusUser = new JLabel("User: " + currentUser.getUsername() + 
                                   " | Role: " + currentUser.getRole());
        lblStatusUser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatusUser.setForeground(new Color(52, 58, 64));
        leftPanel.add(lblStatusUser);
        
        statusBar.add(leftPanel, BorderLayout.WEST);
        
        // Right side - Date and Time
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setBackground(new Color(248, 249, 250));
        
        lblStatusDateTime = new JLabel();
        lblStatusDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatusDateTime.setForeground(new Color(52, 58, 64));
        rightPanel.add(lblStatusDateTime);
        
        statusBar.add(rightPanel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        
        // File Menu
        JMenu menuFile = new JMenu("File");
        menuFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem itemNew = new JMenuItem("New");
        JMenuItem itemOpen = new JMenuItem("Open");
        JMenuItem itemSave = new JMenuItem("Save");
        JMenuItem itemPrint = new JMenuItem("Print");
        JMenuItem itemExit = new JMenuItem("Exit");
        
        itemExit.addActionListener(e -> logout());
        
        menuFile.add(itemNew);
        menuFile.add(itemOpen);
        menuFile.add(itemSave);
        menuFile.addSeparator();
        menuFile.add(itemPrint);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        
        // Edit Menu
        JMenu menuEdit = new JMenu("Edit");
        menuEdit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem itemUndo = new JMenuItem("Undo");
        JMenuItem itemRedo = new JMenuItem("Redo");
        JMenuItem itemCut = new JMenuItem("Cut");
        JMenuItem itemCopy = new JMenuItem("Copy");
        JMenuItem itemPaste = new JMenuItem("Paste");
        
        menuEdit.add(itemUndo);
        menuEdit.add(itemRedo);
        menuEdit.addSeparator();
        menuEdit.add(itemCut);
        menuEdit.add(itemCopy);
        menuEdit.add(itemPaste);
        
        // Navigation Menu
        JMenu menuNavigation = new JMenu("Navigation");
        menuNavigation.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem itemSales = new JMenuItem("Sales");
        JMenuItem itemCustomer = new JMenuItem("Customer");
        JMenuItem itemInvoice = new JMenuItem("Invoice");
        JMenuItem itemSupplier = new JMenuItem("Supplier");
        JMenuItem itemProduct = new JMenuItem("Product");
        
        itemSales.addActionListener(e -> { showPanel("SALES"); selectButton(btnSales); });
        itemCustomer.addActionListener(e -> { showPanel("CUSTOMER"); selectButton(btnCustomer); });
        itemInvoice.addActionListener(e -> { showPanel("INVOICE"); selectButton(btnInvoice); });
        itemSupplier.addActionListener(e -> { showPanel("SUPPLIER"); selectButton(btnSupplier); });
        itemProduct.addActionListener(e -> { showPanel("PRODUCT"); selectButton(btnProduct); });
        
        menuNavigation.add(itemSales);
        menuNavigation.add(itemCustomer);
        menuNavigation.add(itemInvoice);
        menuNavigation.add(itemSupplier);
        menuNavigation.add(itemProduct);
        
        // Others Menu
        JMenu menuOthers = new JMenu("Others");
        menuOthers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem itemBackup = new JMenuItem("Backup Database");
        JMenuItem itemRestore = new JMenuItem("Restore Database");
        JMenuItem itemImport = new JMenuItem("Import Data");
        JMenuItem itemExport = new JMenuItem("Export Data");
        
        menuOthers.add(itemBackup);
        menuOthers.add(itemRestore);
        menuOthers.addSeparator();
        menuOthers.add(itemImport);
        menuOthers.add(itemExport);
        
        // Settings Menu
        JMenu menuSettings = new JMenu("Settings");
        menuSettings.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem itemPreferences = new JMenuItem("Preferences");
        JMenuItem itemUsers = new JMenuItem("User Management");
        JMenuItem itemCompany = new JMenuItem("Company Settings");
        JMenuItem itemDatabase = new JMenuItem("Database Settings");
        
        menuSettings.add(itemPreferences);
        menuSettings.add(itemUsers);
        menuSettings.add(itemCompany);
        menuSettings.add(itemDatabase);
        
        // About Menu
        JMenu menuAbout = new JMenu("About");
        menuAbout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem itemAboutApp = new JMenuItem("About Application");
        JMenuItem itemHelp = new JMenuItem("Help");
        JMenuItem itemVersion = new JMenuItem("Version Info");
        
        itemAboutApp.addActionListener(e -> showAboutDialog());
        
        menuAbout.add(itemAboutApp);
        menuAbout.add(itemHelp);
        menuAbout.add(itemVersion);
        
        // Add menus to menu bar
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuNavigation);
        menuBar.add(menuOthers);
        menuBar.add(menuSettings);
        menuBar.add(menuAbout);
        
        setJMenuBar(menuBar);
    }
    
    private void setupEventListeners() {
        // Navigation button listeners
        btnSales.addActionListener(e -> { showPanel("SALES"); selectButton(btnSales); });
        btnCustomer.addActionListener(e -> { showPanel("CUSTOMER"); selectButton(btnCustomer); });
        btnInvoice.addActionListener(e -> { showPanel("INVOICE"); selectButton(btnInvoice); });
        btnSupplier.addActionListener(e -> { showPanel("SUPPLIER"); selectButton(btnSupplier); });
        btnProduct.addActionListener(e -> { showPanel("PRODUCT"); selectButton(btnProduct); });
        btnPurchase.addActionListener(e -> { showPanel("PURCHASE"); selectButton(btnPurchase); });
        btnPOBill.addActionListener(e -> { showPanel("POBILL"); selectButton(btnPOBill); });
        btnReturns.addActionListener(e -> { showPanel("RETURNS"); selectButton(btnReturns); });
        btnAccount.addActionListener(e -> { showPanel("ACCOUNT"); selectButton(btnAccount); });
        btnEmployee.addActionListener(e -> { showPanel("EMPLOYEE"); selectButton(btnEmployee); });
        btnReports.addActionListener(e -> { showPanel("REPORTS"); selectButton(btnReports); });
    }
    
    private void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
    
    private void selectButton(JButton button) {
        // Reset previous selected button
        if (selectedButton != null) {
            selectedButton.setBackground(getOriginalColor(selectedButton));
        }
        
        // Set new selected button
        selectedButton = button;
        selectedButton.setBackground(selectedButton.getBackground().darker());
    }
    
    private Color getOriginalColor(JButton button) {
        if (button == btnSales) return new Color(255, 193, 7);
        if (button == btnCustomer) return new Color(52, 152, 219);
        if (button == btnInvoice) return new Color(52, 152, 219);
        if (button == btnSupplier) return new Color(255, 152, 0);
        if (button == btnProduct) return new Color(76, 175, 80);
        if (button == btnPurchase) return new Color(156, 39, 176);
        if (button == btnPOBill) return new Color(63, 81, 181);
        if (button == btnReturns) return new Color(244, 67, 54);
        if (button == btnAccount) return new Color(233, 30, 99);
        if (button == btnEmployee) return new Color(0, 188, 212);
        if (button == btnReports) return new Color(255, 87, 34);
        return Color.GRAY;
    }
    
    private void startTimeUpdater() {
        Timer timer = new Timer(1000, e -> {
            String dateTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss a"));
            lblStatusDateTime.setText(dateTime);
        });
        timer.start();
        
        // Initial update
        String dateTime = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss a"));
        lblStatusDateTime.setText(dateTime);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Close current frame
            dispose();
            
            // Open login frame
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
    
    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "About Inventory Management System", true);
        aboutDialog.setLayout(new BorderLayout(10, 10));
        aboutDialog.setSize(400, 300);
        aboutDialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel("Inventory Management System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblTitle);
        
        contentPanel.add(Box.createVerticalStrut(15));
        
        JLabel lblVersion = new JLabel("Version 1.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblVersion);
        
        contentPanel.add(Box.createVerticalStrut(10));
        
        JLabel lblCopyright = new JLabel("© 2019 YourCompany.com");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblCopyright);
        
        contentPanel.add(Box.createVerticalStrut(20));
        
        JTextArea txtAbout = new JTextArea(
            "A comprehensive inventory management system for small to medium businesses. " +
            "Features include sales tracking, customer management, supplier management, " +
            "product inventory, purchase orders, invoicing, and detailed reporting."
        );
        txtAbout.setWrapStyleWord(true);
        txtAbout.setLineWrap(true);
        txtAbout.setEditable(false);
        txtAbout.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtAbout.setBackground(Color.WHITE);
        contentPanel.add(txtAbout);
        
        aboutDialog.add(contentPanel, BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> aboutDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnClose);
        aboutDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        aboutDialog.setVisible(true);
    }
    
    /**
     * Get current logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Refresh data in all panels
     */
    public void refreshAllPanels() {
        if (salesPanel != null) salesPanel.refreshData();
        if (customerPanel != null) customerPanel.refreshData();
        // Add refresh calls for other panels as needed
    }
}