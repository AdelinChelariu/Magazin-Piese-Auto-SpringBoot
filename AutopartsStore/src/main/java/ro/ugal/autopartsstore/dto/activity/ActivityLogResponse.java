package ro.ugal.autopartsstore.dto.activity;

import java.time.LocalDateTime;

public class ActivityLogResponse {
    public Long id;
    public String username;
    public String roleSnapshot;

    public String action;
    public String entity;
    public Long entityId;

    public Boolean success;
    public String errorMessage;

    public String ipAddress;
    public LocalDateTime createdAt;
}
