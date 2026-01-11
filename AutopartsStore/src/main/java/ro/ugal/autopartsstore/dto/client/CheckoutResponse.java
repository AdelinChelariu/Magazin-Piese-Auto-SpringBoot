package ro.ugal.autopartsstore.dto.client;

import java.math.BigDecimal;

public class CheckoutResponse {
    public Long orderId;
    public String orderNo;
    public BigDecimal subtotal;
    public BigDecimal vatTotal;
    public BigDecimal total;
}
