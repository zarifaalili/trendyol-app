package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.GroupMessage;
import org.example.trendyolfinalproject.dao.entity.MessageReadStatus;
import org.example.trendyolfinalproject.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {

    List<MessageReadStatus> findByMessage_Group_IdAndUser_IdAndIsReadFalse(Long groupId, Long userId);

    List<MessageReadStatus> findByMessage(GroupMessage message);


    List<MessageReadStatus> findByMessage_Group_IdAndUser_Id(Long groupId, Long userId);

    List<MessageReadStatus> findByMessage_Id(Long messageId);


    @Query("SELECT mrs FROM MessageReadStatus mrs " +
            "JOIN FETCH mrs.message msg " +
            "JOIN FETCH mrs.user u " +
            "WHERE msg.id = :messageId")
    List<MessageReadStatus> findByMessage_IdWithUser(@Param("messageId") Long messageId);

}
