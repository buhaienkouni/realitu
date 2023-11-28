package ua.kpi.realitu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.repository.UserRepository;
import ua.kpi.realitu.service.converter.UserDtoToEntityConverter;
import ua.kpi.realitu.service.converter.UserEntityToDtoConverter;
import ua.kpi.realitu.web.model.NewUserDto;
import ua.kpi.realitu.web.model.UserDto;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDtoToEntityConverter userDtoToEntityConverter;

    @Autowired
    private UserEntityToDtoConverter userEntityToDtoConverter;

    public void createUser(NewUserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("User with username %s already exists".formatted(userDto.getUsername()));
        } else {
            UserEntity newUser = userDtoToEntityConverter.create(userDto);
            userRepository.save(newUser);
        }
    }

    public void updateUser(UserDto userDto) {
        UserEntity user = getUserById(userDto.getId());
        userDtoToEntityConverter.update(userDto, user);
        userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public List<UserDto> getAllCopywriters() {
        return userRepository.findAllByRole(Role.COPYWRITER).stream()
                .map(userEntityToDtoConverter::convert)
                .toList();
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with name %s.".formatted(username)));
    }

    public UserEntity getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find user with id %s.".formatted(id)));
    }
}
