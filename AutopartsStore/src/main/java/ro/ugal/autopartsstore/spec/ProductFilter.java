package ro.ugal.autopartsstore.spec;

import java.math.BigDecimal;

public class ProductFilter {
    public String q;                 // search in sku/name
    public Long categoryId;
    public Long manufacturerId;
    public BigDecimal minPrice;
    public BigDecimal maxPrice;
    public Integer minStock;
    public Boolean active;
}
