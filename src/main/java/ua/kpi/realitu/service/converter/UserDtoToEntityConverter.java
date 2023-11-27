package ua.kpi.realitu.service.converter;

import org.springframework.stereotype.Component;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.web.model.NewUserDto;
import ua.kpi.realitu.web.model.UserDto;

@Component
public class UserDtoToEntityConverter {

    public UserEntity create(NewUserDto userDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(userDto.getUsername());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setRole(Role.COPYWRITER);
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setTelegram(userDto.getTelegram());

        return userEntity;
    }
    
    public UserEntity update(UserDto userDto, UserEntity userEntity) {

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setTelegram(userDto.getTelegram());

        return userEntity;
    }
}
