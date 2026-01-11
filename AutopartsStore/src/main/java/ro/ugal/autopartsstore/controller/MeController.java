package ro.ugal.autopartsstore.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
public class MeController {

    @GetMapping
    public Map<String, Object> me(Authentication auth) {
        return Map.of(
                "username", auth.getName(),
                "roles", auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList())
        );
    }
}
