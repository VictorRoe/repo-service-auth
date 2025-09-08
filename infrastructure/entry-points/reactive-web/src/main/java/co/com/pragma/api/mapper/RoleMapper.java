package co.com.pragma.api.mapper;

import co.com.pragma.model.user.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    default Role fromId(Long id) {
        return new Role(id, null, null);
    }
}
