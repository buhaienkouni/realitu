package ua.kpi.realitu.auth;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.repository.UserRepository;

import java.io.Serializable;

@Service
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private UserRepository userRepository;

//    private boolean permissionUpdateUser(Authentication authentication, Object targetDomainObject) {
//
//        if (targetDomainObject instanceof UserSenotyDto senotyDto) {
//
//            User principal = (User) authentication.getPrincipal();
//            UserEntity principalUser = userRepository.findByUsername(principal.getUsername()).orElse(null);
//            UserEntity targetUser = userRepository.findByUsername(senotyDto.getUsername()).orElse(null);
//
//            if (principalUser == null) {
//                return false;
//            }
//            if (targetUser == null) {
//                return true;
//            }
//
//            return targetUser.getId().equals(principalUser.getId()) || targetUser.getParentUser().getId().equals(principalUser.getId());
//        }
//        return false;
//    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
