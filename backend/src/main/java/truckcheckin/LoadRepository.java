package truckcheckin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

/*
 * This repository talks to the loads table.
 */

public interface LoadRepository extends JpaRepository<Load, Long> {


	/*
	 * Return only active loads.
	 * This supports soft delete later.
	 */
	 
	 List<Load> findByActiveTrue();
	 
	 /*
	  * Build the shipper dashboard rows.
	  * 
	  * This joins:
	  * - loads
	  * -driver_checkins
	  *
	  * LEFT JOIN means:
	  * Show every active load even if no driver has checked in yet.
	  */
	  
	  @Query("""
		  SELECT new truckcheckin.DashboardLoadRow(
			  l.id,
			  l.loadNumber,
			  l.status,
			  d.driverFirstName,
			  d.driverLastName,
			  d.truckingCompany,
			  d.phoneNumber,
			  d.trailerNumber
		)
		FROM Load l
		LEFT JOIN DriverCheckin d
			ON d.loadId = l.id
			AND d.active IS TRUE
		WHERE l.active IS TRUE
		ORDER BY l.id
	   """)
	   List<DashboardLoadRow> findDashboardRows();
	/*
     * Find a load by its external load number and pickup date.
     * Used during CSV import to prevent duplicate loads.
     */
    Optional<Load> findByLoadNumberAndScheduledPickupDate(
            String loadNumber,
            LocalDate scheduledPickupDate);
}
	 