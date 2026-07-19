package model;

import java.math.BigDecimal;

public class Room {
    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private int maxCapacity;

    public Room(String roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = type.getBasePrice();
        this.maxCapacity = type.getMaxCapacity();
    }

    public Room(String roomNumber, RoomType type, BigDecimal pricePerNight, int maxCapacity) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.maxCapacity = maxCapacity;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String toString() {
        return String.format("Room #%s [%-6s] - $%s/night (Max Capacity: %d)", 
                roomNumber, type.getDisplayName(), pricePerNight.toString(), maxCapacity);
    }
}
