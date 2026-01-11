package ro.ugal.autopartsstore.spec;

import java.time.LocalDateTime;

public class OrderManageFilter {
    public String q;           // orderNo OR customer name/email
    public String status;      // PLACED/PAID/...
    public LocalDateTime from;
    public LocalDateTime to;
}
