package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ugal.autopartsstore.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
