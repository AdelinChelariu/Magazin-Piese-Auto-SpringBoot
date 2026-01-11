package ro.ugal.autopartsstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 140)
    @Column(nullable = false, length = 140)
    private String name;

    @Size(max = 40)
    @Column(length = 40)
    private String phone;

    @Email
    @Size(max = 120)
    @Column(length = 120)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerAddress> addresses = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }


    // getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getCreatedAt() { return createdAt; }
    public List<CustomerAddress> getAddresses() { return addresses; }
    public void setAddresses(List<CustomerAddress> addresses) { this.addresses = addresses; }
}
