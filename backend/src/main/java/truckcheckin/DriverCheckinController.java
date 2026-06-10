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
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/checkins")
public class DriverCheckinController {

    /*
     * Repository used to save and search driver check-in records.
     */
    private final DriverCheckinRepository repository;

    /*
     * Spring automatically provides the repository here.
     */
    public DriverCheckinController(DriverCheckinRepository repository) {
        this.repository = repository;
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
                repository.findByLoadIdAndActiveTrue(checkin.getLoadId());

        /*
         * Prevent duplicate active check-ins.
         */
        if (existingCheckin.isPresent()) {
            throw new RuntimeException("This load is already checked in.");
        }

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
        return repository.save(checkin);
    }
}