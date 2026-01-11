package ro.ugal.autopartsstore.dto.manufacturer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ManufacturerUpdateRequest {
    @NotBlank
    @Size(max = 120)
    public String name;

    @Size(max = 80)
    public String country;

    @Size(max = 200)
    public String website;
}
