package main.java.com.inventory.view;

//package com.inventory.view;

import com.inventory.controller.LoginController;
import com.inventory.model.User;
import com.inventory.service.AuthenticationService;
import com.inventory.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LoginFrame - Application login screen
 * Features:
 * - Username and password authentication
 * - Remember me functionality
 * - Password visibility toggle
 * - Company branding (logo and banner)
 * - Date and time display
 * - Power/Exit button
 * - Version information
 * - Modern gradient background
 */
public class LoginFrame extends JFrame {
    
    private LoginController loginController;
    private AuthenticationService authService;
    
    // Login Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkRememberMe;
    private JButton btnShowPassword;
    
    // Display Components
    private JLabel lblDate;
    private JLabel lblTime;
    private JLabel lblCompanyName;
    private JLabel lblVersion;
    private JButton btnPower;
    
    // Background Panel
    private JPanel backgroundPanel;
    
    public LoginFrame() {
        initializeServices();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        startTimeUpdater();
        
        setTitle("Inventory Management System - Login");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeServices() {
        this.loginController = new LoginController();
        this.authService = new AuthenticationService();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(52, 152, 219));
    }
    
    private void setupLayout() {
        // Create main background panel with gradient
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185),
                    getWidth(), getHeight(), new Color(52, 152, 219)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(null); // Using absolute positioning for precise control
        
        // Add header with date, time, and power button
        addHeaderComponents();
        
        // Add company branding
        addCompanyBranding();
        
        // Add login panel
        addLoginPanel();
        
        // Add footer
        addFooter();
        
        add(backgroundPanel, BorderLayout.CENTER);
    }
    
    private void addHeaderComponents() {
        // Date Label (Top Left)
        lblDate = new JLabel("2019-05-20");
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDate.setForeground(Color.WHITE);
        lblDate.setBounds(30, 15, 150, 25);
        backgroundPanel.add(lblDate);
        
        // Power Button (Top Right)
        btnPower = new JButton();
        btnPower.setBounds(820, 15, 50, 50);
        btnPower.setBackground(new Color(255, 193, 7));
        btnPower.setForeground(Color.WHITE);
        btnPower.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnPower.setText("⏻");
        btnPower.setFocusPainted(false);
        btnPower.setBorder(BorderFactory.createEmptyBorder());
        btnPower.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPower.setToolTipText("Exit Application");
        
        // Make it circular
        btnPower.setOpaque(false);
        btnPower.setContentAreaFilled(false);
        btnPower.setBorderPainted(false);
        
        backgroundPanel.add(btnPower);
        
        // Add custom painting for circular button
        btnPower.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPower.setForeground(new Color(255, 235, 59));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPower.setForeground(Color.WHITE);
            }
        });
    }
    
    private void addCompanyBranding() {
        // Company Name/Logo Box
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        logoPanel.setBounds(150, 120, 350, 120);
        
        lblCompanyName = new JLabel("YOUR COMPANY", SwingConstants.CENTER);
        lblCompanyName.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblCompanyName.setForeground(new Color(52, 58, 64));
        logoPanel.add(lblCompanyName, BorderLayout.CENTER);
        
        backgroundPanel.add(logoPanel);
    }
    
    private void addLoginPanel() {
        // Login Container Panel (White rounded panel)
        JPanel loginContainer = new JPanel();
        loginContainer.setLayout(null);
        loginContainer.setBackground(Color.WHITE);
        loginContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        loginContainer.setBounds(550, 120, 280, 320);
        
        // User Icon (Top)
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(new Color(220, 53, 69));
        iconPanel.setLayout(new BorderLayout());
        iconPanel.setBounds(90, -30, 100, 100);
        iconPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        
        JLabel lblUserIcon = new JLabel("👤", SwingConstants.CENTER);
        lblUserIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblUserIcon.setForeground(Color.WHITE);
        iconPanel.add(lblUserIcon, BorderLayout.CENTER);
        
        loginContainer.add(iconPanel);
        
        // "Login Here" Label
        JLabel lblLoginHere = new JLabel("Login Here", SwingConstants.CENTER);
        lblLoginHere.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLoginHere.setForeground(new Color(52, 152, 219));
        lblLoginHere.setBounds(40, 80, 200, 30);
        loginContainer.add(lblLoginHere);
        
        // Username Field
        txtUsername = new JTextField();
        txtUsername.setBounds(20, 130, 240, 40);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        loginContainer.add(txtUsername);
        
        // Password Field
        txtPassword = new JPasswordField();
        txtPassword.setBounds(20, 180, 240, 40);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        loginContainer.add(txtPassword);
        
        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setBounds(65, 240, 150, 45);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(52, 152, 219));
            }
        });
        
        loginContainer.add(btnLogin);
        
        backgroundPanel.add(loginContainer);
    }
    
    private void addFooter() {
        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(new Color(41, 128, 185));
        footerPanel.setBounds(0, 520, 900, 100);
        
        // Company Info
        JLabel lblCopyright = new JLabel("© 2019 Inventory Account System.  All rights reserved | Desing by  YourCompany.com");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(Color.WHITE);
        lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(Box.createVerticalStrut(20));
        footerPanel.add(lblCopyright);
        
        // Location
        JLabel lblLocation = new JLabel("📍 \"Sunethra\", Pahalakaduruwwa, NY 190150");
        lblLocation.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLocation.setForeground(Color.WHITE);
        lblLocation.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(Box.createVerticalStrut(5));
        footerPanel.add(lblLocation);
        
        // Contact Info
        JLabel lblContact = new JLabel("+94714784337 | +94779405409 | +94765500601");
        lblContact.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContact.setForeground(Color.WHITE);
        lblContact.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(Box.createVerticalStrut(5));
        footerPanel.add(lblContact);
        
        // Email
        JLabel lblEmail = new JLabel("codessindu@gmail.com  |  info@yourcompany.com");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEmail.setForeground(Color.WHITE);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(Box.createVerticalStrut(5));
        footerPanel.add(lblEmail);
        
        backgroundPanel.add(footerPanel);
        
        // Version Label (Bottom Right)
        lblVersion = new JLabel("Version 1.0");
        lblVersion.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblVersion.setForeground(new Color(255, 255, 255, 150));
        lblVersion.setBounds(800, 600, 80, 20);
        backgroundPanel.add(lblVersion);
    }
    
    private void setupEventListeners() {
        // Login button action
        btnLogin.addActionListener(e -> performLogin());
        
        // Enter key on password field
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        
        // Enter key on username field
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
        
        // Power button action
        btnPower.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }
    
    private void startTimeUpdater() {
        // Update date
        lblDate.setText(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // Timer to update time every second (if you add a time label)
        Timer timer = new Timer(1000, e -> {
            lblDate.setText(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        });
        timer.start();
    }
    
    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validate inputs
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter username!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter password!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        
        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);
        
        // Perform authentication in background thread
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    
                    if (user != null) {
                        // Login successful
                        loginSuccess(user);
                    } else {
                        // Login failed
                        loginFailed();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Login error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    btnLogin.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void loginSuccess(User user) {
        // Show success message
        JOptionPane.showMessageDialog(this,
            "Welcome, " + user.getUsername() + "!",
            "Login Successful",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Open main frame
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
            LoginFrame.this.dispose();
        });
    }
    
    private void loginFailed() {
        JOptionPane.showMessageDialog(this,
            "Invalid username or password!",
            "Login Failed",
            JOptionPane.ERROR_MESSAGE);
        
        // Clear password field
        txtPassword.setText("");
        txtPassword.requestFocus();
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show login frame
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}