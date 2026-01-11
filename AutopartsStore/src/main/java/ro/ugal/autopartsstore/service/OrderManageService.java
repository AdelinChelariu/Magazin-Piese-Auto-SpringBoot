package ro.ugal.autopartsstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ugal.autopartsstore.audit.Audit;
import ro.ugal.autopartsstore.domain.Order;
import ro.ugal.autopartsstore.domain.enums.OrderStatus;
import ro.ugal.autopartsstore.dto.order.*;
import ro.ugal.autopartsstore.repository.OrderRepository;
import ro.ugal.autopartsstore.spec.OrderManageFilter;
import ro.ugal.autopartsstore.spec.OrderManageSpecifications;

import java.util.stream.Collectors;

@Service
public class OrderManageService {

    private final OrderRepository orderRepo;

    public OrderManageService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Transactional(readOnly = true)
    public Page<OrderRowResponse> list(OrderManageFilter filter, Pageable pageable) {
        var spec = OrderManageSpecifications.byFilter(filter);
        return orderRepo.findAll(spec, pageable).map(this::toRow);
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse details(Long id) {
        Order o = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return toDetails(o);
    }

    @Audit(action = "UPDATE_ORDER_STATUS", entity = "Order")
    @Transactional
    public Long updateStatus(Long id, String newStatus) {
        Order o = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        // validare simplă status
        String s = newStatus.trim().toUpperCase();
        if (!s.equals("PLACED") && !s.equals("PAID") && !s.equals("SHIPPED") && !s.equals("CANCELLED")) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        o.setStatus(OrderStatus.valueOf(s));
        orderRepo.save(o);
        return id; // ca audit-ul să prindă entityId
    }

    private OrderRowResponse toRow(Order o) {
        OrderRowResponse r = new OrderRowResponse();
        r.id = o.getId();
        r.orderNo = o.getOrderNo();
        r.status = String.valueOf(o.getStatus());
        r.orderDate = o.getOrderDate();

        if (o.getCustomer() != null) {
            r.customerId = o.getCustomer().getId();
            r.customerName = o.getCustomer().getName();
            r.customerEmail = o.getCustomer().getEmail();
        }

        r.subtotal = o.getSubtotal();
        r.vatTotal = o.getVatTotal();
        r.total = o.getTotal();
        return r;
    }

    private OrderDetailsResponse toDetails(Order o) {
        OrderDetailsResponse d = new OrderDetailsResponse();
        d.id = o.getId();
        d.orderNo = o.getOrderNo();
        d.status = String.valueOf(o.getStatus());
        d.orderDate = o.getOrderDate();
        d.notes = o.getNotes();

        if (o.getCustomer() != null) {
            d.customerId = o.getCustomer().getId();
            d.customerName = o.getCustomer().getName();
            d.customerEmail = o.getCustomer().getEmail();
        }

        d.subtotal = o.getSubtotal();
        d.vatTotal = o.getVatTotal();
        d.total = o.getTotal();

        // presupunem Order are getItems()
        d.items = o.getItems().stream().map(it -> {
            OrderItemResponse r = new OrderItemResponse();
            r.productId = it.getProduct().getId();
            r.sku = it.getProduct().getSku();
            r.name = it.getProduct().getName();
            r.quantity = it.getQuantity();
            r.unitPrice = it.getUnitPrice();
            r.vatRate = it.getVatRate();
            r.lineTotal = it.getLineTotal();
            return r;
        }).collect(Collectors.toList());

        return d;
    }
}
