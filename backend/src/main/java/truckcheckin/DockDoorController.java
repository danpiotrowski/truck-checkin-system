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
 * Controller for dock door visualization and dock door actions.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class DockDoorController {

    private final DockDoorRepository repository;

    public DockDoorController(DockDoorRepository repository) {
        this.repository = repository;
    }

    /*
     * React calls this endpoint to build the dock door page.
     *
     * GET /api/dock-doors
     */
    @GetMapping("/api/dock-doors")
    public List<DockDoorViewRow> getDockDoors() {
        return repository.findDoorVisualizationRows();
    }

    /*
     * Marks an available dock door as down.
     *
     * Endpoint:
     * PUT /api/dock-doors/{doorId}/mark-down
     *
     * Body:
     * {
     *   "reason": "Dock plate not working"
     * }
     */
    @PutMapping("/api/dock-doors/{doorId}/mark-down")
    public DockDoor markDoorDown(
            @PathVariable Long doorId,
            @RequestBody MarkDoorDownRequest request) {

        Optional<DockDoor> doorOptional = repository.findById(doorId);

        if (doorOptional.isEmpty()) {
            throw new RuntimeException("Dock door not found.");
        }

        DockDoor door = doorOptional.get();

        /*
         * Do not allow an occupied door to be marked down.
         * The load should be completed or moved first.
         */
        if ("OCCUPIED".equals(door.getStatus()) || door.getCurrentLoadId() != null) {
            throw new RuntimeException("Cannot mark an occupied dock door as down.");
        }

        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new RuntimeException("A down reason is required.");
        }

        LocalDateTime now = LocalDateTime.now();

        door.setStatus("DOWN");
        door.setCurrentLoadId(null);
        door.setAvailableSince(null);
        door.setOccupiedSince(null);
        door.setDownSince(now);
        door.setDownReason(request.getReason());
        door.setLastStatusChangedAt(now);

        return repository.save(door);
    }

    /*
     * Marks a down dock door as available again.
     *
     * Endpoint:
     * PUT /api/dock-doors/{doorId}/mark-available
     */
    @PutMapping("/api/dock-doors/{doorId}/mark-available")
    public DockDoor markDoorAvailable(@PathVariable Long doorId) {

        Optional<DockDoor> doorOptional = repository.findById(doorId);

        if (doorOptional.isEmpty()) {
            throw new RuntimeException("Dock door not found.");
        }

        DockDoor door = doorOptional.get();

        /*
         * Do not allow an occupied door to be forced available.
         */
        if ("OCCUPIED".equals(door.getStatus()) || door.getCurrentLoadId() != null) {
            throw new RuntimeException("Cannot mark an occupied dock door as available.");
        }

        LocalDateTime now = LocalDateTime.now();

        door.setStatus("AVAILABLE");
        door.setCurrentLoadId(null);
        door.setAvailableSince(now);
        door.setOccupiedSince(null);
        door.setDownSince(null);
        door.setDownReason(null);
        door.setLastStatusChangedAt(now);

        return repository.save(door);
    }
}