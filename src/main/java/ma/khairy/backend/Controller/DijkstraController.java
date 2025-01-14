package ma.khairy.backend.Controller;

import ma.khairy.backend.Model.Graphe;
import ma.khairy.backend.Service.DijkstraService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dijkstra")
public class DijkstraController {

    private final DijkstraService dijkstraService;

    public DijkstraController(DijkstraService dijkstraService) {
        this.dijkstraService = dijkstraService;
    }

    @PostMapping("/calculate")
    public Map<String, Object> calculateShortestPath(
            @RequestParam String startNode,
            @RequestParam(required = false, defaultValue = "false") boolean details,
            @RequestBody Graphe graphe) {
        return dijkstraService.calculerCheminMinimal(graphe, startNode, details);
    }
}