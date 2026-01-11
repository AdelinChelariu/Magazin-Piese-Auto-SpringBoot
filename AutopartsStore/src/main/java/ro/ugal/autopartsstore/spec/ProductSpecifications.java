package ro.ugal.autopartsstore.spec;

import org.springframework.data.jpa.domain.Specification;
import ro.ugal.autopartsstore.domain.Product;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

public class ProductSpecifications {

    public static Specification<Product> byFilter(ProductFilter f) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (f == null) return cb.conjunction();

            if (f.q != null && !f.q.isBlank()) {
                String like = "%" + f.q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("sku")), like)
                ));
            }

            if (f.categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), f.categoryId));
            }

            if (f.manufacturerId != null) {
                predicates.add(cb.equal(root.get("manufacturer").get("id"), f.manufacturerId));
            }

            if (f.minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), f.minPrice));
            }

            if (f.maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), f.maxPrice));
            }

            if (f.minStock != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("stockQty"), f.minStock));
            }

            if (f.active != null) {
                predicates.add(cb.equal(root.get("active"), f.active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
