package ro.ugal.autopartsstore.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.dto.manufacturer.ManufacturerCreateRequest;
import ro.ugal.autopartsstore.dto.manufacturer.ManufacturerUpdateRequest;
import ro.ugal.autopartsstore.repository.ManufacturerRepository;
import ro.ugal.autopartsstore.service.ManufacturerService;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturers/manage")
public class ManufacturerManageController {

    private final ManufacturerService service;
    private final ManufacturerRepository repo;

    public ManufacturerManageController(ManufacturerService service, ManufacturerRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @GetMapping
    public List<ManufacturerRepository.ManufacturerRow> list() {
        return service.listRows();
    }

    @GetMapping("/{id}")
    public ManufacturerRepository.ManufacturerRow get(@PathVariable Long id) {
        return service.getRow(id);
    }

    @PostMapping
    public ManufacturerRepository.ManufacturerRow create(@Valid @RequestBody ManufacturerCreateRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ManufacturerRepository.ManufacturerRow update(@PathVariable Long id, @Valid @RequestBody ManufacturerUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/options")
    public List<ManufacturerRepository.ManufacturerOption> options() {
        return repo.findOptions();
    }
}
