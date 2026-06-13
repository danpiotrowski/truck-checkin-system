package truckcheckin;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * This controller handles driver check-ins.
 *
 * React sends driver form data here using:
 * POST /api/checkins
 *
 * This controller now does two things:
 * 1. Saves the driver check-in.
 * 2. Updates the related load status to WAITING.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/checkins")
public class DriverCheckinController {

    /*
     * Repository used to save and search driver check-in records.
     */
    private final DriverCheckinRepository checkinRepository;

    /*
     * Repository used to find and update load records.
     *
     * We need this because when a driver checks in,
     * the load status should change from NOT_ARRIVED to WAITING.
     */
    private final LoadRepository loadRepository;

    /*
     * Spring automatically provides both repositories here.
     */
    public DriverCheckinController(
            DriverCheckinRepository checkinRepository,
            LoadRepository loadRepository) {

        this.checkinRepository = checkinRepository;
        this.loadRepository = loadRepository;
    }

    /*
     * Handles new driver check-ins.
     */
    @PostMapping
    public DriverCheckin createCheckin(@RequestBody DriverCheckin checkin) {

        /*
         * Check whether this load already has an active check-in.
         */
        Optional<DriverCheckin> existingCheckin =
                checkinRepository.findByLoadIdAndActiveTrue(checkin.getLoadId());

        /*
         * Prevent duplicate active check-ins.
         */
        if (existingCheckin.isPresent()) {
            throw new RuntimeException("This load is already checked in.");
        }

        /*
         * Find the load that this driver is checking in for.
         */
        Optional<Load> loadOptional =
                loadRepository.findById(checkin.getLoadId());

        /*
         * If the load ID does not exist, stop the check-in.
         */
        if (loadOptional.isEmpty()) {
            throw new RuntimeException("Load not found.");
        }

        /*
         * Get the actual Load object out of the Optional.
         */
        Load load = loadOptional.get();

        /*
         * Mark this check-in as active.
         */
        checkin.setActive(true);

        /*
         * Store the current date/time as the check-in time.
         */
        checkin.setCheckinTime(LocalDateTime.now());

        /*
         * Save the check-in to PostgreSQL.
         */
        DriverCheckin savedCheckin = checkinRepository.save(checkin);

        /*
         * Once the driver checks in, the load is no longer NOT_ARRIVED.
         *
         * It should now be WAITING because the driver has arrived
         * and is waiting for the shipping office to assign a dock door.
         */
        load.setStatus("WAITING");

        /*
         * Update the load's last-modified timestamp.
         */
        load.setUpdatedAt(LocalDateTime.now());

        /*
         * Save the updated load status to PostgreSQL.
         */
        loadRepository.save(load);

        /*
         * Return the saved driver check-in back to React.
         */
        return savedCheckin;
    }
}