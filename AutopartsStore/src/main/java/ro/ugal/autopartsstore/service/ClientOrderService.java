package ro.ugal.autopartsstore.service;

import org.springframework.security.core.context.SecurityContextHolder;
import ro.ugal.autopartsstore.domain.User;
import ro.ugal.autopartsstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ugal.autopartsstore.domain.Customer;
import ro.ugal.autopartsstore.domain.Order;
import ro.ugal.autopartsstore.domain.OrderItem;
import ro.ugal.autopartsstore.domain.Product;
import ro.ugal.autopartsstore.domain.enums.OrderStatus;
import ro.ugal.autopartsstore.dto.client.CheckoutRequest;
import ro.ugal.autopartsstore.dto.client.CheckoutResponse;
import ro.ugal.autopartsstore.repository.CustomerRepository;
import ro.ugal.autopartsstore.repository.OrderItemRepository;
import ro.ugal.autopartsstore.repository.OrderRepository;
import ro.ugal.autopartsstore.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ClientOrderService {

    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final UserRepository userRepo;


    public ClientOrderService(CustomerRepository customerRepo,
                              ProductRepository productRepo,
                              OrderRepository orderRepo,
                              OrderItemRepository itemRepo,
                              UserRepository userRepo) {
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;

    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Customer customer = customerRepo.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer profile not found for user: " + username));

        if (req.items == null || req.items.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        Order o = new Order();
        o.setCustomer(customer);
        o.setCreatedBy(u);
        o.setStatus(OrderStatus.DRAFT); // sau DRAFT, cum vrei
        o.setOrderDate(LocalDateTime.now());
        o.setNotes(req.notes);

        String orderNo = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        o.setOrderNo(orderNo);

        // totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal vatTotal = BigDecimal.ZERO;

        o = orderRepo.save(o);

        for (var it : req.items) {
            Product p = productRepo.findById(it.productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + it.productId));

            if (!p.isActive()) throw new IllegalArgumentException("Product inactive: " + p.getSku());
            if (p.getStockQty() < it.quantity) throw new IllegalArgumentException("Insufficient stock for: " + p.getSku());

            BigDecimal qty = BigDecimal.valueOf(it.quantity);
            BigDecimal lineSubtotal = p.getPrice().multiply(qty);
            BigDecimal lineVat = lineSubtotal.multiply(p.getVatRate()).divide(BigDecimal.valueOf(100));
            BigDecimal lineTotal = lineSubtotal.add(lineVat);

            OrderItem oi = new OrderItem();
            oi.setOrder(o);
            oi.setProduct(p);
            oi.setQuantity(it.quantity);
            oi.setUnitPrice(p.getPrice());
            oi.setVatRate(p.getVatRate());
            oi.setDiscount(BigDecimal.ZERO);
            oi.setLineTotal(lineSubtotal); // dacă la tine e total fără TVA, păstrează așa; altfel pune lineTotal
            itemRepo.save(oi);

            // scade stoc (opțional acum, dar e logic)
            p.setStockQty(p.getStockQty() - it.quantity);
            productRepo.save(p);

            subtotal = subtotal.add(lineSubtotal);
            vatTotal = vatTotal.add(lineVat);
        }

        o.setSubtotal(subtotal);
        o.setVatTotal(vatTotal);
        o.setTotal(subtotal.add(vatTotal));
        orderRepo.save(o);

        CheckoutResponse resp = new CheckoutResponse();
        resp.orderId = o.getId();
        resp.orderNo = o.getOrderNo();
        resp.subtotal = o.getSubtotal();
        resp.vatTotal = o.getVatTotal();
        resp.total = o.getTotal();
        return resp;
    }
}
