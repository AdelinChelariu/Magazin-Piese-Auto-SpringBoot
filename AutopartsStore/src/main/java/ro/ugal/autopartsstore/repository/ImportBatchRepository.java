package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ugal.autopartsstore.domain.ImportBatch;

public interface ImportBatchRepository extends JpaRepository<ImportBatch, Long> {
}
