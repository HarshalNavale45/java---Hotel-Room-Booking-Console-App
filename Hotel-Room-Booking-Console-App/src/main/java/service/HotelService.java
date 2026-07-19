package service;

import model.Booking;
import model.BookingStatus;
import model.Guest;
import model.Room;
import model.RoomType;
import repository.FileManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HotelService {
    private final List<Room> rooms;
    private final List<Booking> bookings;
    private final FileManager fileManager;

    public HotelService() {
        this.fileManager = new FileManager();
        this.rooms = fileManager.loadRooms();
        this.bookings = fileManager.loadBookings(rooms);
    }

    public List<Room> getAllRooms() {
        return rooms;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    /**
     * Checks if a specific room is available for the given date range.
     * Booking checkout morning is not charged, so check-out day is non-overlapping.
     */
    public boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        for (Booking booking : bookings) {
            // Only care about active bookings (CONFIRMED or IN_HOUSE)
            if (booking.getRoom().getRoomNumber().equals(room.getRoomNumber())
                    && (booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.IN_HOUSE)) {
                
                // Overlap formula: checkIn < bookingCheckOut AND checkOut > bookingCheckIn
                if (checkIn.isBefore(booking.getCheckOut()) && checkOut.isAfter(booking.getCheckIn())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds available rooms of a specific type for a date range.
     */
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getType() == type && isRoomAvailable(room, checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    /**
     * Returns a Room object by its room number.
     */
    public Room getRoomByNumber(String roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Creates a new booking.
     */
    public Booking createBooking(String guestName, String phone, String email, String roomNumber, 
                                 LocalDate checkIn, LocalDate checkOut, int guestCount) {
        Room room = getRoomByNumber(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room " + roomNumber + " does not exist.");
        }

        if (guestCount > room.getMaxCapacity()) {
            throw new IllegalArgumentException("Guest count (" + guestCount + ") exceeds room maximum capacity (" + room.getMaxCapacity() + ").");
        }

        if (!isRoomAvailable(room, checkIn, checkOut)) {
            throw new IllegalArgumentException("Room " + roomNumber + " is not available for the selected dates.");
        }

        String nextId = generateNextBookingId();
        Guest guest = new Guest(guestName, phone, email);
        Booking booking = new Booking(nextId, guest, room, checkIn, checkOut, guestCount);
        
        bookings.add(booking);
        fileManager.saveBookings(bookings);
        return booking;
    }

    /**
     * Generates a sequential booking ID (e.g. BK-10001, BK-10002).
     */
    private synchronized String generateNextBookingId() {
        int maxId = 10000;
        for (Booking booking : bookings) {
            try {
                String idPart = booking.getBookingId();
                if (idPart.startsWith("BK-")) {
                    int numericVal = Integer.parseInt(idPart.substring(3));
                    if (numericVal > maxId) {
                        maxId = numericVal;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
        return "BK-" + (maxId + 1);
    }

    /**
     * Returns a booking by its ID.
     */
    public Booking getBookingById(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bookingId)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Cancels an existing booking.
     */
    public boolean cancelBooking(String bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking != null && booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.CANCELLED);
            fileManager.saveBookings(bookings);
            return true;
        }
        return false;
    }

    /**
     * Checks in a guest for their booking.
     */
    public boolean checkInGuest(String bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking != null && booking.getStatus() == BookingStatus.CONFIRMED) {
            // Confirm we are on or after check-in date (for simulation simplicity, we allow check-in anytime)
            booking.setStatus(BookingStatus.IN_HOUSE);
            fileManager.saveBookings(bookings);
            return true;
        }
        return false;
    }

    /**
     * Checks out a guest and prints invoice.
     */
    public boolean checkOutGuest(String bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking != null && booking.getStatus() == BookingStatus.IN_HOUSE) {
            booking.setStatus(BookingStatus.COMPLETED);
            fileManager.saveBookings(bookings);
            return true;
        }
        return false;
    }
}
