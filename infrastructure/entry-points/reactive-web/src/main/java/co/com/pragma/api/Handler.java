package co.com.pragma.api;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import co.com.pragma.api.dto.AuthRequestDTO;
import co.com.pragma.api.dto.AuthResponseDTO;
import co.com.pragma.api.dto.CreateUserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.api.security.AuthenticationManager;
import co.com.pragma.api.security.JwtUtil;
import co.com.pragma.api.security.UserDetailsAdapter;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    @Value("${internal.api-key}")
    private String internalApiKey;

    private final UserUseCase useCase;
    private final UserDTOMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AuthRequestDTO.class)
                .flatMap(dto -> {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
                    return this.authenticationManager.authenticate(authentication)
                            .flatMap(auth -> {
                                UserDetailsAdapter userDetails = (UserDetailsAdapter) auth.getPrincipal();
                                String token = jwtUtil.generateToken(userDetails.getDomainUser());
                                return ServerResponse.ok().bodyValue(new AuthResponseDTO(token));
                            });
                })
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'ASESOR')")
    public Mono<ServerResponse> registerUser(ServerRequest request) {
        log.info("[registerUser] Petición recibida para registrar usuario");
        return request.bodyToMono(CreateUserDTO.class)
                .doOnNext(dto -> log.debug("[registerUser] Payload recibido: {}", dto))
                .map(dto -> {
                    String error = validateRequiredFields(dto);
                    if (nonNull(error)) {
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

    public Mono<ServerResponse> getUserDetailsByEmail(ServerRequest serverRequest) {

        String apiKeyHeader = serverRequest.headers().header("X-API-KEY").stream().findFirst().orElse(null);
        if (!internalApiKey.equals(apiKeyHeader)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = serverRequest.pathVariable("email");


        return useCase.findByEmail(email)
                .map(userMapper::toDetailDTO)
                .flatMap(dto -> ServerResponse.ok().bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private String validateRequiredFields(CreateUserDTO dto) {
        if (isNull(dto.firstName()) || dto.firstName().isBlank()) {
            return "El 'nombre' es obligatorio";
        }
        if (isNull(dto.lastName()) || dto.lastName().isBlank()) {
            return "El 'apellido' es obligatorio";
        }
        if (isNull(dto.email()) || dto.email().isBlank()) {
            return "El 'correo_electronico' es obligatorio";
        }
        if (isNull(dto.documentId()) || dto.documentId().isBlank()) {
            return "El 'documentID' es obligatorio";
        }
        if (isNull(dto.baseSalary())) {
            return "El 'salario_base' es obligatorio";
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
