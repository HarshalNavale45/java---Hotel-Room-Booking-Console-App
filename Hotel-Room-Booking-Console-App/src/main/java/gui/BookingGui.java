package gui;

import model.Booking;
import model.BookingStatus;
import model.Room;
import model.RoomType;
import service.HotelService;
import utility.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BookingGui extends JFrame {
    private final HotelService hotelService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Modern Palette Colors
    private static final Color COLOR_SLATE = new Color(30, 41, 59);    // Header Background
    private static final Color COLOR_BG = new Color(248, 250, 252);     // Light Gray Canvas
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_TEAL = new Color(13, 148, 136);    // Main Buttons Accent
    private static final Color COLOR_RED = new Color(239, 68, 68);      // Danger/Cancel Buttons
    private static final Color COLOR_TEXT_DARK = new Color(15, 23, 42); // Primary Text
    private static final Color COLOR_BORDER = new Color(226, 232, 240);  // Clean Border

    // Fonts
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    // Dashboard Metric Labels
    private JLabel lblTotalRoomsValue;
    private JLabel lblAvailableRoomsValue;
    private JLabel lblActiveBookingsValue;

    // Tables & Models
    private DefaultTableModel recentBookingsModel;
    private DefaultTableModel searchRoomsModel;
    private DefaultTableModel bookingsLedgerModel;
    private DefaultTableModel inventoryModel;

    private JTable tblSearchRooms;
    private JTable tblBookingsLedger;

    // Search Fields in Book Tab
    private JTextField txtCheckIn;
    private JTextField txtCheckOut;
    private JComboBox<RoomType> cbRoomType;
    private List<Room> currentSearchResults;

    // Ledger Search Fields
    private JTextField txtLedgerSearch;

    public BookingGui() {
        // Initialize Core Logic
        hotelService = new HotelService();

        // Setup Frame Properties
        setTitle("Hotel Room Booking System - Desktop Client");
        setSize(1050, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        // Build GUI Layout
        initComponentLayout();

        // Initial Load
        refreshDashboardMetrics();
        refreshLedgerTable();
        refreshInventoryTable();
    }

    private void initComponentLayout() {
        setLayout(new BorderLayout());

        // 1. Top Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_SLATE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("HOTEL ROOM BOOKING SYSTEM");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel subtitleLabel = new JLabel("Administrative Desk Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_HEADER);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("  Dashboard  ", createDashboardPanel());
        tabbedPane.addTab("  Search & Book Room  ", createSearchBookPanel());
        tabbedPane.addTab("  Manage Bookings  ", createManageBookingsPanel());
        tabbedPane.addTab("  Room Inventory  ", createInventoryPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ==========================================
    // TAB 1: DASHBOARD PANEL
    // ==========================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Metric Cards Grid
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsPanel.setBackground(COLOR_BG);

        metricsPanel.add(createMetricCard("TOTAL ROOMS", "8 Rooms", "Master Room Inventory Capacity", new Color(59, 130, 246)));
        
        lblAvailableRoomsValue = new JLabel("0 Rooms");
        metricsPanel.add(createDynamicMetricCard("TODAY'S VACANT ROOMS", lblAvailableRoomsValue, "Instantly bookable units", COLOR_TEAL));
        
        lblActiveBookingsValue = new JLabel("0 Bookings");
        metricsPanel.add(createDynamicMetricCard("ACTIVE RESERVATIONS", lblActiveBookingsValue, "Confirmed / In-house guest count", COLOR_SLATE));

        panel.add(metricsPanel, BorderLayout.NORTH);

        // Recent Bookings Panel
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBackground(COLOR_CARD_BG);
        recentPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblRecentTitle = new JLabel("RECENT BOOKING TRANSACTIONS");
        lblRecentTitle.setFont(FONT_HEADER);
        lblRecentTitle.setForeground(COLOR_TEXT_DARK);
        recentPanel.add(lblRecentTitle, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Guest Name", "Room Number", "Check-In", "Check-Out", "Amount ($)", "Status"};
        recentBookingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tblRecent = new JTable(recentBookingsModel);
        tblRecent.setFont(FONT_BODY);
        tblRecent.setRowHeight(25);
        tblRecent.getTableHeader().setFont(FONT_HEADER);
        JScrollPane scrollPane = new JScrollPane(tblRecent);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));
        recentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Spacer between metrics and table
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(COLOR_BG);
        centerWrapper.setBorder(new EmptyBorder(20, 0, 0, 0));
        centerWrapper.add(recentPanel, BorderLayout.CENTER);

        panel.add(centerWrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricCard(String title, String value, String sub, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(accentColor);
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(COLOR_TEXT_DARK);
        card.add(lblValue, BorderLayout.CENTER);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(100, 116, 139));
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDynamicMetricCard(String title, JLabel lblValue, String sub, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(accentColor);
        card.add(lblTitle, BorderLayout.NORTH);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(COLOR_TEXT_DARK);
        card.add(lblValue, BorderLayout.CENTER);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(100, 116, 139));
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    // ==========================================
    // TAB 2: SEARCH & BOOK PANEL
    // ==========================================
    private JPanel createSearchBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search Form Panel
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.setBackground(COLOR_CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Check-in
        JPanel p1 = new JPanel(new BorderLayout(0, 3));
        p1.setBackground(COLOR_CARD_BG);
        p1.add(new JLabel("Check-in Date (YYYY-MM-DD):"), BorderLayout.NORTH);
        txtCheckIn = new JTextField(LocalDate.now().toString(), 10);
        txtCheckIn.setFont(FONT_BODY);
        p1.add(txtCheckIn, BorderLayout.CENTER);
        formPanel.add(p1);

        // Check-out
        JPanel p2 = new JPanel(new BorderLayout(0, 3));
        p2.setBackground(COLOR_CARD_BG);
        p2.add(new JLabel("Check-out Date (YYYY-MM-DD):"), BorderLayout.NORTH);
        txtCheckOut = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
        txtCheckOut.setFont(FONT_BODY);
        p2.add(txtCheckOut, BorderLayout.CENTER);
        formPanel.add(p2);

        // Room Type
        JPanel p3 = new JPanel(new BorderLayout(0, 3));
        p3.setBackground(COLOR_CARD_BG);
        p3.add(new JLabel("Desired Room Type:"), BorderLayout.NORTH);
        cbRoomType = new JComboBox<>(RoomType.values());
        cbRoomType.setFont(FONT_BODY);
        p3.add(cbRoomType, BorderLayout.CENTER);
        formPanel.add(p3);

        // Search Action
        JButton btnSearch = new JButton("Search Availability");
        styleButton(btnSearch, COLOR_TEAL);
        btnSearch.addActionListener(e -> performRoomSearch());
        formPanel.add(btnSearch);

        panel.add(formPanel, BorderLayout.NORTH);

        // Search Results Table
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(COLOR_BG);
        resultsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        String[] cols = {"Room Number", "Room Type", "Price Per Night", "Max Capacity"};
        searchRoomsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSearchRooms = new JTable(searchRoomsModel);
        tblSearchRooms.setFont(FONT_BODY);
        tblSearchRooms.setRowHeight(25);
        tblSearchRooms.getTableHeader().setFont(FONT_HEADER);
        JScrollPane scroll = new JScrollPane(tblSearchRooms);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));
        resultsPanel.add(scroll, BorderLayout.CENTER);

        // Action controls
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(COLOR_BG);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton btnBook = new JButton("Book Selected Room");
        styleButton(btnBook, COLOR_SLATE);
        btnBook.addActionListener(e -> openBookRoomDialog());
        actionPanel.add(btnBook);
        resultsPanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(resultsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void performRoomSearch() {
        LocalDate checkIn = InputValidator.parseDate(txtCheckIn.getText().trim());
        LocalDate checkOut = InputValidator.parseDate(txtCheckOut.getText().trim());

        if (checkIn == null || checkOut == null) {
            JOptionPane.showMessageDialog(this, "Dates must follow the format YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!InputValidator.isValidDateRange(checkIn, checkOut)) {
            JOptionPane.showMessageDialog(this, "Check-out must be after check-in, and dates cannot be in the past.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RoomType type = (RoomType) cbRoomType.getSelectedItem();
        currentSearchResults = hotelService.findAvailableRooms(checkIn, checkOut, type);
        searchRoomsModel.setRowCount(0);

        for (Room room : currentSearchResults) {
            searchRoomsModel.addRow(new Object[]{
                    room.getRoomNumber(),
                    room.getType().getDisplayName(),
                    "$" + room.getPricePerNight(),
                    room.getMaxCapacity()
            });
        }

        if (currentSearchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No vacant rooms of type " + type.getDisplayName() + " found for selected dates.", "No Rooms Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openBookRoomDialog() {
        int selectedRow = tblSearchRooms.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an available room from the search results table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Room selectedRoom = currentSearchResults.get(selectedRow);
        LocalDate checkIn = LocalDate.parse(txtCheckIn.getText().trim(), dateFormatter);
        LocalDate checkOut = LocalDate.parse(txtCheckOut.getText().trim(), dateFormatter);

        // Display Booking Dialog Modal
        JDialog dialog = new JDialog(this, "Create Reservation: Room " + selectedRoom.getRoomNumber(), true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formGrid = new JPanel(new GridLayout(5, 2, 10, 15));
        formGrid.setBorder(new EmptyBorder(20, 20, 20, 20));
        formGrid.setBackground(Color.WHITE);

        formGrid.add(new JLabel("Guest Full Name:"));
        JTextField txtName = new JTextField();
        formGrid.add(txtName);

        formGrid.add(new JLabel("Phone (10 digits):"));
        JTextField txtPhone = new JTextField();
        formGrid.add(txtPhone);

        formGrid.add(new JLabel("Email Address:"));
        JTextField txtEmail = new JTextField();
        formGrid.add(txtEmail);

        formGrid.add(new JLabel("Number of Guests:"));
        JSpinner spinGuests = new JSpinner(new SpinnerNumberModel(1, 1, selectedRoom.getMaxCapacity(), 1));
        formGrid.add(spinGuests);

        // Static details labels
        formGrid.add(new JLabel("Total Rate Per Night:"));
        formGrid.add(new JLabel("$" + selectedRoom.getPricePerNight().toString()));

        dialog.add(formGrid, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(241, 245, 249));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnConfirm = new JButton("Confirm Booking");
        styleButton(btnConfirm, COLOR_TEAL);
        btnConfirm.addActionListener(e -> {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            int guestsCount = (Integer) spinGuests.getValue();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Guest name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!InputValidator.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(dialog, "Mobile number must contain exactly 10 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!InputValidator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Invalid email address format (e.g., test@domain.com).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Booking booking = hotelService.createBooking(name, phone, email, selectedRoom.getRoomNumber(), checkIn, checkOut, guestsCount);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Booking Successful! Assigned ID: " + booking.getBookingId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh Data
                refreshDashboardMetrics();
                refreshLedgerTable();
                performRoomSearch(); // Refresh available room list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Booking failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = new JButton("Cancel");
        styleButton(btnCancel, COLOR_RED);
        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnConfirm);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ==========================================
    // TAB 3: MANAGE BOOKINGS LEDGER PANEL
    // ==========================================
    private JPanel createManageBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Filtering Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(COLOR_CARD_BG);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        filterPanel.add(new JLabel("Search Ledger:"));
        txtLedgerSearch = new JTextField(20);
        txtLedgerSearch.setFont(FONT_BODY);
        filterPanel.add(txtLedgerSearch);

        JButton btnFilter = new JButton("Apply Filter");
        styleButton(btnFilter, COLOR_TEAL);
        btnFilter.addActionListener(e -> refreshLedgerTable());
        filterPanel.add(btnFilter);

        JButton btnClear = new JButton("Clear");
        styleButton(btnClear, COLOR_SLATE);
        btnClear.addActionListener(e -> {
            txtLedgerSearch.setText("");
            refreshLedgerTable();
        });
        filterPanel.add(btnClear);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Ledger Table
        String[] columns = {"Booking ID", "Guest Name", "Guest Phone", "Room #", "Check-In", "Check-Out", "Nights", "Total ($)", "Status"};
        bookingsLedgerModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBookingsLedger = new JTable(bookingsLedgerModel);
        tblBookingsLedger.setFont(FONT_BODY);
        tblBookingsLedger.setRowHeight(25);
        tblBookingsLedger.getTableHeader().setFont(FONT_HEADER);
        JScrollPane scroll = new JScrollPane(tblBookingsLedger);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setBackground(COLOR_BG);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnCheckIn = new JButton("Guest Check-In");
        styleButton(btnCheckIn, COLOR_TEAL);
        btnCheckIn.addActionListener(e -> triggerCheckIn());
        actionPanel.add(btnCheckIn);

        JButton btnCheckOut = new JButton("Guest Check-Out & Bill");
        styleButton(btnCheckOut, COLOR_TEAL);
        btnCheckOut.addActionListener(e -> triggerCheckOut());
        actionPanel.add(btnCheckOut);

        JButton btnCancel = new JButton("Cancel Reservation");
        styleButton(btnCancel, COLOR_RED);
        btnCancel.addActionListener(e -> triggerCancellation());
        actionPanel.add(btnCancel);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshLedgerTable() {
        String filter = txtLedgerSearch.getText().trim().toLowerCase();
        List<Booking> bookings = hotelService.getBookings();
        bookingsLedgerModel.setRowCount(0);

        List<Booking> filteredList = bookings.stream()
                .filter(b -> filter.isEmpty() 
                        || b.getBookingId().toLowerCase().contains(filter)
                        || b.getGuest().getName().toLowerCase().contains(filter))
                .collect(Collectors.toList());

        for (Booking b : filteredList) {
            bookingsLedgerModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getGuest().getName(),
                    b.getGuest().getPhone(),
                    b.getRoom().getRoomNumber(),
                    b.getCheckIn().toString(),
                    b.getCheckOut().toString(),
                    b.getNumberOfNights(),
                    "$" + b.getTotalAmount().toString(),
                    b.getStatus().getDisplayName()
            });
        }
    }

    private void triggerCheckIn() {
        int row = tblBookingsLedger.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking record from the ledger table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsLedgerModel.getValueAt(row, 0);
        Booking booking = hotelService.getBookingById(bookingId);

        if (booking.getStatus() == BookingStatus.IN_HOUSE) {
            JOptionPane.showMessageDialog(this, "Guest is already checked-in.", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            JOptionPane.showMessageDialog(this, "Check-in only allowed for Confirmed bookings.", "Invalid State", JOptionPane.ERROR_MESSAGE);
            return;
        }

        hotelService.checkInGuest(bookingId);
        JOptionPane.showMessageDialog(this, "Check-in complete! Room " + booking.getRoom().getRoomNumber() + " is now Occupied.", "Success", JOptionPane.INFORMATION_MESSAGE);
        
        refreshDashboardMetrics();
        refreshLedgerTable();
    }

    private void triggerCheckOut() {
        int row = tblBookingsLedger.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking record from the ledger table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsLedgerModel.getValueAt(row, 0);
        Booking booking = hotelService.getBookingById(bookingId);

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this, "Booking already checked-out and completed.", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (booking.getStatus() != BookingStatus.IN_HOUSE) {
            JOptionPane.showMessageDialog(this, "Check-out only allowed for In-House guests.", "Invalid State", JOptionPane.ERROR_MESSAGE);
            return;
        }

        hotelService.checkOutGuest(bookingId);
        
        // Show Invoice Dialog
        String invoiceMsg = String.format(
                "==========================================\n" +
                "               FINAL BILL INVOICE          \n" +
                "==========================================\n" +
                "Invoice ID    : INV-%s\n" +
                "Guest Name    : %s\n" +
                "Room Number   : %s (%s)\n" +
                "Nights Charge : %d nights x $%s = $%s\n" +
                "Taxes (10%%)   : $%s\n" +
                "------------------------------------------\n" +
                "GRAND TOTAL   : $%s\n" +
                "==========================================\n" +
                "Payment Status: PAID IN FULL\n",
                booking.getBookingId().substring(3),
                booking.getGuest().getName(),
                booking.getRoom().getRoomNumber(), booking.getRoom().getType().getDisplayName(),
                booking.getNumberOfNights(), booking.getRoom().getPricePerNight().toString(), booking.getRoomCharge().toString(),
                booking.getTax().toString(),
                booking.getTotalAmount().toString()
        );

        JOptionPane.showMessageDialog(this, new JTextArea(invoiceMsg), "Final Checkout Bill", JOptionPane.INFORMATION_MESSAGE);
        
        refreshDashboardMetrics();
        refreshLedgerTable();
    }

    private void triggerCancellation() {
        int row = tblBookingsLedger.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking record from the ledger table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsLedgerModel.getValueAt(row, 0);
        Booking booking = hotelService.getBookingById(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this, "Booking has already been cancelled.", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            JOptionPane.showMessageDialog(this, "Cancellation only allowed for Confirmed bookings.", "Invalid State", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel booking " + bookingId + "?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            hotelService.cancelBooking(bookingId);
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully. Room inventory released.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            refreshDashboardMetrics();
            refreshLedgerTable();
        }
    }

    // ==========================================
    // TAB 4: ROOM INVENTORY PANEL
    // ==========================================
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("HOTEL MASTER ROOM LEDGER");
        title.setFont(FONT_HEADER);
        title.setForeground(COLOR_TEXT_DARK);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Room Number", "Room Category", "Rate Per Night", "Max Capacity"};
        inventoryModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblInv = new JTable(inventoryModel);
        tblInv.setFont(FONT_BODY);
        tblInv.setRowHeight(25);
        tblInv.getTableHeader().setFont(FONT_HEADER);
        JScrollPane scroll = new JScrollPane(tblInv);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void refreshInventoryTable() {
        inventoryModel.setRowCount(0);
        List<Room> rooms = hotelService.getAllRooms();
        for (Room r : rooms) {
            inventoryModel.addRow(new Object[]{
                    r.getRoomNumber(),
                    r.getType().getDisplayName(),
                    "$" + r.getPricePerNight().toString(),
                    r.getMaxCapacity()
            });
        }
    }

    // ==========================================
    // CORE DYNAMIC UPDATE HELPERS
    // ==========================================
    private void refreshDashboardMetrics() {
        List<Booking> bookings = hotelService.getBookings();
        LocalDate today = LocalDate.now();

        // 1. Calculate Today's Vacant Rooms
        long occupiedCount = bookings.stream()
                .filter(b -> (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.IN_HOUSE)
                        && !today.isBefore(b.getCheckIn()) && today.isBefore(b.getCheckOut()))
                .map(b -> b.getRoom().getRoomNumber())
                .distinct()
                .count();
        long vacantToday = 8 - occupiedCount;
        lblAvailableRoomsValue.setText(vacantToday + " Rooms");

        // 2. Active Reservations Count
        long activeCount = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.IN_HOUSE)
                .count();
        lblActiveBookingsValue.setText(activeCount + " Bookings");

        // 3. Load Recent Bookings
        recentBookingsModel.setRowCount(0);
        int listSize = bookings.size();
        int count = 0;
        // Load latest 5 bookings
        for (int i = listSize - 1; i >= 0 && count < 5; i--) {
            Booking b = bookings.get(i);
            recentBookingsModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getGuest().getName(),
                    b.getRoom().getRoomNumber(),
                    b.getCheckIn().toString(),
                    b.getCheckOut().toString(),
                    "$" + b.getTotalAmount().toString(),
                    b.getStatus().getDisplayName()
            });
            count++;
        }
    }

    // Styling Utility
    private void styleButton(JButton button, Color bg) {
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(FONT_HEADER);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bg.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });
    }

    public static void main(String[] args) {
        // Run UI thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set native System Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new BookingGui().setVisible(true);
        });
    }
}
