package truckcheckin;

import java.time.LocalDateTime;

/*
 * DTO for the dock door visualization page.
 *
 * This combines:
 * - dock door data
 * - current load data
 * - driver check-in data
 *
 * React will use this to draw the door cards.
 */
public class DockDoorViewRow {

    private Long doorId;
    private String doorNumber;
    private Integer displayOrder;
    private String status;

    private Long currentLoadId;
    private String loadNumber;

    private String driverFirstName;
    private String driverLastName;
    private String truckingCompany;
    private String trailerNumber;

    private LocalDateTime availableSince;
    private LocalDateTime occupiedSince;
    private LocalDateTime downSince;
    private String downReason;
    private LocalDateTime lastStatusChangedAt;

    public DockDoorViewRow(
            Long doorId,
            String doorNumber,
            Integer displayOrder,
            String status,
            Long currentLoadId,
            String loadNumber,
            String driverFirstName,
            String driverLastName,
            String truckingCompany,
            String trailerNumber,
            LocalDateTime availableSince,
            LocalDateTime occupiedSince,
            LocalDateTime downSince,
            String downReason,
            LocalDateTime lastStatusChangedAt) {

        this.doorId = doorId;
        this.doorNumber = doorNumber;
        this.displayOrder = displayOrder;
        this.status = status;
        this.currentLoadId = currentLoadId;
        this.loadNumber = loadNumber;
        this.driverFirstName = driverFirstName;
        this.driverLastName = driverLastName;
        this.truckingCompany = truckingCompany;
        this.trailerNumber = trailerNumber;
        this.availableSince = availableSince;
        this.occupiedSince = occupiedSince;
        this.downSince = downSince;
        this.downReason = downReason;
        this.lastStatusChangedAt = lastStatusChangedAt;
    }

    public Long getDoorId() {
        return doorId;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public String getStatus() {
        return status;
    }

    public Long getCurrentLoadId() {
        return currentLoadId;
    }

    public String getLoadNumber() {
        return loadNumber;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public String getTruckingCompany() {
        return truckingCompany;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public LocalDateTime getAvailableSince() {
        return availableSince;
    }

    public LocalDateTime getOccupiedSince() {
        return occupiedSince;
    }

    public LocalDateTime getDownSince() {
        return downSince;
    }

    public String getDownReason() {
        return downReason;
    }

    public LocalDateTime getLastStatusChangedAt() {
        return lastStatusChangedAt;
    }
}