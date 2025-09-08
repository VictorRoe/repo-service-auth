package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
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

    public MyUserRepositoryAdapter(MyUserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator, RoleRepository roleRepository) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
        this.roleRepository = roleRepository;
    }

    @Override
    public Mono<User> saveUser(User user) {
        UserEntity entity = new UserEntity(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDocumentId(),
                user.getBirthDate(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getBaseSalary(),
                user.getRole().getId()
        );

        return repository.save(entity)
                .flatMap(savedEntity ->
                        roleRepository.findById(savedEntity.getRoleId())
                                .map(role -> new User(
                                        savedEntity.getId(),
                                        savedEntity.getFirstName(),
                                        savedEntity.getLastName(),
                                        savedEntity.getEmail(),
                                        savedEntity.getDocumentId(),
                                        savedEntity.getBirthDate(),
                                        savedEntity.getAddress(),
                                        savedEntity.getPhoneNumber(),
                                        savedEntity.getBaseSalary(),
                                        role
                                ))
                )
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
