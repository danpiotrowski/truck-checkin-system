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
 * This controller handles load-related API requests.
 *
 * Examples:
 * GET /api/loads
 * PUT /api/loads/{id}/status
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class LoadController {

    /*
     * Repository used to read and update loads in PostgreSQL.
     */
    private final LoadRepository repository;

    /*
     * Spring automatically provides the repository here.
     */
    public LoadController(LoadRepository repository) {
        this.repository = repository;
    }

    /*
     * Returns active loads.
     *
     * This is still useful as a simple loads endpoint,
     * even though the dashboard now uses /api/dashboard/loads.
     */
    @GetMapping("/api/loads")
    public List<Load> getLoads() {
        return repository.findByActiveTrue();
    }

    /*
     * Updates the status of a load.
     *
     * React will call this when a shipper changes status
     * from the dashboard.
     *
     * Example request:
     * PUT /api/loads/3/status
     *
     * JSON body:
     * {
     *   "status": "ASSIGNED_TO_DOOR"
     * }
     */
    @PutMapping("/api/loads/{id}/status")
    public Load updateLoadStatus(
            @PathVariable Long id,
            @RequestBody UpdateLoadStatusRequest request) {

        /*
         * Look up the load by its database ID.
         */
        Optional<Load> loadOptional = repository.findById(id);

        /*
         * If no load exists with that ID, stop the request.
         */
        if (loadOptional.isEmpty()) {
            throw new RuntimeException("Load not found.");
        }

        /*
         * Get the actual Load object from Optional.
         */
        Load load = loadOptional.get();

        /*
         * Update the load status.
         *
         * Valid statuses:
         * NOT_ARRIVED
         * WAITING
         * ASSIGNED_TO_DOOR
         * COMPLETED
         */
        load.setStatus(request.getStatus());

        /*
         * Record when this load was last changed.
         */
        load.setUpdatedAt(LocalDateTime.now());

        /*
         * Save the updated load back to PostgreSQL.
         */
        return repository.save(load);
    }
}
