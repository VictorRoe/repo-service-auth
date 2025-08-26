package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> saveUser(User user);
    public Boolean ifEmailExist (User user); // TODO: Chequear si este metodo pertenese como UserRepository o UseCases

}
