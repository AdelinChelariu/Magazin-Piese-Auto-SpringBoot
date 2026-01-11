package ro.ugal.autopartsstore.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.dto.auth.RegisterRequest;
import ro.ugal.autopartsstore.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        service.registerClient(req);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
