package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Adress;
import org.example.trendyolfinalproject.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface AdressRepository extends JpaRepository<Adress, Long> {

    List<Adress> findAllByUserId_Id(Long userId);

    Optional<Adress> findByUserId_IdAndCityAndStateAndStreetAndZipCodeAndCountry(
            Long userId, String city, String state, String street, String zipCode, String country);

    Object findByUserIdAndIsDefault(User userId, Boolean isDefault);

    List<Adress> findByUserId(User userId);
}

