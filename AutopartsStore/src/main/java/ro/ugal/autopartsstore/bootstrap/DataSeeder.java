package ro.ugal.autopartsstore.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.ugal.autopartsstore.domain.Role;
import ro.ugal.autopartsstore.domain.User;
import ro.ugal.autopartsstore.repository.RoleRepository;
import ro.ugal.autopartsstore.repository.UserRepository;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(role("ROLE_ADMIN")));
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(role("ROLE_EMPLOYEE")));

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFullName("Administrator");
            admin.setEmail("admin@local");
            admin.setEnabled(true);
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole, employeeRole));
            userRepository.save(admin);
        }
    }

    private Role role(String name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }
}
