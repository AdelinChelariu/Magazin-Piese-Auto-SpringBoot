package ro.ugal.autopartsstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ro.ugal.autopartsstore.domain.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>, JpaSpecificationExecutor<ActivityLog> {

    // Ca să aducă și user-ul (username) fără Lazy issues / N+1
    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<ActivityLog> findAll(org.springframework.data.jpa.domain.Specification<ActivityLog> spec, Pageable pageable);
}
