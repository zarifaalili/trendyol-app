package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ReturnRequest;
import org.example.trendyolfinalproject.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByUser(User user);

    List<ReturnRequest> findByIsApproved(boolean isApproved);
}
