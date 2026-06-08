package truckcheckin;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class LoadController {

    @GetMapping("/api/loads")
    public List<Load> getLoads() {

        return List.of(
                new Load("123456", "ABC Transport", "TR789", "Checked In"),
                new Load("123457", "XYZ Logistics", "TR790", "Waiting"),
                new Load("123458", "Rapid Freight", "TR791", "Loaded")
        );
    }
}