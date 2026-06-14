package truckcheckin;

import java.time.LocalDate;

/*
 * This class represents one row on the shipper dashboard.
 *
 * It combines:
 * - load data
 * - driver check-in data
 * - dock door data
 */
public class DashboardLoadRow {

    private Long loadId;
    private String loadNumber;
    private LocalDate scheduledPickupDate;

    private Long dockDoorId;
    private String doorNumber;

    private String status;

    private String driverFirstName;
    private String driverLastName;
    private String truckingCompany;
    private String phoneNumber;
    private String trailerNumber;

    public DashboardLoadRow(
            Long loadId,
            String loadNumber,
            LocalDate scheduledPickupDate,
            Long dockDoorId,
            String doorNumber,
            String status,
            String driverFirstName,
            String driverLastName,
            String truckingCompany,
            String phoneNumber,
            String trailerNumber) {

        this.loadId = loadId;
        this.loadNumber = loadNumber;
        this.scheduledPickupDate = scheduledPickupDate;
        this.dockDoorId = dockDoorId;
        this.doorNumber = doorNumber;
        this.status = status;
        this.driverFirstName = driverFirstName;
        this.driverLastName = driverLastName;
        this.truckingCompany = truckingCompany;
        this.phoneNumber = phoneNumber;
        this.trailerNumber = trailerNumber;
    }

    public Long getLoadId() {
        return loadId;
    }

    public String getLoadNumber() {
        return loadNumber;
    }

    public LocalDate getScheduledPickupDate() {
        return scheduledPickupDate;
    }

    public Long getDockDoorId() {
        return dockDoorId;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public String getStatus() {
        return status;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }
}