package truckcheckin;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * This controller provides data specifically for the shipper dashboard.
 *
 * It returns combined data from loads and driver_checkins.
 */
 
 @CrossOrigin(origins = "http://localhost:5173")
 @RestController
 
 public class DashboardController {
	 
	 private final LoadRepository repository;
	 
	 public DashboardController(LoadRepository repository) {
		 
		 this.repository = repository;
		 
	 }
	 
	 /*
	  * react will call this endpoint to display the shipper dashboard.
	  */
	  @GetMapping("/api/dashboard/loads")
	  public List<DashboardLoadRow> getDashboardLoads() {
		  
		  return repository.findDashboardRows();
		  
	  }
	  
 }
 