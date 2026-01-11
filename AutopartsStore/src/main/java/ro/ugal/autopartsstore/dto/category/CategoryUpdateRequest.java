package ro.ugal.autopartsstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryUpdateRequest {
    @NotBlank
    @Size(max = 120)
    public String name;

    public Long parentId; // null = no parent
}
