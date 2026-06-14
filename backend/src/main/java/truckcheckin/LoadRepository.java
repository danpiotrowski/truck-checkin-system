package truckcheckin;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * This repository talks to the loads table.
 */
public interface LoadRepository extends JpaRepository<Load, Long> {

    /*
     * Return only active loads.
     */
    List<Load> findByActiveTrue();

    /*
     * Find a load by external load number and pickup date.
     *
     * Used during CSV import to prevent duplicate loads.
     */
    Optional<Load> findByLoadNumberAndScheduledPickupDate(
            String loadNumber,
            LocalDate scheduledPickupDate);

    /*
     * Build dashboard rows for all active loads.
     */
    @Query("""
        SELECT new truckcheckin.DashboardLoadRow(
            l.id,
            l.loadNumber,
            l.scheduledPickupDate,
            l.dockDoorId,
            dd.doorNumber,
            l.status,
            d.driverFirstName,
            d.driverLastName,
            d.truckingCompany,
            d.phoneNumber,
            d.trailerNumber
        )
        FROM Load l
        LEFT JOIN DockDoor dd
            ON dd.id = l.dockDoorId
        LEFT JOIN DriverCheckin d
            ON d.loadId = l.id
            AND d.active IS TRUE
        WHERE l.active IS TRUE
        ORDER BY l.scheduledPickupDate, l.id
    """)
    List<DashboardLoadRow> findDashboardRows();

    /*
     * Build dashboard rows for one selected pickup date.
     */
    @Query("""
        SELECT new truckcheckin.DashboardLoadRow(
            l.id,
            l.loadNumber,
            l.scheduledPickupDate,
            l.dockDoorId,
            dd.doorNumber,
            l.status,
            d.driverFirstName,
            d.driverLastName,
            d.truckingCompany,
            d.phoneNumber,
            d.trailerNumber
        )
        FROM Load l
        LEFT JOIN DockDoor dd
            ON dd.id = l.dockDoorId
        LEFT JOIN DriverCheckin d
            ON d.loadId = l.id
            AND d.active IS TRUE
        WHERE l.active IS TRUE
            AND l.scheduledPickupDate = :scheduledPickupDate
        ORDER BY l.id
    """)
    List<DashboardLoadRow> findDashboardRowsByPickupDate(
            @Param("scheduledPickupDate") LocalDate scheduledPickupDate);
}