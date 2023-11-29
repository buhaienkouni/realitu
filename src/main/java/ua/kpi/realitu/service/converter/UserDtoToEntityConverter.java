package ua.kpi.realitu.service.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.web.model.NewUserDto;
import ua.kpi.realitu.web.model.UserDto;

@Component
public class UserDtoToEntityConverter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity create(NewUserDto userDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername("@" + userDto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
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
