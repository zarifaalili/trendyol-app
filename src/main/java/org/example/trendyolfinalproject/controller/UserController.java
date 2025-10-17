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
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.ok().body(userService.updateUser(userRequest));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponse>> patchUpdateUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok().body(userService.patchUpdateUser(userRequest));
    }

    @PostMapping("/update/{newEmail}")
    public ResponseEntity<ApiResponse<String>> updateEmail(@PathVariable String newEmail) {
        return ResponseEntity.ok().body(userService.updateEmail(newEmail));
    }

    @PatchMapping("/verifyEmail/{email}/{otp}")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@PathVariable String email, @PathVariable String otp) {
        return ResponseEntity.ok().body(userService.verifyEmail(email, otp));
    }

    @PatchMapping("/deactivate-me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deactivateMe() {
        userService.deleteUser();
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile() {
        return ResponseEntity.ok().body(userService.getUserProfile());
    }

    @PatchMapping("/deactivate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactiveUser(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.deactiveUser(userId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUser(@RequestParam("keyword") String keyword,
                                                      @RequestParam("page") int page,
                                                      @RequestParam("size") int size) {
        return ResponseEntity.ok().body(userService.searchUser(keyword, page, size));
    }

    @GetMapping("/followed-sellers")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<SellerResponse>>> getFollowedSellers() {
        return ResponseEntity.ok().body(userService.getFollowedSellers());
    }



    @PostMapping("/{email}/refer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> refer(@PathVariable String email) throws MessagingException {
        return ResponseEntity.ok().body(userService.referTrendyol(email));
    }

}
