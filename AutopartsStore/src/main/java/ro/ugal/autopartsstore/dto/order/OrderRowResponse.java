package ro.ugal.autopartsstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderRowResponse {
    public Long id;
    public String orderNo;
    public String status;
    public LocalDateTime orderDate;

    public Long customerId;
    public String customerName;
    public String customerEmail;

    public BigDecimal subtotal;
    public BigDecimal vatTotal;
    public BigDecimal total;
}
