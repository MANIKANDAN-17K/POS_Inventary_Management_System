package main.java.com.inventory.view.customer;


//package com.inventory.view.customer;

import com.inventory.controller.CustomerController;
import com.inventory.model.Customer;
import com.inventory.service.CustomerService;
import com.inventory.view.components.CustomButton;
import com.inventory.view.components.CustomTable;
import com.inventory.view.components.SearchTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * CustomerPanel - Main panel for customer management
 * Features:
 * - Display all customers in a table
 * - Search customers by name, contact, city
 * - Add new customers
 * - Edit existing customers
 * - View customer details
 * - Generate customer reports
 * - Export customer list to Excel
 */
public class CustomerPanel extends JPanel {
    
    private CustomerController customerController;
    private CustomerService customerService;
    
    // Search Components
    private SearchTextField txtSearch;
    private JButton btnSearch;
    
    // Table Components
    private CustomTable tblCustomers;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Action Buttons
    private CustomButton btnAddCustomer;
    private CustomButton btnEditCustomer;
    private CustomButton btnDeleteCustomer;
    private CustomButton btnViewDetails;
    private CustomButton btnCustomerReport;
    private CustomButton btnRefresh;
    private CustomButton btnExportExcel;
    
    // Selected Customer
    private Customer selectedCustomer;
    
    public CustomerPanel() {
        initializeServices();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCustomerData();
    }
    
    private void initializeServices() {
        this.customerController = new CustomerController();
        this.customerService = new CustomerService();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 245));
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }
    
    private void setupLayout() {
        // Header Panel with Search and Actions
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Center Panel with Customer Table
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Bottom Panel with Action Buttons
        add(createActionPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Customer Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(52, 58, 64));
        titlePanel.add(lblTitle);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(lblSearch);
        
        txtSearch = new SearchTextField("Search by name, contact, city...");
        txtSearch.setPreferredSize(new Dimension(300, 35));
        searchPanel.add(txtSearch);
        
        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(90, 35));
        searchPanel.add(btnSearch);
        
        btnExportExcel = new CustomButton("Save to Excel", new Color(40, 167, 69));
        btnExportExcel.setPreferredSize(new Dimension(130, 35));
        searchPanel.add(btnExportExcel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                           "All Customers", TitledBorder.LEFT, TitledBorder.TOP,
                           new Font("Segoe UI", Font.BOLD, 13)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Create table columns based on the screenshots
        String[] columns = {
            "ID", "Customer Name", "Office/Office No", "Customer Contact Person",
            "Billing Address", "Shipping Address", "City", "Bank", "Account No",
            "Contact Person", "Person Email", "Person Phone", "Mobile Number",
            "Private Note", "Online", "Other Info", "Private", "Arrears"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 17) { // Arrears column
                    return Double.class;
                }
                return String.class;
            }
        };
        
        tblCustomers = new CustomTable(tableModel);
        tblCustomers.setRowHeight(35);
        tblCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCustomers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCustomers.getTableHeader().setBackground(new Color(52, 58, 64));
        tblCustomers.getTableHeader().setForeground(Color.WHITE);
        tblCustomers.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        tblCustomers.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tblCustomers.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        tblCustomers.getColumnModel().getColumn(2).setPreferredWidth(120); // Office
        tblCustomers.getColumnModel().getColumn(3).setPreferredWidth(150); // Contact Person
        tblCustomers.getColumnModel().getColumn(6).setPreferredWidth(100); // City
        tblCustomers.getColumnModel().getColumn(12).setPreferredWidth(120); // Mobile
        tblCustomers.getColumnModel().getColumn(17).setPreferredWidth(100); // Arrears
        
        // Enable sorting
        sorter = new TableRowSorter<>(tableModel);
        tblCustomers.setRowSorter(sorter);
        
        // Selection mode
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tblCustomers);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Customer count label
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        countPanel.setBackground(Color.WHITE);
        
        JLabel lblCount = new JLabel("Total Customers: 0");
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCount.setForeground(new Color(52, 58, 64));
        countPanel.add(lblCount);
        
        tablePanel.add(countPanel, BorderLayout.SOUTH);
        
        return tablePanel;
    }
    
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionPanel.setBackground(new Color(248, 249, 250));
        actionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        
        // Add Customer Button
        btnAddCustomer = new CustomButton("Add Customer", new Color(40, 167, 69));
        btnAddCustomer.setPreferredSize(new Dimension(140, 40));
        btnAddCustomer.setIcon(createIcon("➕"));
        actionPanel.add(btnAddCustomer);
        
        // Edit Customer Button
        btnEditCustomer = new CustomButton("Edit Customer", new Color(0, 123, 255));
        btnEditCustomer.setPreferredSize(new Dimension(140, 40));
        btnEditCustomer.setIcon(createIcon("✏️"));
        btnEditCustomer.setEnabled(false);
        actionPanel.add(btnEditCustomer);
        
        // Delete Customer Button
        btnDeleteCustomer = new CustomButton("Delete", new Color(220, 53, 69));
        btnDeleteCustomer.setPreferredSize(new Dimension(120, 40));
        btnDeleteCustomer.setIcon(createIcon("🗑️"));
        btnDeleteCustomer.setEnabled(false);
        actionPanel.add(btnDeleteCustomer);
        
        // View Details Button
        btnViewDetails = new CustomButton("View Details", new Color(23, 162, 184));
        btnViewDetails.setPreferredSize(new Dimension(130, 40));
        btnViewDetails.setIcon(createIcon("👁️"));
        btnViewDetails.setEnabled(false);
        actionPanel.add(btnViewDetails);
        
        // Customer Report Button
        btnCustomerReport = new CustomButton("Customer Report", new Color(255, 193, 7));
        btnCustomerReport.setPreferredSize(new Dimension(150, 40));
        btnCustomerReport.setIcon(createIcon("📊"));
        actionPanel.add(btnCustomerReport);
        
        // Refresh Button
        btnRefresh = new CustomButton("Refresh", new Color(108, 117, 125));
        btnRefresh.setPreferredSize(new Dimension(110, 40));
        btnRefresh.setIcon(createIcon("🔄"));
        actionPanel.add(btnRefresh);
        
        return actionPanel;
    }
    
    private ImageIcon createIcon(String emoji) {
        // Create a simple text-based icon
        return null; // Can be replaced with actual icons
    }
    
    private void setupEventListeners() {
        // Search functionality
        txtSearch.addActionListener(e -> performSearch());
        btnSearch.addActionListener(e -> performSearch());
        
        // Add real-time search
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
        });
        
        // Table selection listener
        tblCustomers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onCustomerSelected();
            }
        });
        
        // Double-click to edit
        tblCustomers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editCustomer();
                }
            }
        });
        
        // Action buttons
        btnAddCustomer.addActionListener(e -> addCustomer());
        btnEditCustomer.addActionListener(e -> editCustomer());
        btnDeleteCustomer.addActionListener(e -> deleteCustomer());
        btnViewDetails.addActionListener(e -> viewCustomerDetails());
        btnCustomerReport.addActionListener(e -> openCustomerReport());
        btnRefresh.addActionListener(e -> loadCustomerData());
        btnExportExcel.addActionListener(e -> exportToExcel());
    }
    
    private void loadCustomerData() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Load customers from database
            List<Customer> customers = customerService.getAllCustomers();
            
            // Populate table
            for (Customer customer : customers) {
                Object[] rowData = {
                    customer.getId(),
                    customer.getName(),
                    customer.getOfficeNumber(),
                    customer.getContactPerson(),
                    customer.getBillingAddress(),
                    customer.getShippingAddress(),
                    customer.getCity(),
                    customer.getBankName(),
                    customer.getAccountNumber(),
                    customer.getContactPerson(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getMobileNumber(),
                    customer.getPrivateNote(),
                    customer.getOnlineInfo(),
                    customer.getOtherInfo(),
                    customer.isPrivate() ? "Yes" : "No",
                    customer.getArrears() != null ? customer.getArrears().doubleValue() : 0.0
                };
                tableModel.addRow(rowData);
            }
            
            // Update count
            updateCustomerCount();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading customer data: " + ex.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
        
        updateCustomerCount();
    }
    
    private void onCustomerSelected() {
        int selectedRow = tblCustomers.getSelectedRow();
        
        if (selectedRow >= 0) {
            // Convert view index to model index (important when sorting/filtering)
            int modelRow = tblCustomers.convertRowIndexToModel(selectedRow);
            
            // Get customer ID
            String customerId = tableModel.getValueAt(modelRow, 0).toString();
            
            // Load selected customer
            selectedCustomer = customerService.getCustomerById(customerId);
            
            // Enable action buttons
            btnEditCustomer.setEnabled(true);
            btnDeleteCustomer.setEnabled(true);
            btnViewDetails.setEnabled(true);
        } else {
            selectedCustomer = null;
            btnEditCustomer.setEnabled(false);
            btnDeleteCustomer.setEnabled(false);
            btnViewDetails.setEnabled(false);
        }
    }
    
    private void addCustomer() {
        CustomerFormDialog dialog = new CustomerFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Add New Customer",
            null
        );
        
        dialog.setVisible(true);
        
        // Refresh table if customer was added
        if (dialog.isCustomerSaved()) {
            loadCustomerData();
        }
    }
    
    private void editCustomer() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a customer to edit!",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        CustomerFormDialog dialog = new CustomerFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Edit Customer",
            selectedCustomer
        );
        
        dialog.setVisible(true);
        
        // Refresh table if customer was updated
        if (dialog.isCustomerSaved()) {
            loadCustomerData();
        }
    }
    
    private void deleteCustomer() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a customer to delete!",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete customer: " + selectedCustomer.getName() + "?\n" +
            "This action cannot be undone!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = customerController.deleteCustomer(selectedCustomer.getId());
                
                if (deleted) {
                    JOptionPane.showMessageDialog(this,
                        "Customer deleted successfully!",
                        "Delete Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadCustomerData();
                    selectedCustomer = null;
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete customer!",
                        "Delete Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error deleting customer: " + ex.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewCustomerDetails() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a customer to view!",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create and show customer details dialog
        JDialog detailsDialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Customer Details - " + selectedCustomer.getName(),
            true
        );
        
        detailsDialog.setLayout(new BorderLayout(10, 10));
        detailsDialog.setSize(600, 500);
        detailsDialog.setLocationRelativeTo(this);
        
        // Create details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);
        
        // Add customer details
        addDetailRow(detailsPanel, "Customer Name:", selectedCustomer.getName());
        addDetailRow(detailsPanel, "Office Number:", selectedCustomer.getOfficeNumber());
        addDetailRow(detailsPanel, "Contact Person:", selectedCustomer.getContactPerson());
        addDetailRow(detailsPanel, "Email:", selectedCustomer.getEmail());
        addDetailRow(detailsPanel, "Phone:", selectedCustomer.getPhone());
        addDetailRow(detailsPanel, "Mobile:", selectedCustomer.getMobileNumber());
        addDetailRow(detailsPanel, "City:", selectedCustomer.getCity());
        addDetailRow(detailsPanel, "Billing Address:", selectedCustomer.getBillingAddress());
        addDetailRow(detailsPanel, "Shipping Address:", selectedCustomer.getShippingAddress());
        addDetailRow(detailsPanel, "Bank:", selectedCustomer.getBankName());
        addDetailRow(detailsPanel, "Account No:", selectedCustomer.getAccountNumber());
        addDetailRow(detailsPanel, "Arrears:", selectedCustomer.getArrears() != null ? 
                    selectedCustomer.getArrears().toString() : "0.00");
        
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        detailsDialog.add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(btnClose);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 5));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLabel.setPreferredSize(new Dimension(150, 25));
        
        JLabel lblValue = new JLabel(value != null ? value : "N/A");
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        rowPanel.add(lblLabel, BorderLayout.WEST);
        rowPanel.add(lblValue, BorderLayout.CENTER);
        
        panel.add(rowPanel);
        panel.add(Box.createVerticalStrut(5));
    }
    
    private void openCustomerReport() {
        CustomerReportDialog reportDialog = new CustomerReportDialog(
            (Frame) SwingUtilities.getWindowAncestor(this)
        );
        reportDialog.setVisible(true);
    }
    
    private void exportToExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Customer List");
            fileChooser.setSelectedFile(new java.io.File("CustomerList.xlsx"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                
                // Export to Excel
                com.inventory.util.ExcelExporter exporter = new com.inventory.util.ExcelExporter();
                exporter.exportCustomerList(
                    customerService.getAllCustomers(),
                    file.getAbsolutePath()
                );
                
                JOptionPane.showMessageDialog(this,
                    "Customer list exported successfully!",
                    "Export Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error exporting to Excel: " + ex.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCustomerCount() {
        int visibleRows = tblCustomers.getRowCount();
        int totalRows = tableModel.getRowCount();
        
        String countText = visibleRows == totalRows ?
            "Total Customers: " + totalRows :
            "Showing " + visibleRows + " of " + totalRows + " customers";
        
        // Update count label (would need reference to the label)
        // lblCount.setText(countText);
    }
    
    /**
     * Public method to refresh data (can be called from parent)
     */
    public void refreshData() {
        loadCustomerData();
    }
    
    /**
     * Get selected customer
     */
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }
}