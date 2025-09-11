package co.com.pragma.api.dto;

public record AuthRequestDTO(
        String email,
        String password
) {
}
