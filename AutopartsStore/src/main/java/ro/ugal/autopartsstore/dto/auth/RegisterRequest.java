package ro.ugal.autopartsstore.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 40)
    public String username;

    @NotBlank @Size(min = 6, max = 100)
    public String password;

    @NotBlank @Size(max = 100)
    public String fullName;

    @Size(max = 120)
    public String email;
}
