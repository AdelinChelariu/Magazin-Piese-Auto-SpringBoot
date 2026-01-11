package ro.ugal.autopartsstore.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.dto.category.CategoryCreateRequest;
import ro.ugal.autopartsstore.dto.category.CategoryUpdateRequest;
import ro.ugal.autopartsstore.repository.CategoryRepository;
import ro.ugal.autopartsstore.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories/manage")
public class CategoryManageController {

    private final CategoryService service;
    private final CategoryRepository repo;

    public CategoryManageController(CategoryService service, CategoryRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    // listare pentru tabel (cu parent)
    @GetMapping
    public List<CategoryRepository.CategoryRow> list() {
        return service.listRows();
    }

    @GetMapping("/{id}")
    public CategoryRepository.CategoryRow get(@PathVariable Long id) {
        return service.getRow(id);
    }

    @PostMapping
    public CategoryRepository.CategoryRow create(@Valid @RequestBody CategoryCreateRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CategoryRepository.CategoryRow update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // opțiuni pentru dropdown (în pagina de categories)
    @GetMapping("/options")
    public List<CategoryRepository.CategoryOption> options() {
        return repo.findOptions();
    }
}
