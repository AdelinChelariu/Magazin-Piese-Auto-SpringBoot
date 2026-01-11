package ro.ugal.autopartsstore.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "role_snapshot", length = 100)
    private String roleSnapshot;

    @Column(nullable = false, length = 120)
    private String action;     // ex: UPDATE_PRODUCT, EXPORT_INVOICE_PDF

    @Column(length = 80)
    private String entity;     // Product, Order etc.

    @Column(name = "entity_id")
    private Long entityId;

    @Lob
    @Column(name = "sql_text")
    private String sqlText;    // opțional

    @Column(nullable = false)
    private boolean success = true;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "ip_address", length = 60)
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRoleSnapshot() { return roleSnapshot; }
    public void setRoleSnapshot(String roleSnapshot) { this.roleSnapshot = roleSnapshot; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getSqlText() { return sqlText; }
    public void setSqlText(String sqlText) { this.sqlText = sqlText; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime time) { this.createdAt = time; }
}
