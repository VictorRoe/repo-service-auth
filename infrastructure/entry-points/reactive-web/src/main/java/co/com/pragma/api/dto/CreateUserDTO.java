package co.com.pragma.api.dto;

import java.time.LocalDate;

public record CreateUserDTO (
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        Long baseSalary
) {
}
