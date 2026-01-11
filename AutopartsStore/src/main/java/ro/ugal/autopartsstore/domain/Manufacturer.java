package ro.ugal.autopartsstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "manufacturers")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Size(max = 80)
    @Column(length = 80)
    private String country;

    @Size(max = 200)
    @Column(length = 200)
    private String website;

    // getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
