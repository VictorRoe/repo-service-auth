package co.com.pragma.api.dto;

import java.time.LocalDate;

public record CreateUserDTO (
        String firstName,
        String lastName,
        String email,
        String password,
        String documentId,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        Long baseSalary,
        Long roleId
) {
}
