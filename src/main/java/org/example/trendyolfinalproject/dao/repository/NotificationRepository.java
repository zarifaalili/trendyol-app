package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Notification;
import org.example.trendyolfinalproject.model.DeliveryChannelType;
import org.example.trendyolfinalproject.model.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    Optional<Notification> findByUserIdAndIdAndDeliveryChannelType(Long userId, Long id, DeliveryChannelType deliveryChannelType);

    List<Notification> findByUserIdAndReadStatus(Long userId, ReadStatus readStatus);


    @Query("""
    SELECT n
    FROM Notification n
    WHERE LOWER(n.message) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    List<Notification> search(@Param("keyword") String keyword);

    List<Notification> findByReadStatus(ReadStatus readStatus);
}
