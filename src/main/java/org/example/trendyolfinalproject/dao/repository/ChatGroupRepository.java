package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.enums.GroupVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    Optional<ChatGroup> findByIdAndVisibility(Long id, GroupVisibility visibility);


    @Query("""
           SELECT g 
           FROM ChatGroup g
           WHERE LOWER(g.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    Page<ChatGroup> searchGroupsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Optional<ChatGroup> findByTitle(String title);

    Optional<ChatGroup> findByTitleAndOwner(String title, User owner);
}
