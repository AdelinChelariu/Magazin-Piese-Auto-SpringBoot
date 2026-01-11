package ro.ugal.autopartsstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ro.ugal.autopartsstore.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Override
    @EntityGraph(attributePaths = {"customer"})
    Page<Order> findAll(org.springframework.data.jpa.domain.Specification<Order> spec, Pageable pageable);
}
