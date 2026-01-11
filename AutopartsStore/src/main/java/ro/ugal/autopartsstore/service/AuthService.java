package ro.ugal.autopartsstore.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ugal.autopartsstore.domain.Customer;
import ro.ugal.autopartsstore.domain.Role;
import ro.ugal.autopartsstore.domain.User;
import ro.ugal.autopartsstore.dto.auth.RegisterRequest;
import ro.ugal.autopartsstore.repository.CustomerRepository;
import ro.ugal.autopartsstore.repository.RoleRepository;
import ro.ugal.autopartsstore.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final CustomerRepository customerRepo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       CustomerRepository customerRepo,
                       PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.customerRepo = customerRepo;
        this.encoder = encoder;
    }

    @Transactional
    public void registerClient(RegisterRequest req) {
        if (userRepo.existsByUsername(req.username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        Role clientRole = roleRepo.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new IllegalArgumentException("ROLE_CLIENT not found. Run Flyway migrations."));

        User u = new User();
        u.setUsername(req.username.trim());
        u.setPasswordHash(encoder.encode(req.password));
        u.setFullName(req.fullName.trim());
        u.setEmail(req.email == null ? null : req.email.trim());
        u.setEnabled(true);

        userRepo.save(u);

        // presupunem că User are getRoles()
        u.getRoles().add(clientRole);
        userRepo.save(u);

        // Customer linked to user (needs V4 migration)
        Customer c = new Customer();
        c.setName(u.getFullName());
        c.setEmail(u.getEmail());
        c.setPhone(null);
        c.setUser(u); // presupunem că ai setUser(User) în Customer
        customerRepo.save(c);
    }
}
