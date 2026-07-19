package main;

import model.Booking;
import model.BookingStatus;
import model.Room;
import model.RoomType;
import service.HotelService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("           HOTEL BOOKING SYSTEM INTEGRATION TESTS        ");
        System.out.println("=========================================================");

        int testsPassed = 0;
        int totalTests = 8;

        try {
            // Setup HotelService (will load default rooms and load/save to CSV files)
            HotelService service = new HotelService();

            // Clear previous bookings for clean test run
            service.getBookings().clear();

            // 1. Verify Rooms Pre-populated
            System.out.print("Test 1: Verify Rooms Pre-population... ");
            List<Room> rooms = service.getAllRooms();
            if (rooms.size() == 8) {
                System.out.println("PASSED (Found 8 rooms)");
                testsPassed++;
            } else {
                System.out.println("FAILED (Found " + rooms.size() + " rooms)");
            }

            // 2. Booking a Room Successfully
            System.out.print("Test 2: Book Room 101 (Single) Successfully... ");
            LocalDate checkIn1 = LocalDate.now().plusDays(10);
            LocalDate checkOut1 = LocalDate.now().plusDays(14); // 4 nights
            Booking b1 = service.createBooking("Alice Smith", "1234567890", "alice@example.com", "101", checkIn1, checkOut1, 1);
            if (b1 != null && b1.getBookingId().startsWith("BK-") && b1.getStatus() == BookingStatus.CONFIRMED) {
                System.out.println("PASSED (ID: " + b1.getBookingId() + ")");
                testsPassed++;
            } else {
                System.out.println("FAILED");
            }

            // 3. Verify Financial Calculation (Base Rate = $100.00, nights = 4, tax = 10%)
            System.out.print("Test 3: Verify Financial Calculations... ");
            BigDecimal expectedRoomCharge = new BigDecimal("400.00");
            BigDecimal expectedTax = new BigDecimal("40.00");
            BigDecimal expectedTotal = new BigDecimal("440.00");
            if (b1 != null && b1.getNumberOfNights() == 4 
                    && b1.getRoomCharge().compareTo(expectedRoomCharge) == 0
                    && b1.getTax().compareTo(expectedTax) == 0
                    && b1.getTotalAmount().compareTo(expectedTotal) == 0) {
                System.out.println("PASSED (Charge: $" + b1.getRoomCharge() + ", Tax: $" + b1.getTax() + ", Total: $" + b1.getTotalAmount() + ")");
                testsPassed++;
            } else {
                System.out.println("FAILED");
            }

            // 4. Overlap Booking Validation (Double booking of same room on overlapping dates should fail)
            System.out.print("Test 4: Attempt Overlapping Booking (Should Fail)... ");
            LocalDate checkInOverlap = LocalDate.now().plusDays(12);
            LocalDate checkOutOverlap = LocalDate.now().plusDays(15);
            try {
                service.createBooking("Bob Miller", "9876543210", "bob@example.com", "101", checkInOverlap, checkOutOverlap, 1);
                System.out.println("FAILED (Allowed booking overlapping dates)");
            } catch (IllegalArgumentException e) {
                System.out.println("PASSED (" + e.getMessage() + ")");
                testsPassed++;
            }

            // 5. Booking same room on non-overlapping dates (Checkout day overlap should succeed)
            System.out.print("Test 5: Book same room on checkout-day start (Should Succeed)... ");
            LocalDate checkInNonOverlap = LocalDate.now().plusDays(14);
            LocalDate checkOutNonOverlap = LocalDate.now().plusDays(16);
            Booking b2 = service.createBooking("Bob Miller", "9876543210", "bob@example.com", "101", checkInNonOverlap, checkOutNonOverlap, 1);
            if (b2 != null && b2.getBookingId().startsWith("BK-")) {
                System.out.println("PASSED (ID: " + b2.getBookingId() + ")");
                testsPassed++;
            } else {
                System.out.println("FAILED");
            }

            // 6. Max Capacity Constraint check
            System.out.print("Test 6: Booking exceeding room maximum capacity (Should Fail)... ");
            try {
                // Room 101 has a capacity of 1 (Single)
                service.createBooking("Charlie Brown", "5551234567", "charlie@example.com", "101", LocalDate.now().plusDays(20), LocalDate.now().plusDays(22), 2);
                System.out.println("FAILED (Allowed guest count of 2 for Single room)");
            } catch (IllegalArgumentException e) {
                System.out.println("PASSED (" + e.getMessage() + ")");
                testsPassed++;
            }

            // 7. Check-in & Check-out Status Flow
            System.out.print("Test 7: Guest Check-in & Check-out Flow... ");
            boolean checkInOk = service.checkInGuest(b1.getBookingId());
            boolean checkOutOk = service.checkOutGuest(b1.getBookingId());
            if (checkInOk && checkOutOk && b1.getStatus() == BookingStatus.COMPLETED) {
                System.out.println("PASSED (Status is now COMPLETED)");
                testsPassed++;
            } else {
                System.out.println("FAILED");
            }

            // 8. Cancellation of a Confirmed Booking
            System.out.print("Test 8: Cancel Booking... ");
            boolean cancelOk = service.cancelBooking(b2.getBookingId());
            if (cancelOk && b2.getStatus() == BookingStatus.CANCELLED) {
                System.out.println("PASSED (Status is now CANCELLED)");
                testsPassed++;
            } else {
                System.out.println("FAILED");
            }

            System.out.println("=========================================================");
            System.out.println("RESULTS: Passed " + testsPassed + " of " + totalTests + " tests.");
            System.out.println("=========================================================");

            if (testsPassed == totalTests) {
                System.out.println("\u001B[32m\u001B[1mALL TESTS PASSED SUCCESSFULLY!\u001B[0m");
            } else {
                System.out.println("\u001B[31m\u001B[1mSOME TESTS FAILED. PLEASE CHECK THE CODE.\u001B[0m");
            }

        } catch (Exception e) {
            System.out.println("UNEXPECTED ERROR DURING TESTING:");
            e.printStackTrace();
        }
    }
}
