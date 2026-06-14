package truckcheckin;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controller for dock door visualization.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class DockDoorController {

    private final DockDoorRepository repository;

    public DockDoorController(DockDoorRepository repository) {
        this.repository = repository;
    }

    /*
     * React will call this endpoint to build the dock door page.
     *
     * GET /api/dock-doors
     */
    @GetMapping("/api/dock-doors")
    public List<DockDoorViewRow> getDockDoors() {
        return repository.findDoorVisualizationRows();
    }
}