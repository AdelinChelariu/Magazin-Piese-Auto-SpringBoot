package ro.ugal.autopartsstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "customer_addresses")
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String country = "Romania";

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String city;

    @NotBlank
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String street;

    @Size(max = 20)
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    // getters/setters
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
