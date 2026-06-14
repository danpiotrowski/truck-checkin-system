package truckcheckin;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * Represents one row in the driver_checkins table.
 *
 * This stores the information entered by the driver
 * when they check in for a load.
 */
@Entity
@Table(name = "driver_checkins")
public class DriverCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * This connects the driver check-in to loads.id.
     *
     * The driver does not type this number anymore.
     * Spring Boot finds the load by load number + pickup date,
     * then stores the matching load ID here.
     */
    @Column(name = "load_id")
    private Long loadId;

    @Column(name = "driver_first_name")
    private String driverFirstName;

    @Column(name = "driver_last_name")
    private String driverLastName;

    @Column(name = "trucking_company")
    private String truckingCompany;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "trailer_number")
    private String trailerNumber;

    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;

    /*
     * active = true means this check-in is still current.
     */
    private Boolean active;

    /*
     * Required by JPA.
     */
    public DriverCheckin() {
    }

    public Long getId() {
        return id;
    }

    public Long getLoadId() {
        return loadId;
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

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
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

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}