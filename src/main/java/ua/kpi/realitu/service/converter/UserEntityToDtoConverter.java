package ua.kpi.realitu.service.converter;

import org.springframework.stereotype.Component;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.web.model.UserDto;

@Component
public class UserEntityToDtoConverter {

    public UserDto convert(UserEntity userEntity) {
        UserDto userDto = new UserDto();

        userDto.setId(userEntity.getId());
        userDto.setUsername(userEntity.getUsername());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setEmail(userEntity.getEmail());
        userDto.setPhone(userEntity.getPhone());
        userDto.setTelegram(userEntity.getTelegram());

        return userDto;
    }
}
