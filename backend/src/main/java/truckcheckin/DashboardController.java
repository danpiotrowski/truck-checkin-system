package truckcheckin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * This controller provides data specifically for the shipper dashboard.
 *
 * It returns combined data from:
 * - loads
 * - driver_checkins
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class DashboardController {

    private final LoadRepository repository;

    public DashboardController(LoadRepository repository) {
        this.repository = repository;
    }

    /*
     * React calls this endpoint to display the shipper dashboard.
     *
     * Without a date:
     * GET /api/dashboard/loads
     *
     * With a date filter:
     * GET /api/dashboard/loads?date=2026-06-12
     */
    @GetMapping("/api/dashboard/loads")
    public List<DashboardLoadRow> getDashboardLoads(
            @RequestParam(required = false) String date) {

        /*
         * If React sends a date, only return loads scheduled
         * for that pickup date.
         */
        if (date != null && !date.isBlank()) {
            LocalDate scheduledPickupDate = LocalDate.parse(date);
            return repository.findDashboardRowsByPickupDate(scheduledPickupDate);
        }

        /*
         * If no date is sent, return all active loads.
         * This keeps the endpoint flexible.
         */
        return repository.findDashboardRows();
    }
}