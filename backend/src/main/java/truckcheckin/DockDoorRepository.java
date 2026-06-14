package truckcheckin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
 * Repository for the dock_doors table.
 */
public interface DockDoorRepository extends JpaRepository<DockDoor, Long> {

    /*
     * Returns only active dock doors.
     */
    List<DockDoor> findByActiveTrueOrderByDisplayOrderAsc();

    /*
     * Builds the door visualization data.
     *
     * LEFT JOIN is used so every dock door shows up,
     * even if it does not currently have a load assigned.
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