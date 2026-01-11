package ro.ugal.autopartsstore.domain;

import ro.ugal.autopartsstore.domain.enums.ExportDocType;
import ro.ugal.autopartsstore.domain.enums.ExportFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_exports")
public class DocumentExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 30)
    private ExportDocType docType;

    @Column(name = "doc_id", nullable = false)
    private Long docId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ExportFormat format;

    @Column(nullable = false, length = 255)
    private String filename;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by_user_id", nullable = false)
    private User generatedBy;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    // getters/setters
    public Long getId() { return id; }
    public ExportDocType getDocType() { return docType; }
    public void setDocType(ExportDocType docType) { this.docType = docType; }
    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }
    public ExportFormat getFormat() { return format; }
    public void setFormat(ExportFormat format) { this.format = format; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public User getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(User generatedBy) { this.generatedBy = generatedBy; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}
