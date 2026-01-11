package ro.ugal.autopartsstore.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.dto.client.CheckoutRequest;
import ro.ugal.autopartsstore.dto.client.CheckoutResponse;
import ro.ugal.autopartsstore.service.ClientOrderService;

@RestController
@RequestMapping("/api/client")
public class ClientOrderController {

    private final ClientOrderService service;

    public ClientOrderController(ClientOrderService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest req) {
        return service.checkout(req);
    }
}
