package ro.ugal.autopartsstore.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SalesSummaryResponse {
    public LocalDateTime from;
    public LocalDateTime to;

    public long ordersCount;
    public BigDecimal subtotal;
    public BigDecimal vatTotal;
    public BigDecimal total;
}
