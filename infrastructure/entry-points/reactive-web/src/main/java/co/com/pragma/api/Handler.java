package co.com.pragma.api;

import co.com.pragma.api.dto.CreateUserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

private final UserUseCase useCase;
private final UserDTOMapper userMapper;


    public Mono<ServerResponse> registerUser(ServerRequest request) {

        return request.bodyToMono(CreateUserDTO.class)
                .map(userMapper::toModel) // DTO → Domain Model
                .flatMap(useCase::registerUser) // Lógica de negocio
                .map(userMapper::toResponse) // Domain Model → DTO
                .flatMap(userDTO ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userDTO)
                )
                .onErrorResume(e -> {
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new Exception("Error al registrar usuario"));
                });
    }
}
