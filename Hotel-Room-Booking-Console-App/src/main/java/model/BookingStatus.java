package model;

public enum BookingStatus {
    CONFIRMED("Confirmed"),
    IN_HOUSE("In-House"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
