package ro.ugal.autopartsstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ugal.autopartsstore.audit.Audit;
import ro.ugal.autopartsstore.domain.Product;
import ro.ugal.autopartsstore.dto.product.ProductCreateRequest;
import ro.ugal.autopartsstore.dto.product.ProductResponse;
import ro.ugal.autopartsstore.dto.product.ProductUpdateRequest;
import ro.ugal.autopartsstore.repository.CategoryRepository;
import ro.ugal.autopartsstore.repository.ManufacturerRepository;
import ro.ugal.autopartsstore.repository.ProductRepository;
import ro.ugal.autopartsstore.spec.ProductFilter;
import ro.ugal.autopartsstore.spec.ProductSpecifications;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ManufacturerRepository manufacturerRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.manufacturerRepository = manufacturerRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> list(ProductFilter filter, Pageable pageable) {
        var spec = ProductSpecifications.byFilter(filter);
        return productRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        return toResponse(p);
    }

    @Audit(action = "CREATE_PRODUCT", entity = "Product")
    @Transactional
    public ProductResponse create(ProductCreateRequest req) {
        if (productRepository.existsBySku(req.sku)) {
            throw new IllegalArgumentException("SKU already exists: " + req.sku);
        }

        var category = categoryRepository.findById(req.categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + req.categoryId));
        var manufacturer = manufacturerRepository.findById(req.manufacturerId)
                .orElseThrow(() -> new IllegalArgumentException("Manufacturer not found: " + req.manufacturerId));

        Product p = new Product();
        p.setSku(req.sku);
        p.setName(req.name);
        p.setDescription(req.description);
        p.setCategory(category);
        p.setManufacturer(manufacturer);
        p.setPrice(req.price);
        p.setVatRate(req.vatRate);
        p.setStockQty(req.stockQty);
        p.setActive(req.active);

        return toResponse(productRepository.save(p));
    }

    @Audit(action = "UPDATE_PRODUCT", entity = "Product")
    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest req) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        var category = categoryRepository.findById(req.categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + req.categoryId));
        var manufacturer = manufacturerRepository.findById(req.manufacturerId)
                .orElseThrow(() -> new IllegalArgumentException("Manufacturer not found: " + req.manufacturerId));

        p.setName(req.name);
        p.setDescription(req.description);
        p.setCategory(category);
        p.setManufacturer(manufacturer);
        p.setPrice(req.price);
        p.setVatRate(req.vatRate);
        p.setStockQty(req.stockQty);
        p.setActive(req.active);

        return toResponse(productRepository.save(p));
    }

    @Audit(action = "DELETE_PRODUCT", entity = "Product")
    @Transactional
    public Long delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        productRepository.deleteById(id);
        return id;
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.id = p.getId();
        r.sku = p.getSku();
        r.name = p.getName();
        r.description = p.getDescription();
        r.categoryId = p.getCategory().getId();
        r.categoryName = p.getCategory().getName();
        r.manufacturerId = p.getManufacturer().getId();
        r.manufacturerName = p.getManufacturer().getName();
        r.price = p.getPrice();
        r.vatRate = p.getVatRate();
        r.stockQty = p.getStockQty();
        r.active = p.isActive();
        return r;
    }
}
