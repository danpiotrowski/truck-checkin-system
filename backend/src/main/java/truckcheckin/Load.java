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
     * External load number from the CSV:
     * Externally Planned Load Nbr
     */
    @Column(name = "load_number")
    private String loadNumber;

    @Column(name = "trucking_company")
    private String truckingCompany;

    @Column(name = "trailer_number")
    private String trailerNumber;

    /*
     * Valid load statuses:
     *
     * NOT_ARRIVED       = created from CSV upload
     * WAITING           = driver checked in
     * ASSIGNED_TO_DOOR  = shipper assigned a dock door
     * COMPLETED         = load is finished
     */
    private String status;

    /*
     * Date selected by the shipper during CSV upload.
     */
    @Column(name = "scheduled_pickup_date")
    private LocalDate scheduledPickupDate;

    /*
     * If this load is assigned to a dock door,
     * this stores dock_doors.id.
     */
    @Column(name = "dock_door_id")
    private Long dockDoorId;

    /*
     * Timestamp for when the shipper assigned this load to a dock door.
     */
    @Column(name = "assigned_to_door_at")
    private LocalDateTime assignedToDoorAt;

    /*
     * Timestamp for when the load was completed.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /*
     * Used for soft delete/archive later.
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
     * Runs automatically before a new Load is inserted.
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
     * Runs automatically before an existing Load is updated.
     */
    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

    public Long getDockDoorId() {
        return dockDoorId;
    }

    public LocalDateTime getAssignedToDoorAt() {
        return assignedToDoorAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
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

    public void setDockDoorId(Long dockDoorId) {
        this.dockDoorId = dockDoorId;
    }

    public void setAssignedToDoorAt(LocalDateTime assignedToDoorAt) {
        this.assignedToDoorAt = assignedToDoorAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}