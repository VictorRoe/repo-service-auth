package co.com.pragma.api;

import co.com.pragma.api.dto.CreateUserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

private final UserUseCase useCase;
private final UserDTOMapper userMapper;


    public Mono<ServerResponse> registerUser(ServerRequest request) {
        log.info("[registerUser] Petición recibida para registrar usuario");
        return request.bodyToMono(CreateUserDTO.class)
                .doOnNext(dto -> log.debug("[registerUser] Payload recibido: {}", dto))
                .map(dto -> {
                    String error = validateRequiredFields(dto);
                    if (error != null) {
                        log.warn("[registerUser] Validación fallida: {}", error);
                        throw new IllegalArgumentException(error);
                    }
                    return dto;
                })
                .map(userMapper::toModel)
                .flatMap(useCase::registerUser)
                .flatMap(user ->
                        ServerResponse.created(URI.create("/api/v1/usuarios/" + user.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("mensaje", "Usuario registrado correctamente"))
                )
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                )
                .onErrorResume(error -> {
                    log.error("[registerUser] Error inesperado", error);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", "Error al registrar usuario"));
                });

    }

    private String validateRequiredFields(CreateUserDTO dto) {
        if (dto.firstName() == null || dto.firstName().isBlank()) {
            return "El campo 'nombres' es obligatorio";
        }
        if (dto.lastName() == null || dto.lastName().isBlank()) {
            return "El campo 'apellidos' es obligatorio";
        }
        if (dto.email() == null || dto.email().isBlank()) {
            return "El campo 'correo_electronico' es obligatorio";
        }
        if (dto.baseSalary() == null) {
            return "El campo 'salario_base' es obligatorio";
        }

        if (!dto.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "El formato del correo electrónico no es válido";
        }

        if (dto.baseSalary() < 0 || dto.baseSalary() > 15_000_000) {
            return "El salario base debe estar entre 0 y 15,000,000";
        }

        return null;
    }

}
