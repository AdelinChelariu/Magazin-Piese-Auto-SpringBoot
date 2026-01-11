package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ugal.autopartsstore.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
