package ua.kpi.realitu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.realitu.domain.Image;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
