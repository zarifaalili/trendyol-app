package org.example.trendyolfinalproject.dao.repository;

import jakarta.transaction.Transactional;
import org.example.trendyolfinalproject.dao.entity.ResetCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResetCodeRepository extends CrudRepository<ResetCode, Long> {

    Optional<ResetCode> findByEmailAndCode(String email, String code);

    @Transactional
    @Modifying
    void deleteByEmail(String email);
}
