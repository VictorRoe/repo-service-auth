package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.CreateUserDTO;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    UserDTO toResponse(User user);

    @Mapping(target = "role", expression = "java(toRole(createUserDTO.roleId()))")
    User toModel(CreateUserDTO createUserDTO);

    default Role toRole(Long roleId) {
        if (roleId == null) return null;
        Role role = new Role();
        role.setId(roleId);
        return role;
    }

}
