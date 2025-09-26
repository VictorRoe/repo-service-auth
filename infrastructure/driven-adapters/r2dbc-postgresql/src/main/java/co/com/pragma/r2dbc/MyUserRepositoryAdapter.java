package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class MyUserRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        Long,
        MyUserReactiveRepository
> implements UserRepository {

    private  final TransactionalOperator transactionalOperator;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public MyUserRepositoryAdapter(MyUserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Mono<User> saveUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        UserEntity entity = new UserEntity();
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPassword(encodedPassword);
        entity.setDocumentId(user.getDocumentId());
        entity.setBirthDate(user.getBirthDate());
        entity.setAddress(user.getAddress());
        entity.setPhoneNumber(user.getPhoneNumber());
        entity.setBaseSalary(user.getBaseSalary());
        entity.setRoleId(user.getRole().getId());

        return repository.save(entity)
                .flatMap(this::toDomain)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .flatMap(this::toDomain);
    }

    private Mono<User> toDomain(UserEntity userEntity) {
        return roleRepository.findById(userEntity.getRoleId())
                .map(role -> new User(
                        userEntity.getId(),
                        userEntity.getFirstName(),
                        userEntity.getLastName(),
                        userEntity.getEmail(),
                        userEntity.getPassword(),
                        userEntity.getDocumentId(),
                        userEntity.getBirthDate(),
                        userEntity.getAddress(),
                        userEntity.getPhoneNumber(),
                        userEntity.getBaseSalary(),
                        role
                ));
    }
}
