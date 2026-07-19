package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private String bookingId;
    private Guest guest;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numberOfGuests;
    private BookingStatus status;
    
    // Financial details
    private long numberOfNights;
    private BigDecimal roomCharge;
    private BigDecimal tax;
    private BigDecimal totalAmount;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax

    public Booking(String bookingId, Guest guest, Room room, LocalDate checkIn, LocalDate checkOut, int numberOfGuests) {
        this.bookingId = bookingId;
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.status = BookingStatus.CONFIRMED;
        calculateFinancials();
    }

    public Booking(String bookingId, Guest guest, Room room, LocalDate checkIn, LocalDate checkOut, 
                   int numberOfGuests, BookingStatus status, BigDecimal roomCharge, BigDecimal tax, BigDecimal totalAmount) {
        this.bookingId = bookingId;
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.roomCharge = roomCharge;
        this.tax = tax;
        this.totalAmount = totalAmount;
    }

    public void calculateFinancials() {
        this.numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (this.numberOfNights <= 0) {
            this.numberOfNights = 1; // Minimum charge of 1 night if check-in and check-out are same day
        }
        
        BigDecimal nightsBD = BigDecimal.valueOf(numberOfNights);
        this.roomCharge = room.getPricePerNight().multiply(nightsBD).setScale(2, RoundingMode.HALF_UP);
        this.tax = roomCharge.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        this.totalAmount = roomCharge.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        calculateFinancials();
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
        calculateFinancials();
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
        calculateFinancials();
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public long getNumberOfNights() {
        return numberOfNights;
    }

    public BigDecimal getRoomCharge() {
        return roomCharge;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return String.format("Booking ID: %s | Status: %s | Room: %s | Dates: %s to %s (%d nights) | Guest: %s | Total: $%s",
                bookingId, status.getDisplayName(), room.getRoomNumber(), checkIn, checkOut, numberOfNights, guest.getName(), totalAmount);
    }
}
