package ro.ugal.autopartsstore.spec;

import java.time.LocalDateTime;

public class ActivityLogFilter {
    public String username;
    public String action;
    public String entity;
    public Boolean success;
    public LocalDateTime from;
    public LocalDateTime to;
}
