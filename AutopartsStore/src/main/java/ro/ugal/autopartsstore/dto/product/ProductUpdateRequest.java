package ro.ugal.autopartsstore.dto.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductUpdateRequest {
    @NotBlank @Size(max = 200)
    public String name;

    public String description;

    @NotNull
    public Long categoryId;

    @NotNull
    public Long manufacturerId;

    @NotNull @DecimalMin("0.00")
    public BigDecimal price;

    @NotNull @DecimalMin("0.00") @DecimalMax("100.00")
    public BigDecimal vatRate;

    @Min(0)
    public int stockQty;

    public boolean active = true;
}
