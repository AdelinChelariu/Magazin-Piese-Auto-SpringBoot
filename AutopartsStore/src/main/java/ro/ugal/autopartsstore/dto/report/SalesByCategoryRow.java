package ro.ugal.autopartsstore.dto.report;

import java.math.BigDecimal;

public class SalesByCategoryRow {
    public Long categoryId;
    public String categoryName;

    public Long qty;
    public BigDecimal subtotal;
    public BigDecimal vat;
    public BigDecimal total;
}
