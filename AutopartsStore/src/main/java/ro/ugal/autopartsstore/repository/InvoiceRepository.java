package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ugal.autopartsstore.domain.Invoice;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNo(String invoiceNo);
    Optional<Invoice> findByOrderId(Long orderId);
}
