package truckcheckin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controller for load-related API requests.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class LoadController {

    private final LoadRepository loadRepository;
    private final DockDoorRepository dockDoorRepository;

    public LoadController(
            LoadRepository loadRepository,
            DockDoorRepository dockDoorRepository) {

        this.loadRepository = loadRepository;
        this.dockDoorRepository = dockDoorRepository;
    }

    /*
     * Returns active loads.
     */
    @GetMapping("/api/loads")
    public List<Load> getLoads() {
        return loadRepository.findByActiveTrue();
    }

    /*
     * Assigns a WAITING load to an AVAILABLE dock door.
     *
     * Endpoint:
     * PUT /api/loads/{loadId}/assign-door
     *
     * Body:
     * {
     *   "dockDoorId": 5
     * }
     */
    @PutMapping("/api/loads/{loadId}/assign-door")
    public Load assignLoadToDoor(
            @PathVariable Long loadId,
            @RequestBody AssignDoorRequest request) {

        Optional<Load> loadOptional = loadRepository.findById(loadId);

        if (loadOptional.isEmpty()) {
            throw new RuntimeException("Load not found.");
        }

        Optional<DockDoor> doorOptional =
                dockDoorRepository.findById(request.getDockDoorId());

        if (doorOptional.isEmpty()) {
            throw new RuntimeException("Dock door not found.");
        }

        Load load = loadOptional.get();
        DockDoor door = doorOptional.get();

        /*
         * The shipper should only assign doors to loads
         * after the driver has checked in.
         */
        if (!"WAITING".equals(load.getStatus())) {
            throw new RuntimeException("Only waiting loads can be assigned to a dock door.");
        }

        /*
         * A load can only be assigned to a usable empty door.
         */
        if (!"AVAILABLE".equals(door.getStatus())) {
            throw new RuntimeException("Dock door is not available.");
        }

        if (door.getCurrentLoadId() != null) {
            throw new RuntimeException("Dock door already has a load assigned.");
        }

        LocalDateTime now = LocalDateTime.now();

        /*
         * Update the load.
         */
        load.setStatus("ASSIGNED_TO_DOOR");
        load.setDockDoorId(door.getId());
        load.setAssignedToDoorAt(now);
        load.setUpdatedAt(now);

        Load savedLoad = loadRepository.save(load);

        /*
         * Update the dock door.
         */
        door.setStatus("OCCUPIED");
        door.setCurrentLoadId(load.getId());
        door.setOccupiedSince(now);
        door.setAvailableSince(null);
        door.setDownSince(null);
        door.setDownReason(null);
        door.setLastStatusChangedAt(now);

        dockDoorRepository.save(door);

        return savedLoad;
    }

    /*
     * Completes a load and frees the dock door.
     *
     * Endpoint:
     * PUT /api/loads/{loadId}/complete
     */
    @PutMapping("/api/loads/{loadId}/complete")
    public Load completeLoad(@PathVariable Long loadId) {

        Optional<Load> loadOptional = loadRepository.findById(loadId);

        if (loadOptional.isEmpty()) {
            throw new RuntimeException("Load not found.");
        }

        Load load = loadOptional.get();

        if (!"ASSIGNED_TO_DOOR".equals(load.getStatus())) {
            throw new RuntimeException("Only loads assigned to a door can be completed.");
        }

        LocalDateTime now = LocalDateTime.now();

        /*
         * Find the dock door assigned to this load.
         */
        Optional<DockDoor> doorOptional;

        if (load.getDockDoorId() != null) {
            doorOptional = dockDoorRepository.findById(load.getDockDoorId());
        } else {
            doorOptional = dockDoorRepository.findByCurrentLoadId(load.getId());
        }

        if (doorOptional.isEmpty()) {
            throw new RuntimeException("Assigned dock door not found.");
        }

        DockDoor door = doorOptional.get();

        /*
         * Complete the load.
         */
        load.setStatus("COMPLETED");
        load.setCompletedAt(now);
        load.setUpdatedAt(now);

        Load savedLoad = loadRepository.save(load);

        /*
         * Free the door.
         */
        door.setStatus("AVAILABLE");
        door.setCurrentLoadId(null);
        door.setAvailableSince(now);
        door.setOccupiedSince(null);
        door.setDownSince(null);
        door.setDownReason(null);
        door.setLastStatusChangedAt(now);

        dockDoorRepository.save(door);

        return savedLoad;
    }
}