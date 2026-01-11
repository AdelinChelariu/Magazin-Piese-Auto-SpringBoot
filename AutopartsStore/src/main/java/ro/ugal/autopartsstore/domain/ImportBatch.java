package ro.ugal.autopartsstore.domain;

import ro.ugal.autopartsstore.domain.enums.ImportStatus;
import ro.ugal.autopartsstore.domain.enums.ImportType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_batches")
public class ImportBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ImportType type;

    @Column(nullable = false, length = 255)
    private String filename;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "imported_by_user_id", nullable = false)
    private User importedBy;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImportStatus status;

    @Column(name = "summary_json", columnDefinition = "json")
    private String summaryJson;

    // getters/setters
    public Long getId() { return id; }
    public ImportType getType() { return type; }
    public void setType(ImportType type) { this.type = type; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public User getImportedBy() { return importedBy; }
    public void setImportedBy(User importedBy) { this.importedBy = importedBy; }
    public LocalDateTime getImportedAt() { return importedAt; }
    public ImportStatus getStatus() { return status; }
    public void setStatus(ImportStatus status) { this.status = status; }
    public String getSummaryJson() { return summaryJson; }
    public void setSummaryJson(String summaryJson) { this.summaryJson = summaryJson; }
}
