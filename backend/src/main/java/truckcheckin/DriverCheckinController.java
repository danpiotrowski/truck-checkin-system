package truckcheckin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
 *
 * POST /api/checkins
 *
 * The driver enters only the real load number.
 *
 * The driver does NOT enter:
 * - internal database ID
 * - pickup date
 *
 * Spring Boot automatically uses today's date.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/checkins")
public class DriverCheckinController {

    private final DriverCheckinRepository checkinRepository;
    private final LoadRepository loadRepository;

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
    public DriverCheckin createCheckin(@RequestBody DriverCheckinRequest request) {

        /*
         * Clean the load number entered by the driver.
         */
        String loadNumber = request.getLoadNumber() == null
                ? ""
                : request.getLoadNumber().trim();

        if (loadNumber.isBlank()) {
            throw new RuntimeException("Load number is required.");
        }

        /*
         * Automatically use today's date.
         *
         * America/New_York is used because this warehouse workflow
         * should follow local Ohio/Eastern time, not UTC.
         */
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));

        /*
         * Find the load using:
         *
         * - load number entered by the driver
         * - today's pickup date
         *
         * This means the driver does not need to pick a date.
         */
        Optional<Load> loadOptional =
                loadRepository.findByLoadNumberAndScheduledPickupDate(
                        loadNumber,
                        today);

        /*
         * If no matching load exists for today, stop the check-in.
         */
        if (loadOptional.isEmpty()) {
            throw new RuntimeException("Load number not found for today's pickup date.");
        }

        Load load = loadOptional.get();

        /*
         * Only NOT_ARRIVED loads can be checked in.
         *
         * Normal flow:
         * NOT_ARRIVED -> WAITING
         */
        if (!"NOT_ARRIVED".equals(load.getStatus())) {
            throw new RuntimeException("This load is already checked in, assigned, or completed.");
        }

        /*
         * Prevent duplicate active check-ins.
         */
        Optional<DriverCheckin> existingCheckin =
                checkinRepository.findByLoadIdAndActiveTrue(load.getId());

        if (existingCheckin.isPresent()) {
            throw new RuntimeException("This load is already checked in.");
        }

        /*
         * Create the actual DriverCheckin database record.
         *
         * The driver never typed the database ID.
         * Spring Boot found the correct load internally.
         */
        DriverCheckin checkin = new DriverCheckin();

        checkin.setLoadId(load.getId());
        checkin.setDriverFirstName(request.getDriverFirstName());
        checkin.setDriverLastName(request.getDriverLastName());
        checkin.setTruckingCompany(request.getTruckingCompany());
        checkin.setPhoneNumber(request.getPhoneNumber());
        checkin.setTrailerNumber(request.getTrailerNumber());
        checkin.setActive(true);
        checkin.setCheckinTime(LocalDateTime.now());

        DriverCheckin savedCheckin = checkinRepository.save(checkin);

        /*
         * This is the only place where:
         *
         * NOT_ARRIVED -> WAITING
         *
         * should happen.
         */
        LocalDateTime now = LocalDateTime.now();

        load.setStatus("WAITING");
        load.setUpdatedAt(now);

        loadRepository.save(load);

        return savedCheckin;
    }
}