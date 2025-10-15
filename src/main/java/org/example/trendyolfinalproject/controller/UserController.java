package org.example.trendyolfinalproject.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.UserRegisterRequest;
import org.example.trendyolfinalproject.model.request.UserRequest;
import org.example.trendyolfinalproject.model.request.VerifyAndRegisterRequest;
import org.example.trendyolfinalproject.model.response.*;
import org.example.trendyolfinalproject.service.SellerFollowService;
import org.example.trendyolfinalproject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping
    public ApiResponse<UserResponse> updateUser(@RequestBody @Valid UserRequest userRequest) {
        return userService.updateUser(userRequest);
    }

    @PatchMapping
    public ApiResponse<UserResponse> patchUpdateUser(@RequestBody UserRequest userRequest) {
        return userService.patchUpdateUser(userRequest);
    }

    @PostMapping("/update/{newEmail}")
    public ApiResponse<String> updateEmail(@PathVariable String newEmail) {
        return userService.updateEmail(newEmail);
    }

    @PatchMapping("/verifyEmail/{email}/{otp}")
    public ApiResponse<String> verifyEmail(@PathVariable String email, @PathVariable String otp) {
        return userService.verifyEmail(email, otp);
    }

    @PatchMapping("/deactivate-me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deactivateMe() {
        userService.deleteUser();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getUserProfile() {
        return userService.getUserProfile();
    }

    @PatchMapping("/deactivate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deactiveUser(@PathVariable Long userId) {
        return userService.deactiveUser(userId);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<UserResponse>> searchUser(@RequestParam("keyword") String keyword,
                                                      @RequestParam("page") int page,
                                                      @RequestParam("size") int size) {
        return userService.searchUser(keyword, page, size);
    }

    @GetMapping("/followed-sellers")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<SellerResponse>> getFollowedSellers() {
        return userService.getFollowedSellers();
    }



    @PostMapping("/{email}/refer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> refer(@PathVariable String email) throws MessagingException {
        return userService.referTrendyol(email);
    }

}
