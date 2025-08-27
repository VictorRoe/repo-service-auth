package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UserUseCase implements UserUseCaseImpl {

    private final UserRepository repository;
    private static final Logger LOG = Logger.getLogger(UserUseCase.class.getName());

    @Override
    public Mono<User> registerUser(User user) {
        LOG.info("Trying register user");
        return repository.ifEmailExist(user.getEmail())
                .flatMap(exist -> {
                    if (exist) {
                        LOG.warning("Email already exist");
                        return Mono.error(new IllegalArgumentException("This email already exist"));
                    }
                    LOG.info("successfully registered user");
                    return repository.saveUser(user);

                });
    }
}
