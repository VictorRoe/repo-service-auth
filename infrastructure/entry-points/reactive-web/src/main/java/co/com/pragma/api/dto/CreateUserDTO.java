package co.com.pragma.api.dto;

import co.com.pragma.model.user.Role;

import java.time.LocalDate;

public record CreateUserDTO (
        Long id,
        String firstName,
        String lastName,
        String email,
        String documentId,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        Long baseSalary,
        Long roleId
) {
}
