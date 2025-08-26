package org.example.trendyolfinalproject.dao.repository;

import jdk.dynalink.linker.LinkerServices;
import org.example.trendyolfinalproject.dao.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    Optional<Collection> findByUser_IdAndName(Long userId, String name);

    List<Collection> findByUser_Id(Long userId);

    Collection findByShareToken(String shareToken);
}
