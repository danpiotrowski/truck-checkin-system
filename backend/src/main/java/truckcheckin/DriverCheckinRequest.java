package truckcheckin;

/*
 * This class represents the JSON sent from React
 * when a driver checks in.
 *
 * The driver should NOT enter:
 * - internal database ID
 * - pickup date
 *
 * The backend automatically uses today's date.
 */
public class DriverCheckinRequest {

    private String loadNumber;

    private String driverFirstName;
    private String driverLastName;
    private String truckingCompany;
    private String phoneNumber;
    private String trailerNumber;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public void setLoadNumber(String loadNumber) {
        this.loadNumber = loadNumber;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public void setTruckingCompany(String truckingCompany) {
        this.truckingCompany = truckingCompany;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setTrailerNumber(String trailerNumber) {
        this.trailerNumber = trailerNumber;
    }
}