package org.example.trendyolfinalproject.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.UserRegisterRequest;
import org.example.trendyolfinalproject.request.UserRequest;
import org.example.trendyolfinalproject.response.*;
import org.example.trendyolfinalproject.service.SellerFollowService;
import org.example.trendyolfinalproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SellerFollowService sellerFollowService;

    @PostMapping("/signUp")
    public ApiResponse<String> registerOrLoginUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.registerUser(userRegisterRequest);
    }


    @PostMapping("/signUp/verify-otp/{email}/{otp}")
    public ApiResponse<AuthResponse> verifyOtp(@PathVariable String email,
                                               @PathVariable String otp,
                                               @RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.verifyOtp(email, otp, userRegisterRequest);
    }

    @PutMapping("/putUpdateUser")
    public ApiResponse<UserResponse> updateUser(@RequestBody @Valid UserRequest userRequest) {
        return userService.updateUser(userRequest);
    }


    @PatchMapping("/patchUpdateUser")
    public ApiResponse<UserResponse> patchUpdateUser(@RequestBody UserRequest userRequest) {
        return userService.patchUpdateUser(userRequest);
    }

    @PostMapping("/updateEmail/{newEmail}")
    public ApiResponse<String> updateEmail(@PathVariable String newEmail) {
        return userService.updateEmail(newEmail);
    }

    @PatchMapping("/verifyEmail/{email}/{otp}")
    public ApiResponse<String> verifyEmail(@PathVariable String email, @PathVariable String otp) {
        return userService.verifyEmail(email, otp);
    }


    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getUserProfile")
    public ApiResponse<UserProfileResponse> getUserProfile() {
        return userService.getUserProfile();
    }


    @PatchMapping("/deactiveUser")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String deactiveUser() {
        return userService.deactiveUser();
    }

    @PostMapping("/activateUser")
    public String activeUser(@PathParam("email") String email) {
        return userService.activateUser(email);
    }

    @PatchMapping("/verifyReactivateOtp")
    public String verifyReactivateOtp(@PathParam("email") String email, @PathParam("otp") String otp) {
        return userService.verifyReactivateOtp(email, otp);
    }

    @GetMapping("/searchUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> searchUser(@PathParam("keyword") String keyword) {
        return userService.searchUser(keyword);
    }

    @GetMapping("/followed/sellers")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<SellerResponse>> getFollowedSellers() {
        return userService.getFollowedSellers();
    }


    @DeleteMapping("/unfollow/seller/{sellerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> unfollowSeller(@PathVariable Long sellerId) {
        return sellerFollowService.unfollow(sellerId);
    }


    @PostMapping("/refer/{email}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> refer(@PathVariable String email) throws MessagingException {
        return userService.referTrendyol(email);
    }
}
