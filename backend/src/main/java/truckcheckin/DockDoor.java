package truckcheckin;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * Represents one row in the dock_doors table.
 *
 * Each dock door has its own status:
 *
 * AVAILABLE = green
 * OCCUPIED  = yellow
 * DOWN      = red
 */
@Entity
@Table(name = "dock_doors")
public class DockDoor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Door number shown to the shipper.
     *
     * Example:
     * 3, 4, 5 ... 43
     */
    @Column(name = "door_number")
    private String doorNumber;

    /*
     * Door status:
     *
     * AVAILABLE
     * OCCUPIED
     * DOWN
     */
    private String status;

    /*
     * If the door is occupied, this points to loads.id.
     *
     * If the door is available or down, this should be null.
     */
    @Column(name = "current_load_id")
    private Long currentLoadId;

    /*
     * Timestamp for when the door became available.
     */
    @Column(name = "available_since")
    private LocalDateTime availableSince;

    /*
     * Timestamp for when the door became occupied.
     */
    @Column(name = "occupied_since")
    private LocalDateTime occupiedSince;

    /*
     * Timestamp for when the door was marked down.
     */
    @Column(name = "down_since")
    private LocalDateTime downSince;

    /*
     * Message typed by the shipper explaining why the door is down.
     */
    @Column(name = "down_reason")
    private String downReason;

    /*
     * Timestamp for the most recent status change.
     */
    @Column(name = "last_status_changed_at")
    private LocalDateTime lastStatusChangedAt;

    /*
     * Used to hide/remove doors later without deleting history.
     */
    private Boolean active;

    /*
     * Used to sort doors numerically.
     *
     * This prevents text sorting problems like:
     * 10 appearing before 3.
     */
    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /*
     * Required by JPA.
     */
    public DockDoor() {
    }

    @PrePersist
    public void beforeInsert() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (lastStatusChangedAt == null) {
            lastStatusChangedAt = now;
        }

        if (active == null) {
            active = true;
        }

        if (status == null) {
            status = "AVAILABLE";
        }
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public String getStatus() {
        return status;
    }

    public Long getCurrentLoadId() {
        return currentLoadId;
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

    public Boolean getActive() {
        return active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setDoorNumber(String doorNumber) {
        this.doorNumber = doorNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentLoadId(Long currentLoadId) {
        this.currentLoadId = currentLoadId;
    }

    public void setAvailableSince(LocalDateTime availableSince) {
        this.availableSince = availableSince;
    }

    public void setOccupiedSince(LocalDateTime occupiedSince) {
        this.occupiedSince = occupiedSince;
    }

    public void setDownSince(LocalDateTime downSince) {
        this.downSince = downSince;
    }

    public void setDownReason(String downReason) {
        this.downReason = downReason;
    }

    public void setLastStatusChangedAt(LocalDateTime lastStatusChangedAt) {
        this.lastStatusChangedAt = lastStatusChangedAt;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}