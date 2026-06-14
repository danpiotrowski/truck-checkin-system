package truckcheckin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Repository for the driver_checkins table.
 *
 * This interface lets Spring Boot search and save
 * DriverCheckin records in PostgreSQL.
 */
public interface DriverCheckinRepository extends JpaRepository<DriverCheckin, Long> {

    /*
     * Finds an active driver check-in for one load.
     *
     * We use this to prevent the same load from being
     * checked in more than once while it is still active.
     */
    Optional<DriverCheckin> findByLoadIdAndActiveTrue(Long loadId);
}