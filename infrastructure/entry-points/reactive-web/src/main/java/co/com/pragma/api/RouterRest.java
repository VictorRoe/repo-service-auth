package co.com.pragma.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
@Tag(name = "User", description = "Creacion de usuario")
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = {"application/json"},
                    method = {org.springframework.web.bind.annotation.RequestMethod.POST},
                    beanClass = Handler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            summary = "Registra un usuario",
                            description = "Se registra un usuario en el sistema"
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::registerUser)
                .andRoute(RequestPredicates.POST("/api/v1/login"), handler::login);


    }
}
