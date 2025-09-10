package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userUseCase = new UserUseCase(userRepository);
    }

    @Test
    void registerUser_ShouldReturnError_WhenEmailExists() {
        User user = User.builder()
                .email("test@example.com")
                .build();

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.registerUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Email ya existe")
                )
                .verify();

        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void registerUser_ShouldSaveUser_WhenEmailDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(Mono.just(false));
        when(userRepository.saveUser(any(User.class)))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.registerUser(user))
                .assertNext(savedUser -> {
                    assertThat(savedUser.getId()).isEqualTo(1L);
                    assertThat(savedUser.getFirstName()).isEqualTo("John");
                })
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveUser(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("john@example.com");
    }
}
