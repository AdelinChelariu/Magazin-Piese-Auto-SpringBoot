package ro.ugal.autopartsstore.spec;

import org.springframework.data.jpa.domain.Specification;
import ro.ugal.autopartsstore.domain.ActivityLog;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

public class ActivityLogSpecifications {

    public static Specification<ActivityLog> byFilter(ActivityLogFilter f) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (f == null) return cb.conjunction();

            // join user pentru username filter (left join)
            var userJoin = root.join("user", jakarta.persistence.criteria.JoinType.LEFT);

            if (f.username != null && !f.username.isBlank()) {
                String like = "%" + f.username.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(userJoin.get("username")), like));
            }

            if (f.action != null && !f.action.isBlank()) {
                String like = "%" + f.action.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("action")), like));
            }

            if (f.entity != null && !f.entity.isBlank()) {
                String like = "%" + f.entity.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("entity")), like));
            }

            if (f.success != null) {
                predicates.add(cb.equal(root.get("success"), f.success));
            }

            if (f.from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.from));
            }

            if (f.to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
