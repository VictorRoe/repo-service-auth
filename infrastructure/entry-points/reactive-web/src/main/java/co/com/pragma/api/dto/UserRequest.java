package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UserDTO(
        @NotBlank(message = "Firstname is required")
        String name,
        @NotBlank(message = "Lastname is required")
        String lastname,
        @NotBlank(message = "Email is required")
        @Email
        String email,
        @NotNull(message = "Salary is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be greater than 0")
        @DecimalMax(value = "150000000.00", inclusive = true, message = "Salary must be less than 150000000")

        BigDecimal salaryBase
) {
}
