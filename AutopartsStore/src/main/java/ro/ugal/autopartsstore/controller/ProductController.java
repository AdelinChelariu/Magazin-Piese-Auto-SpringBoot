package ro.ugal.autopartsstore.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.dto.product.*;
import ro.ugal.autopartsstore.service.ProductService;
import ro.ugal.autopartsstore.spec.ProductFilter;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ProductResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long manufacturerId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        ProductFilter f = new ProductFilter();
        f.q = q;
        f.categoryId = categoryId;
        f.manufacturerId = manufacturerId;
        f.minPrice = minPrice;
        f.maxPrice = maxPrice;
        f.minStock = minStock;
        f.active = active;

        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        var pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        return service.list(f, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
