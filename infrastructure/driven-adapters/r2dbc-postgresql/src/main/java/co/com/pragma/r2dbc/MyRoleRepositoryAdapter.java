package co.com.pragma.r2dbc;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MyRoleRepositoryAdapter extends ReactiveAdapterOperations<
        Role,
        RoleEntity,
        Long,
        MyRoleReactiveRepository
        > implements RoleRepository {

    public MyRoleRepositoryAdapter(MyRoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }

    @Override
    public Mono<Role> findById(Long id) {
        return repository.findById(id)
                .map(entity -> new Role(entity.getId(), entity.getName(), entity.getDescription()));
    }


}
