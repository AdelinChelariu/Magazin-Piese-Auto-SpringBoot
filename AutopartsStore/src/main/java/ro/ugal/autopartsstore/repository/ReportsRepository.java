package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.ugal.autopartsstore.domain.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportsRepository extends JpaRepository<Order, Long> {

    interface SalesSummaryProj {
        Long getOrdersCount();
        BigDecimal getSubtotal();
        BigDecimal getVatTotal();
        BigDecimal getTotal();
    }

    interface SalesByCategoryProj {
        Long getCategoryId();
        String getCategoryName();
        Long getQty();
        BigDecimal getSubtotal();
        BigDecimal getVat();
        BigDecimal getTotal();
    }

    @Query(value = """
        SELECT
          COUNT(*) AS ordersCount,
          COALESCE(SUM(o.subtotal), 0) AS subtotal,
          COALESCE(SUM(o.vat_total), 0) AS vatTotal,
          COALESCE(SUM(o.total), 0) AS total
        FROM orders o
        WHERE o.order_date >= :from
          AND o.order_date <= :to
          AND o.status IN ('PLACED','PAID','SHIPPED')
        """, nativeQuery = true)
    SalesSummaryProj salesSummary(@Param("from") LocalDateTime from,
                                  @Param("to") LocalDateTime to);

    @Query(value = """
        SELECT
          c.id AS categoryId,
          c.name AS categoryName,
          COALESCE(SUM(oi.quantity), 0) AS qty,
          COALESCE(SUM(oi.line_total), 0) AS subtotal,
          COALESCE(SUM(oi.line_total * oi.vat_rate / 100), 0) AS vat,
          COALESCE(SUM(oi.line_total + (oi.line_total * oi.vat_rate / 100)), 0) AS total
        FROM orders o
        JOIN order_items oi ON oi.order_id = o.id
        JOIN products p ON p.id = oi.product_id
        JOIN categories c ON c.id = p.category_id
        WHERE o.order_date >= :from
          AND o.order_date <= :to
          AND o.status IN ('PLACED','PAID','SHIPPED')
        GROUP BY c.id, c.name
        ORDER BY total DESC
        """, nativeQuery = true)
    List<SalesByCategoryProj> salesByCategory(@Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);
}
