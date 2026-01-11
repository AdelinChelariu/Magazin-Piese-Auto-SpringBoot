package ro.ugal.autopartsstore.controller;

import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.repository.ManufacturerRepository;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerController {

    private final ManufacturerRepository repo;

    public ManufacturerController(ManufacturerRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ManufacturerRepository.ManufacturerOption> list() {
        return repo.findOptions();
    }
}
