package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ugal.autopartsstore.domain.DocumentExport;

public interface DocumentExportRepository extends JpaRepository<DocumentExport, Long> {
}
