package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ReturnRequest;
import org.example.trendyolfinalproject.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByUser(User user);

    List<ReturnRequest> findByIsApproved(boolean isApproved);

    Optional<ReturnRequest> findByOrderItem_IdAndUser_Id(Long orderItemId, Long userId);
}
