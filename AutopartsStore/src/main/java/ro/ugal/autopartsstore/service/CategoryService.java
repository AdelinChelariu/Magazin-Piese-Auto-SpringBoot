package ro.ugal.autopartsstore.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ugal.autopartsstore.audit.Audit;
import ro.ugal.autopartsstore.domain.Category;
import ro.ugal.autopartsstore.dto.category.CategoryCreateRequest;
import ro.ugal.autopartsstore.dto.category.CategoryUpdateRequest;
import ro.ugal.autopartsstore.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<CategoryRepository.CategoryRow> listRows() {
        return repo.findRows();
    }

    @Transactional(readOnly = true)
    public CategoryRepository.CategoryRow getRow(Long id) {
        return repo.findRowById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    @Audit(action = "CREATE_CATEGORY", entity = "Category")
    @Transactional
    public CategoryRepository.CategoryRow create(CategoryCreateRequest req) {
        Category parent = null;
        if (req.parentId != null) {
            parent = repo.findById(req.parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + req.parentId));
        }

        Category c = new Category();
        c.setName(req.name.trim());
        c.setParent(parent);

        try {
            repo.save(c);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Category already exists or invalid data.");
        }

        return getRow(c.getId());
    }

    @Audit(action = "UPDATE_CATEGORY", entity = "Category")
    @Transactional
    public CategoryRepository.CategoryRow update(Long id, CategoryUpdateRequest req) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));

        if (req.parentId != null && req.parentId.equals(id)) {
            throw new IllegalArgumentException("A category cannot be its own parent.");
        }

        Category parent = null;
        if (req.parentId != null) {
            parent = repo.findById(req.parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + req.parentId));
        }

        c.setName(req.name.trim());
        c.setParent(parent);

        try {
            repo.save(c);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Category already exists or invalid data.");
        }

        return getRow(c.getId());
    }

    @Audit(action = "DELETE_CATEGORY", entity = "Category")
    @Transactional
    public Long delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Category not found: " + id);
        }
        repo.deleteById(id);
        return id;
    }
}
