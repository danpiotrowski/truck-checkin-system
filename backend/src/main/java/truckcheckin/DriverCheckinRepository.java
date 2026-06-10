package truckcheckin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverCheckinRepository extends JpaRepository<DriverCheckin, Long> {

	/*
	 * Find an actice check-in for a load.
	 *
	 * Optional means:
	 * "There may or may not be a result."
	 */
	 
	 Optional<DriverCheckin> findByLoadIdAndActiveTrue(Long loadId);


}
