package ua.kpi.realitu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAllByRole(Role role);
}
