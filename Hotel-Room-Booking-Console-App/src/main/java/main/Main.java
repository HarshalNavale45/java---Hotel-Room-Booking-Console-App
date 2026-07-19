package main;

import model.Booking;
import model.BookingStatus;
import model.Room;
import model.RoomType;
import service.HotelService;
import utility.InputValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    // ANSI Console Colors
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE_BOLD = "\u001B[1;37m";

    private static final Scanner scanner = new Scanner(System.in);
    private static final HotelService hotelService = new HotelService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println(CYAN + BOLD + "=========================================================" + RESET);
        System.out.println(CYAN + BOLD + "     WELCOME TO THE HOTEL ROOM BOOKING CONSOLE SYSTEM    " + RESET);
        System.out.println(CYAN + BOLD + "=========================================================" + RESET);

        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = readLine(WHITE_BOLD + "Choose an option (1-8): " + RESET).trim();
            switch (choice) {
                case "1":
                    handleSearchRooms();
                    break;
                case "2":
                    handleBookRoom();
                    break;
                case "3":
                    handleViewBooking();
                    break;
                case "4":
                    handleGuestCheckIn();
                    break;
                case "5":
                    handleGuestCheckOut();
                    break;
                case "6":
                    handleCancelBooking();
                    break;
                case "7":
                    handleViewBookingHistory();
                    break;
                case "8":
                    exit = true;
                    System.out.println(GREEN + "\nThank you for using the Hotel Room Booking Console App! Goodbye." + RESET);
                    break;
                default:
                    System.out.println(RED + "Invalid choice! Please select an option between 1 and 8." + RESET);
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n" + CYAN + BOLD + "--------------------- MAIN MENU ---------------------" + RESET);
        System.out.println("1. " + BOLD + "Search Available Rooms" + RESET);
        System.out.println("2. " + BOLD + "Book a Room" + RESET);
        System.out.println("3. " + BOLD + "View Booking Details" + RESET);
        System.out.println("4. " + BOLD + "Guest Check-In" + RESET);
        System.out.println("5. " + BOLD + "Guest Check-Out & Generate Invoice" + RESET);
        System.out.println("6. " + BOLD + "Cancel Booking" + RESET);
        System.out.println("7. " + BOLD + "View Booking History" + RESET);
        System.out.println("8. " + RED + BOLD + "Exit" + RESET);
        System.out.println(CYAN + "-----------------------------------------------------" + RESET);
    }

    private static void handleSearchRooms() {
        System.out.println("\n" + CYAN + BOLD + ">>> Search Available Rooms <<<" + RESET);
        RoomType type = readRoomType();
        LocalDate checkIn = readValidDate("Enter Check-in Date (YYYY-MM-DD): ", LocalDate.now());
        LocalDate checkOut = readValidDate("Enter Check-out Date (YYYY-MM-DD): ", checkIn.plusDays(1));

        while (!InputValidator.isValidDateRange(checkIn, checkOut)) {
            System.out.println(RED + "Invalid date range! Checkout must be after checkin. Try again." + RESET);
            checkIn = readValidDate("Enter Check-in Date (YYYY-MM-DD): ", LocalDate.now());
            checkOut = readValidDate("Enter Check-out Date (YYYY-MM-DD): ", checkIn.plusDays(1));
        }

        List<Room> available = hotelService.findAvailableRooms(checkIn, checkOut, type);
        if (available.isEmpty()) {
            System.out.println(YELLOW + "No rooms of type " + type.getDisplayName() + " are available for the selected dates." + RESET);
        } else {
            System.out.println(GREEN + "\nAvailable " + type.getDisplayName() + " Rooms:" + RESET);
            printRoomsTable(available);
        }
    }

    private static void handleBookRoom() {
        System.out.println("\n" + CYAN + BOLD + ">>> Book a Room <<<" + RESET);
        RoomType type = readRoomType();
        LocalDate checkIn = readValidDate("Enter Check-in Date (YYYY-MM-DD): ", LocalDate.now());
        LocalDate checkOut = readValidDate("Enter Check-out Date (YYYY-MM-DD): ", checkIn.plusDays(1));

        while (!InputValidator.isValidDateRange(checkIn, checkOut)) {
            System.out.println(RED + "Invalid date range! Checkout must be after checkin. Try again." + RESET);
            checkIn = readValidDate("Enter Check-in Date (YYYY-MM-DD): ", LocalDate.now());
            checkOut = readValidDate("Enter Check-out Date (YYYY-MM-DD): ", checkIn.plusDays(1));
        }

        List<Room> available = hotelService.findAvailableRooms(checkIn, checkOut, type);
        if (available.isEmpty()) {
            System.out.println(YELLOW + "No rooms of type " + type.getDisplayName() + " are available for the selected dates." + RESET);
            return;
        }

        System.out.println(GREEN + "\nAvailable " + type.getDisplayName() + " Rooms:" + RESET);
        printRoomsTable(available);

        String roomNumber = "";
        Room selectedRoom = null;
        while (selectedRoom == null) {
            roomNumber = readLine("Enter the Room Number you wish to book (or type 'cancel' to exit): ").trim();
            if (roomNumber.equalsIgnoreCase("cancel")) {
                return;
            }
            selectedRoom = hotelService.getRoomByNumber(roomNumber);
            if (selectedRoom == null || selectedRoom.getType() != type || !hotelService.isRoomAvailable(selectedRoom, checkIn, checkOut)) {
                System.out.println(RED + "Invalid Room Number or room is not available. Please pick one from the list." + RESET);
                selectedRoom = null;
            }
        }

        // Capacity and Guest information collection
        int guestCount = readInteger("Enter Number of Guests: ", 1, selectedRoom.getMaxCapacity());

        String guestName = "";
        while (guestName.isEmpty()) {
            guestName = readLine("Enter Guest Full Name: ").trim();
            if (guestName.isEmpty()) {
                System.out.println(RED + "Guest name cannot be empty!" + RESET);
            }
        }

        String phone = "";
        while (!InputValidator.isValidPhone(phone)) {
            phone = readLine("Enter 10-Digit Mobile Number: ").trim();
            if (!InputValidator.isValidPhone(phone)) {
                System.out.println(RED + "Invalid mobile number. Please enter exactly 10 digits." + RESET);
            }
        }

        String email = "";
        while (!InputValidator.isValidEmail(email)) {
            email = readLine("Enter Guest Email: ").trim();
            if (!InputValidator.isValidEmail(email)) {
                System.out.println(RED + "Invalid email address formatting. Try again (e.g., example@domain.com)." + RESET);
            }
        }

        try {
            Booking booking = hotelService.createBooking(guestName, phone, email, roomNumber, checkIn, checkOut, guestCount);
            System.out.println(GREEN + BOLD + "\nBooking Successfully Confirmed!" + RESET);
            printBookingInvoice(booking);
        } catch (Exception e) {
            System.out.println(RED + "Booking failed: " + e.getMessage() + RESET);
        }
    }

    private static void handleViewBooking() {
        System.out.println("\n" + CYAN + BOLD + ">>> View Booking Details <<<" + RESET);
        String bookingId = readLine("Enter Booking ID: ").trim().toUpperCase();
        Booking booking = hotelService.getBookingById(bookingId);
        if (booking == null) {
            System.out.println(RED + "Booking ID not found." + RESET);
        } else {
            printBookingInvoice(booking);
        }
    }

    private static void handleGuestCheckIn() {
        System.out.println("\n" + CYAN + BOLD + ">>> Guest Check-In <<<" + RESET);
        String bookingId = readLine("Enter Booking ID for Check-in: ").trim().toUpperCase();
        Booking booking = hotelService.getBookingById(bookingId);
        if (booking == null) {
            System.out.println(RED + "Booking ID not found." + RESET);
            return;
        }

        if (booking.getStatus() == BookingStatus.IN_HOUSE) {
            System.out.println(YELLOW + "Guest is already checked-in (In-House)." + RESET);
        } else if (booking.getStatus() != BookingStatus.CONFIRMED) {
            System.out.println(RED + "Cannot check-in. Booking status is: " + booking.getStatus().getDisplayName() + RESET);
        } else {
            hotelService.checkInGuest(bookingId);
            System.out.println(GREEN + BOLD + "Check-in successful! Room " + booking.getRoom().getRoomNumber() + " is now Occupied by " + booking.getGuest().getName() + "." + RESET);
        }
    }

    private static void handleGuestCheckOut() {
        System.out.println("\n" + CYAN + BOLD + ">>> Guest Check-Out & Generate Invoice <<<" + RESET);
        String bookingId = readLine("Enter Booking ID for Check-out: ").trim().toUpperCase();
        Booking booking = hotelService.getBookingById(bookingId);
        if (booking == null) {
            System.out.println(RED + "Booking ID not found." + RESET);
            return;
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            System.out.println(YELLOW + "This booking is already completed/checked-out." + RESET);
        } else if (booking.getStatus() != BookingStatus.IN_HOUSE) {
            System.out.println(RED + "Cannot check-out. Guest must check-in first. Current status: " + booking.getStatus().getDisplayName() + RESET);
        } else {
            hotelService.checkOutGuest(bookingId);
            System.out.println(GREEN + BOLD + "Check-out successful! Invoice generated below:" + RESET);
            printFinalInvoice(booking);
        }
    }

    private static void handleCancelBooking() {
        System.out.println("\n" + CYAN + BOLD + ">>> Cancel Booking <<<" + RESET);
        String bookingId = readLine("Enter Booking ID to Cancel: ").trim().toUpperCase();
        Booking booking = hotelService.getBookingById(bookingId);
        if (booking == null) {
            System.out.println(RED + "Booking ID not found." + RESET);
            return;
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println(YELLOW + "This booking is already cancelled." + RESET);
        } else if (booking.getStatus() != BookingStatus.CONFIRMED) {
            System.out.println(RED + "Cannot cancel booking. Current status: " + booking.getStatus().getDisplayName() + RESET);
        } else {
            String confirm = readLine("Are you sure you want to cancel booking " + bookingId + "? (y/n): ");
            if (confirm.equalsIgnoreCase("y")) {
                hotelService.cancelBooking(bookingId);
                System.out.println(GREEN + "Booking " + bookingId + " has been successfully cancelled. The room is now released." + RESET);
            } else {
                System.out.println(YELLOW + "Cancellation aborted." + RESET);
            }
        }
    }

    private static void handleViewBookingHistory() {
        System.out.println("\n" + CYAN + BOLD + ">>> Booking History <<<" + RESET);
        List<Booking> bookings = hotelService.getBookings();
        if (bookings.isEmpty()) {
            System.out.println(YELLOW + "No booking records found." + RESET);
            return;
        }
        
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-18s | %-6s | %-10s | %-10s | %-5s | %-10s | %-12s |\n",
                "Booking ID", "Guest Name", "Room #", "Check-In", "Check-Out", "Nigts", "Total ($)", "Status");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        for (Booking b : bookings) {
            System.out.printf("| %-10s | %-18s | %-6s | %-10s | %-10s | %-5d | %-10s | %-12s |\n",
                    b.getBookingId(),
                    truncate(b.getGuest().getName(), 18),
                    b.getRoom().getRoomNumber(),
                    b.getCheckIn().toString(),
                    b.getCheckOut().toString(),
                    b.getNumberOfNights(),
                    b.getTotalAmount().toString(),
                    b.getStatus().getDisplayName());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
    }

    private static RoomType readRoomType() {
        while (true) {
            System.out.println("Select Room Type:");
            System.out.println("1. Single ($100.00)");
            System.out.println("2. Double ($150.00)");
            System.out.println("3. Deluxe ($220.00)");
            System.out.println("4. Suite  ($350.00)");
            String choice = readLine("Choice (1-4): ").trim();
            switch (choice) {
                case "1": return RoomType.SINGLE;
                case "2": return RoomType.DOUBLE;
                case "3": return RoomType.DELUXE;
                case "4": return RoomType.SUITE;
                default:
                    System.out.println(RED + "Invalid Room Type choice! Choose between 1 and 4." + RESET);
            }
        }
    }

    private static LocalDate readValidDate(String prompt, LocalDate minDate) {
        while (true) {
            String input = readLine(prompt).trim();
            LocalDate date = InputValidator.parseDate(input);
            if (date == null) {
                System.out.println(RED + "Invalid date format! Please enter in YYYY-MM-DD format." + RESET);
            } else if (date.isBefore(minDate)) {
                System.out.println(RED + "Selected date cannot be before " + minDate.toString() + "." + RESET);
            } else {
                return date;
            }
        }
    }

    private static int readInteger(String prompt, int min, int max) {
        while (true) {
            try {
                String input = readLine(prompt).trim();
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println(RED + "Value must be between " + min + " and " + max + "." + RESET);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input! Please enter a whole number." + RESET);
            }
        }
    }

    private static void printRoomsTable(List<Room> rooms) {
        System.out.println("------------------------------------------------------------");
        System.out.printf("| %-12s | %-12s | %-15s | %-8s |\n", "Room Number", "Room Type", "Price Per Night", "Max Occ.");
        System.out.println("------------------------------------------------------------");
        for (Room r : rooms) {
            System.out.printf("| %-12s | %-12s | $%-14s | %-8d |\n",
                    r.getRoomNumber(), r.getType().getDisplayName(), r.getPricePerNight().toString(), r.getMaxCapacity());
        }
        System.out.println("------------------------------------------------------------");
    }

    private static void printBookingInvoice(Booking booking) {
        System.out.println("\n==============================================");
        System.out.println("              BOOKING INVOICE                 ");
        System.out.println("==============================================");
        System.out.printf("Booking ID    : %s\n", booking.getBookingId());
        System.out.printf("Status        : %s\n", booking.getStatus().getDisplayName());
        System.out.println("----------------------------------------------");
        System.out.printf("Guest Name    : %s\n", booking.getGuest().getName());
        System.out.printf("Phone Number  : %s\n", booking.getGuest().getPhone());
        System.out.printf("Email         : %s\n", booking.getGuest().getEmail());
        System.out.println("----------------------------------------------");
        System.out.printf("Room Number   : %s (%s)\n", booking.getRoom().getRoomNumber(), booking.getRoom().getType().getDisplayName());
        System.out.printf("Occupants     : %d (Max Capacity: %d)\n", booking.getNumberOfGuests(), booking.getRoom().getMaxCapacity());
        System.out.printf("Check-In Date : %s\n", booking.getCheckIn());
        System.out.printf("Check-Out Date: %s\n", booking.getCheckOut());
        System.out.printf("Stay Duration : %d nights\n", booking.getNumberOfNights());
        System.out.println("----------------------------------------------");
        System.out.printf("Room Charge   : $%s ($%s x %d nights)\n", 
                booking.getRoomCharge().toString(), booking.getRoom().getPricePerNight().toString(), booking.getNumberOfNights());
        System.out.printf("Taxes (10%%)   : $%s\n", booking.getTax().toString());
        System.out.println("----------------------------------------------");
        System.out.printf("Total Amount  : $%s\n", booking.getTotalAmount().toString());
        System.out.println("==============================================\n");
    }

    private static void printFinalInvoice(Booking booking) {
        System.out.println("\n==============================================");
        System.out.println("             FINAL BILL / INVOICE             ");
        System.out.println("==============================================");
        System.out.printf("Invoice ID    : INV-%s\n", booking.getBookingId().substring(3));
        System.out.printf("Booking ID    : %s\n", booking.getBookingId());
        System.out.printf("Status        : %s\n", booking.getStatus().getDisplayName());
        System.out.println("----------------------------------------------");
        System.out.printf("Guest Name    : %s\n", booking.getGuest().getName());
        System.out.printf("Phone Number  : %s\n", booking.getGuest().getPhone());
        System.out.println("----------------------------------------------");
        System.out.printf("Room Number   : %s (%s)\n", booking.getRoom().getRoomNumber(), booking.getRoom().getType().getDisplayName());
        System.out.printf("Check-In Date : %s\n", booking.getCheckIn());
        System.out.printf("Check-Out Date: %s\n", booking.getCheckOut());
        System.out.printf("Total Nights  : %d nights\n", booking.getNumberOfNights());
        System.out.println("----------------------------------------------");
        System.out.printf("Room Subtotal : $%s\n", booking.getRoomCharge().toString());
        System.out.printf("Taxes (10%%)   : $%s\n", booking.getTax().toString());
        System.out.println("----------------------------------------------");
        System.out.printf("Grand Total   : $%s\n", booking.getTotalAmount().toString());
        System.out.println("Status        : PAID IN FULL\n");
        System.out.println("         THANK YOU FOR STAYING WITH US!       ");
        System.out.println("==============================================\n");
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
}
