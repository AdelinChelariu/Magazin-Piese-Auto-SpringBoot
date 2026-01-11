package ro.ugal.autopartsstore.dto.order;

import java.math.BigDecimal;

public class OrderItemResponse {
    public Long productId;
    public String sku;
    public String name;

    public int quantity;
    public BigDecimal unitPrice;
    public BigDecimal vatRate;
    public BigDecimal lineTotal;
}
