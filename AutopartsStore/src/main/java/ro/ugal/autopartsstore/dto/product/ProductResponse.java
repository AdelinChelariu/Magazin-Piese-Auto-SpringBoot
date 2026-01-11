package ro.ugal.autopartsstore.dto.product;

import java.math.BigDecimal;

public class ProductResponse {
    public Long id;
    public String sku;
    public String name;
    public String description;

    public Long categoryId;
    public String categoryName;

    public Long manufacturerId;
    public String manufacturerName;

    public BigDecimal price;
    public BigDecimal vatRate;
    public int stockQty;
    public boolean active;
}
