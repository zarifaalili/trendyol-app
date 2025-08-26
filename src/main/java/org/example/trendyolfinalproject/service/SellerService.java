package org.example.trendyolfinalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.BasketRepository;
import org.example.trendyolfinalproject.dao.repository.SellerRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.mapper.SellerMapper;
import org.example.trendyolfinalproject.model.NotificationType;
import org.example.trendyolfinalproject.model.Role;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.request.SellerCreateRequest;
import org.example.trendyolfinalproject.response.SellerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final SellerMapper sellerMapper;
    private final BasketRepository basketRepository;
    private final NotificationService notificationService;

    public SellerResponse createSeller(SellerCreateRequest request) {

        log.info("Actionlog.createSeller.start : companyName={}", request.getCompanyName());

        Long sellerId = request.getUserId();

        User user = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole().equals(Role.SELLER)) {
            throw new AlreadyException("User is already a seller");
        }
        if (user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You cant be a seller");
        }

        if (sellerRepository.existsByCompanyName(request.getCompanyName())) {
            throw new AlreadyException("Company name already exists");
        }
        if (sellerRepository.existsByTaxId(request.getTaxId())) {
            throw new AlreadyException("Tax ID already exists");
        }


        var basket = basketRepository.findByUserId(sellerId);
        basket.ifPresent(basketRepository::delete);


//        if(!user.getRole().equals(Role.SELLER)){
//            throw new RuntimeException("User is not a seller");
//        }


        user.setRole(Role.SELLER);
        user.setIsActive(false);
        userRepository.save(user);

        var seller = sellerMapper.toEntity(request);
        seller.setUser(user);
        seller.setStatus(Status.PENDING);
        seller.setContactEmail(user.getEmail());
        sellerRepository.save(seller);
        var response = sellerMapper.toResponse(seller);
        notificationService.sendToAdmins("New seller request", NotificationType.SELLER_REQUEST, seller.getId());
        notificationService.sendNotification(user, "Your seller account has been created. Please wait for approval", NotificationType.SELLER_CREATED, seller.getId());
        log.info("Actionlog.createSeller.end : sellerName={}", request.getCompanyName());
        return response;
    }

    public List<SellerResponse> getSellers() {
        var sellers = sellerRepository.findAll();
        if (!sellers.isEmpty()) {
            return sellers.stream().map(sellerMapper::toResponse).toList();
        }
        return List.of();
    }


    public SellerResponse getSeller(String companyName) {
        Seller seller = sellerRepository.findFirstByCompanyName(companyName)
                .orElseThrow(() -> new RuntimeException("Seller not found with company name: " + companyName));
        return sellerMapper.toResponse(seller);
    }


}
