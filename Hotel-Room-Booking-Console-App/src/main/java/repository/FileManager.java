package repository;

import model.Booking;
import model.BookingStatus;
import model.Guest;
import model.Room;
import model.RoomType;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String ROOMS_FILE = DATA_DIR + "/rooms.csv";
    private static final String BOOKINGS_FILE = DATA_DIR + "/bookings.csv";

    public FileManager() {
        ensureDataDirectoryExists();
    }

    private void ensureDataDirectoryExists() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        File file = new File(ROOMS_FILE);

        if (!file.exists()) {
            // Populate default rooms if they don't exist
            rooms.add(new Room("101", RoomType.SINGLE));
            rooms.add(new Room("102", RoomType.SINGLE));
            rooms.add(new Room("201", RoomType.DOUBLE));
            rooms.add(new Room("202", RoomType.DOUBLE));
            rooms.add(new Room("301", RoomType.DELUXE));
            rooms.add(new Room("302", RoomType.DELUXE));
            rooms.add(new Room("401", RoomType.SUITE));
            rooms.add(new Room("402", RoomType.SUITE));
            saveRooms(rooms);
            return rooms;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("roomNumber")) {
                    continue; // Skip header or empty lines
                }
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String roomNumber = tokens[0];
                    RoomType type = RoomType.valueOf(tokens[1].toUpperCase());
                    BigDecimal price = new BigDecimal(tokens[2]);
                    int capacity = Integer.parseInt(tokens[3]);
                    rooms.add(new Room(roomNumber, type, price, capacity));
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading rooms, resetting to defaults. Details: " + e.getMessage());
            rooms.clear();
            rooms.add(new Room("101", RoomType.SINGLE));
            rooms.add(new Room("102", RoomType.SINGLE));
            rooms.add(new Room("201", RoomType.DOUBLE));
            rooms.add(new Room("202", RoomType.DOUBLE));
            rooms.add(new Room("301", RoomType.DELUXE));
            rooms.add(new Room("302", RoomType.DELUXE));
            rooms.add(new Room("401", RoomType.SUITE));
            rooms.add(new Room("402", RoomType.SUITE));
            saveRooms(rooms);
        }
        return rooms;
    }

    public void saveRooms(List<Room> rooms) {
        File file = new File(ROOMS_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("roomNumber,roomType,pricePerNight,maxCapacity");
            for (Room room : rooms) {
                pw.printf("%s,%s,%s,%d\n",
                        room.getRoomNumber(),
                        room.getType().name(),
                        room.getPricePerNight().toString(),
                        room.getMaxCapacity());
            }
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    public List<Booking> loadBookings(List<Room> rooms) {
        List<Booking> bookings = new ArrayList<>();
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) {
            return bookings;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("bookingId")) {
                    continue; // Skip header
                }
                String[] tokens = line.split(",");
                if (tokens.length >= 12) {
                    String bookingId = tokens[0];
                    String guestName = tokens[1];
                    String guestPhone = tokens[2];
                    String guestEmail = tokens[3];
                    String roomNumber = tokens[4];
                    LocalDate checkIn = LocalDate.parse(tokens[5]);
                    LocalDate checkOut = LocalDate.parse(tokens[6]);
                    int guestCount = Integer.parseInt(tokens[7]);
                    BookingStatus status = BookingStatus.valueOf(tokens[8].toUpperCase());
                    BigDecimal roomCharge = new BigDecimal(tokens[9]);
                    BigDecimal tax = new BigDecimal(tokens[10]);
                    BigDecimal totalAmount = new BigDecimal(tokens[11]);

                    // Resolve room references using room number
                    Room room = null;
                    for (Room r : rooms) {
                        if (r.getRoomNumber().equals(roomNumber)) {
                            room = r;
                            break;
                        }
                    }

                    if (room != null) {
                        Guest guest = new Guest(guestName, guestPhone, guestEmail);
                        Booking booking = new Booking(bookingId, guest, room, checkIn, checkOut, guestCount, status, roomCharge, tax, totalAmount);
                        bookings.add(booking);
                    }
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }

    public void saveBookings(List<Booking> bookings) {
        File file = new File(BOOKINGS_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("bookingId,guestName,guestPhone,guestEmail,roomNumber,checkInDate,checkOutDate,numberOfGuests,bookingStatus,roomCharge,tax,totalAmount");
            for (Booking booking : bookings) {
                // Ensure name doesn't contain commas that could break CSV parsing
                String safeName = booking.getGuest().getName().replace(",", " ");
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%d,%s,%s,%s,%s\n",
                        booking.getBookingId(),
                        safeName,
                        booking.getGuest().getPhone(),
                        booking.getGuest().getEmail(),
                        booking.getRoom().getRoomNumber(),
                        booking.getCheckIn().toString(),
                        booking.getCheckOut().toString(),
                        booking.getNumberOfGuests(),
                        booking.getStatus().name(),
                        booking.getRoomCharge().toString(),
                        booking.getTax().toString(),
                        booking.getTotalAmount().toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }
}
