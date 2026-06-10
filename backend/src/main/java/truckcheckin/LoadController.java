package truckcheckin;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class LoadController {
	
	private final LoadRepository repository;
	
	public LoadController(LoadRepository repository) {
		this.repository	= repository;
		
	}
	
	@GetMapping("/api/loads")
	public List<Load> getLoads() {
		return repository.findByActiveTrue();
	}
	
}
