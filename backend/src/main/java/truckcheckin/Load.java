package truckcheckin;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * Represents one row in the loads table.
 *
 * This class is a JPA Entity.
 * Spring Boot uses it to read from and write to
 * the PostgreSQL loads table.
 */
@Entity
@Table(name = "loads")
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * This stores the external load number from the CSV:
     * Externally Planned Load Nbr
     */
    @Column(name = "load_number")
    private String loadNumber;

    /*
     * These fields may be entered later by the driver
     * or updated by the shipping office.
     */
    @Column(name = "trucking_company")
    private String truckingCompany;

    @Column(name = "trailer_number")
    private String trailerNumber;

    /*
     * Valid statuses:
     *
     * NOT_ARRIVED
     * WAITING
     * ASSIGNED_TO_DOOR
     * COMPLETED
     */
    private String status;

    /*
     * This comes from the date selected by the shipper
     * during CSV upload.
     *
     * We are not using Required Ship Date from the CSV.
     */
    @Column(name = "scheduled_pickup_date")
    private LocalDate scheduledPickupDate;

    /*
     * Used for soft delete/archive.
     *
     * active = true  means show on dashboard.
     * active = false means hidden/archived.
     */
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /*
     * Required by JPA.
     */
    public Load() {
    }

    /*
     * This method runs automatically before a new Load
     * is inserted into PostgreSQL.
     */
    @PrePersist
    public void beforeInsert() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (active == null) {
            active = true;
        }

        if (status == null) {
            status = "NOT_ARRIVED";
        }
    }

    /*
     * This method runs automatically before an existing Load
     * is updated in PostgreSQL.
     */
    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /*
     * Getters allow other classes to read values.
     */

    public Long getId() {
        return id;
    }

    public String getLoadNumber() {
        return loadNumber;
    }

    public String getTruckingCompany() {
        return truckingCompany;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getScheduledPickupDate() {
        return scheduledPickupDate;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /*
     * Setters allow other classes to update values.
     *
     * CsvUploadController needs these when importing loads
     * from the CSV file.
     */

    public void setLoadNumber(String loadNumber) {
        this.loadNumber = loadNumber;
    }

    public void setTruckingCompany(String truckingCompany) {
        this.truckingCompany = truckingCompany;
    }

    public void setTrailerNumber(String trailerNumber) {
        this.trailerNumber = trailerNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setScheduledPickupDate(LocalDate scheduledPickupDate) {
        this.scheduledPickupDate = scheduledPickupDate;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}