package ro.ugal.autopartsstore.dto.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CheckoutRequest {
    public String notes;

    @NotNull
    public List<Item> items;

    public static class Item {
        @NotNull
        public Long productId;

        @Min(1)
        public int quantity;
    }
}
