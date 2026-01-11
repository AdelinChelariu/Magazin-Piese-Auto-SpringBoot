package ro.ugal.autopartsstore.controller;

import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.repository.CategoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository repo;

    public CategoryController(CategoryRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<CategoryRepository.CategoryOption> list() {
        return repo.findOptions();
    }
}
