package ro.ugal.autopartsstore.spec;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ro.ugal.autopartsstore.domain.Order;

import java.util.ArrayList;

public class OrderManageSpecifications {

    public static Specification<Order> byFilter(OrderManageFilter f) {
        return (root, query, cb) -> {
            var preds = new ArrayList<Predicate>();
            if (f == null) return cb.conjunction();

            var customerJoin = root.join("customer", JoinType.LEFT);

            if (f.q != null && !f.q.isBlank()) {
                String like = "%" + f.q.trim().toLowerCase() + "%";
                preds.add(cb.or(
                        cb.like(cb.lower(root.get("orderNo")), like),
                        cb.like(cb.lower(customerJoin.get("name")), like),
                        cb.like(cb.lower(customerJoin.get("email")), like)
                ));
            }

            if (f.status != null && !f.status.isBlank()) {
                preds.add(cb.equal(root.get("status"), f.status.trim()));
            }

            if (f.from != null) {
                preds.add(cb.greaterThanOrEqualTo(root.get("orderDate"), f.from));
            }
            if (f.to != null) {
                preds.add(cb.lessThanOrEqualTo(root.get("orderDate"), f.to));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}
