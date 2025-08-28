package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;


public interface UserUserCaseImpl {

    Mono<User> registerUser(User user);
}
