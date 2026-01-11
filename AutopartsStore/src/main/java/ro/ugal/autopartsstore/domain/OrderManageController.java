package ro.ugal.autopartsstore.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.dto.order.*;
import ro.ugal.autopartsstore.service.OrderManageService;
import ro.ugal.autopartsstore.spec.OrderManageFilter;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders/manage")
public class OrderManageController {

    private final OrderManageService service;

    public OrderManageController(OrderManageService service) {
        this.service = service;
    }

    @GetMapping
    public Page<OrderRowResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderDate") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        OrderManageFilter f = new OrderManageFilter();
        f.q = q;
        f.status = status;
        f.from = from;
        f.to = to;

        Sort.Direction direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        var pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return service.list(f, pageable);
    }

    @GetMapping("/{id}")
    public OrderDetailsResponse details(@PathVariable Long id) {
        return service.details(id);
    }

    @PutMapping("/{id}/status")
    public Long updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest req) {
        return service.updateStatus(id, req.status);
    }
}
