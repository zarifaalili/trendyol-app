package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Adress;
import org.example.trendyolfinalproject.dao.repository.AdressRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.AdressMapper;
import org.example.trendyolfinalproject.model.request.AdressCreateRequest;
import org.example.trendyolfinalproject.model.response.AdressResponse;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.service.AdressService;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdressServiceImpl implements AdressService {

    private final AdressRepository adressRepository;
    private final AdressMapper adressMapper;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    public ApiResponse<AdressResponse> createAdress(AdressCreateRequest request) {
        Long userId = getCurrentUserId();
        log.info("Actionlog.createAdress.start : userId={}", userId);
        var user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found with id: " + userId));
        var excistingAdress = adressRepository.findByUserId_IdAndCityAndStateAndStreetAndZipCodeAndCountry(
                userId, request.getCity(), request.getState(), request.getStreet(), request.getZipCode(), request.getCountry()
        );
        if (excistingAdress.isPresent()) {
            log.error("Adress already exists");
            throw new AlreadyException("Adress already exists");
        }
        var entity = adressMapper.toEntity(request);
        entity.setUserId(user);

        List<Adress> adresses = adressRepository.findAllByUserId_Id(userId);
        if (adresses.isEmpty()) {
            entity.setIsDefault(true);
        } else {
            entity.setIsDefault(false);
        }
        var saved = adressRepository.save(entity);
        var response = adressMapper.toResponse(saved);
        auditLogService.createAuditLog(user, "Create Adress", "Adress created successfully. Adress id: " + saved.getId());
        log.info("Actionlog.createAdress.end : userId={}", userId);

        return ApiResponse.<AdressResponse>builder()
                .status(200)
                .message("Adress created successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<String> deleteAdress(Long id) {

        Long currentUserId = getCurrentUserId();
        log.info("Actionlog.deleteAdress.start : id={}", id);
        var adress = adressRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Adress not found with id: " + id)
        );
        if (!adress.getUserId().getId().equals(currentUserId)) {
            log.error("You don't have permission to delete this Adress");
            throw new RuntimeException("You don't have permission to delete this Adress");
        }
        adressRepository.deleteById(id);
        var adreses = adressRepository.findAllByUserId_Id(currentUserId);
        if (adress.getIsDefault() == true && !adreses.isEmpty()) {
            adreses.get(0).setIsDefault(true);
        }
        adressRepository.save(adreses.get(0));
        auditLogService.createAuditLog(adress.getUserId(), "Delete Adress", "Adress deleted successfully. Adress id: " + id);
        log.info("Actionlog.deleteAdress.end : id={}", id);

        return ApiResponse.<String>builder()
                .status(200)
                .message("Adress deleted successfully")
                .data(null)
                .build();
    }

    @Override
    public ApiResponse<List<AdressResponse>> getAdresses() {
        log.info("Actionlog.getAdresses.start : ");
        Long currentUserId = getCurrentUserId();
        var user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + currentUserId));
        List<Adress> adresses = adressRepository.findAllByUserId_Id(currentUserId);
        if (adresses.isEmpty()) {
            log.error("User has no adresses");
            throw new NotFoundException("User has no adresses");
        }
        List<AdressResponse> response = adressMapper.toResponseList(adresses);
        auditLogService.createAuditLog(user, "Get all adresses", "Get all adresses successfully.");
        log.info("Actionlog.getAdresses.end : ");
        return ApiResponse.<List<AdressResponse>>builder()
                .status(200)
                .message("Adresses fetched successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<AdressResponse> updateAdress(Long id, AdressCreateRequest request) {
        Long currentUserId = getCurrentUserId();
        log.info("Actionlog.updateAdress.start : id={}", id);
        var adress = adressRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Adress not found with id: " + id)
        );
        if (!adress.getUserId().getId().equals(currentUserId)) {
            log.error("You don't have permission to update this Adress");
            throw new RuntimeException("You don't have permission to update this Adress");
        }
        if (request.getCity() != null) adress.setCity(request.getCity());
        if (request.getState() != null) adress.setState(request.getState());
        if (request.getStreet() != null) adress.setStreet(request.getStreet());
        if (request.getZipCode() != null) adress.setZipCode(request.getZipCode());
        if (request.getCountry() != null) adress.setCountry(request.getCountry());

        var saved = adressRepository.save(adress);
        var response = adressMapper.toResponse(saved);
        var user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + currentUserId));
        auditLogService.createAuditLog(user, "Update Adress", "Adress updated successfully. Adress id: " + id);
        log.info("Actionlog.updateAdress.end : id={}", id);
        return ApiResponse.<AdressResponse>builder()
                .status(200)
                .message("Adress updated successfully")
                .data(response)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }



}
