package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.entity.UserWheel;
import org.example.trendyolfinalproject.dao.entity.Wheel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserWheelRepository extends JpaRepository<UserWheel, Long> {
    Optional<UserWheel> findByUserAndWheel(User user, Wheel wheel);

    Optional<UserWheel> findByUser(User user);

    List<UserWheel> findByUser_Id(Long userId);


    List<UserWheel> findByUser_IdAndUsedAtIsNull(Long userId);
}
