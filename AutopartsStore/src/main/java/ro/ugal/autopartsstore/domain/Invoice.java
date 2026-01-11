package ro.ugal.autopartsstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_no", nullable = false, unique = true, length = 40)
    private String invoiceNo;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(length = 20)
    private String series;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @Column(name = "vat_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal vatTotal;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    // getters/setters
    public Long getId() { return id; }
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getVatTotal() { return vatTotal; }
    public void setVatTotal(BigDecimal vatTotal) { this.vatTotal = vatTotal; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
