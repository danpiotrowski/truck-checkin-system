package truckcheckin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoadRepository extends JpaRepository<Load, Long> {


	/*
	 * Return only active loads.
	 * This supports soft delete later.
	 */
	 
	 List<Load> findByActiveTrue();
	 
	}
	 