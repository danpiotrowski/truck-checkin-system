package truckcheckin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
 * Repository for the dock_doors table.
 *
 * This interface lets Spring Boot read and update
 * dock door records in PostgreSQL.
 */
public interface DockDoorRepository extends JpaRepository<DockDoor, Long> {

    /*
     * Returns only active dock doors in display order.
     *
     * This is useful if we ever need the raw DockDoor rows.
     */
    List<DockDoor> findByActiveTrueOrderByDisplayOrderAsc();

    /*
     * Finds the dock door that currently has a specific load assigned.
     *
     * LoadController uses this when completing a load and freeing the door.
     */
    Optional<DockDoor> findByCurrentLoadId(Long currentLoadId);

    /*
     * Builds the door visualization data for React.
     *
     * This combines:
     * - dock door data
     * - current load data
     * - driver check-in data
     *
     * LEFT JOIN is used so every dock door appears,
     * even if there is no load currently assigned.
     */
    @Query("""
        SELECT new truckcheckin.DockDoorViewRow(
            door.id,
            door.doorNumber,
            door.displayOrder,
            door.status,
            door.currentLoadId,
            load.loadNumber,
            driver.driverFirstName,
            driver.driverLastName,
            driver.truckingCompany,
            driver.trailerNumber,
            door.availableSince,
            door.occupiedSince,
            door.downSince,
            door.downReason,
            door.lastStatusChangedAt
        )
        FROM DockDoor door
        LEFT JOIN Load load
            ON load.id = door.currentLoadId
        LEFT JOIN DriverCheckin driver
            ON driver.loadId = load.id
            AND driver.active IS TRUE
        WHERE door.active IS TRUE
        ORDER BY door.displayOrder
    """)
    List<DockDoorViewRow> findDoorVisualizationRows();
}