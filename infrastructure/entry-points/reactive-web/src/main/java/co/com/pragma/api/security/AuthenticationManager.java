package co.com.pragma.api.security;

import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String email = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password , user.getPassword()))
                .switchIfEmpty(Mono.error(new Exception("Invalid credentials")))
                .map(user -> {
                    UserDetails userDetails = new UserDetailsAdapter(user);
                    return new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());
                });
    }

}
