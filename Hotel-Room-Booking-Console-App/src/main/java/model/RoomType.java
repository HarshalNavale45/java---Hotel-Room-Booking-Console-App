package model;

import java.math.BigDecimal;

public enum RoomType {
    SINGLE("Single", new BigDecimal("100.00"), 1),
    DOUBLE("Double", new BigDecimal("150.00"), 2),
    DELUXE("Deluxe", new BigDecimal("220.00"), 3),
    SUITE("Suite", new BigDecimal("350.00"), 4);

    private final String displayName;
    private final BigDecimal basePrice;
    private final int maxCapacity;

    RoomType(String displayName, BigDecimal basePrice, int maxCapacity) {
        this.displayName = displayName;
        this.basePrice = basePrice;
        this.maxCapacity = maxCapacity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}
