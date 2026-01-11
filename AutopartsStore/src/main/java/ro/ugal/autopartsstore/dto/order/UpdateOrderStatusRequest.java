package ro.ugal.autopartsstore.dto.order;

import jakarta.validation.constraints.NotBlank;

public class UpdateOrderStatusRequest {
    @NotBlank
    public String status; // PLACED/PAID/SHIPPED/CANCELLED
}
