package co.com.pragma.api;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

        private final UserUseCase userCase;
        private final TransactionalOperator txOperator;

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(userCase::registerUser)
                .as(txOperator::transactional) // Se registra Usuario Utilizando Transaccional
                .flatMap(savedUser -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser))
                .onErrorResume(error -> ServerResponse
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(
                                Map.of(
                                        "error", error.getClass().getSimpleName(),
                                        "message" ,error.getMessage()
                                )
                        ));
    }
}
