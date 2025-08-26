package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> saveUser(User user);
    Mono<Boolean> ifEmailExist (String email); // TODO: Chequear si este metodo pertenese como UserRepository

}
