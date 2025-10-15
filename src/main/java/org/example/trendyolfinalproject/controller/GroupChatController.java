package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.ChatGroupRequest;
import org.example.trendyolfinalproject.model.request.ChatMessageRequest;
import org.example.trendyolfinalproject.model.request.MessageRequest;
import org.example.trendyolfinalproject.model.response.*;
import org.example.trendyolfinalproject.service.GroupChatService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groupChats")
@RequiredArgsConstructor
public class GroupChatController {


    private final GroupChatService groupChatService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatGroupResponse>> createGroup(@RequestBody @Valid ChatGroupRequest chatGroupRequest) {
        ChatGroupResponse response = groupChatService.createGroupChat(chatGroupRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<Void>> addMembers(@PathVariable Long groupId,
                                                        @RequestBody List<Long> memberIds) {
        groupChatService.addMemmbers(groupId, memberIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @PostMapping("/{groupId}/membersWithEmail")
    public ResponseEntity<ApiResponse<Void>> addMembersWithEmail(@PathVariable Long groupId,
                                                                 @RequestBody List<String> emails) {
        groupChatService.addMemmbersWithEmail(groupId, emails);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable Long groupId,
                                                          @PathVariable Long userId) {
        groupChatService.removeMember(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{groupId}/messages")
    public ResponseEntity<ApiResponse<Void>> sendMessage(@PathVariable Long groupId,
                                                         @RequestBody @Valid ChatMessageRequest chatMessageRequest) {
        groupChatService.sendMessage(groupId, chatMessageRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(null, "Message sent"));
    }


    @DeleteMapping("/{groupId}/messages/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable Long groupId,
                                                           @PathVariable Long messageId,
                                                           @RequestParam String scope) {
        groupChatService.deleteMessage(groupId, messageId, scope);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<ApiResponse<Page<ChatMessageResponse>>> getGroupMessages(@PathVariable Long groupId,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(groupChatService.getGroupMessages(groupId, page, size)));
    }


    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<Void>> joinGroup(@PathVariable Long groupId) {
        groupChatService.joinGroup(groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(null, "Joined group"));
    }

    @PostMapping("{groupId}/request")
    public ResponseEntity<ApiResponse<String>> requestToJoinGroup(@PathVariable Long groupId,
                                                                  @RequestBody @Valid MessageRequest messageRequest) {
        groupChatService.sendJoinRequest(groupId, messageRequest);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(HttpStatus.CREATED.value())
                .message("Request sent")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{groupId}/requests")
    public ResponseEntity<ApiResponse<List<JoinRequestResponse>>> getJoinRequests(@PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.success(groupChatService.getJoinRequests(groupId)));
    }

    @PatchMapping("/{groupId}/requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<Void>> acceptJoinRequest(@PathVariable Long groupId,
                                                               @PathVariable Long requestId) {
        groupChatService.approveJoinRequest(groupId, requestId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{groupId}/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<Void>> declineJoinRequest(@PathVariable Long groupId,
                                                                @PathVariable Long requestId) {
        groupChatService.rejectJoinRequest(groupId, requestId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{groupId}/requests/{requestId}")
    public ResponseEntity<ApiResponse<Void>> deleteJoinRequest(@PathVariable Long groupId,
                                                               @PathVariable Long requestId) {
        groupChatService.deleteJoinRequest(groupId, requestId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @PatchMapping("/{groupId}/members/{memberId}/set-admin")
    public ResponseEntity<ApiResponse<Void>> setAdmin(@PathVariable Long groupId,
                                                      @PathVariable Long memberId) {
        groupChatService.setAdmin(groupId, memberId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(@PathVariable Long groupId) {
        groupChatService.leaveGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{groupId}/message/{messageId}/pin")
    public ResponseEntity<ApiResponse<Void>> pinMessage(@PathVariable Long groupId,
                                                        @PathVariable Long messageId) {
        groupChatService.pinMessage(messageId, groupId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @DeleteMapping("/{groupId}/message/{messageId}/unpin")
    public ResponseEntity<ApiResponse<Void>> unpinMessage(@PathVariable Long groupId,
                                                          @PathVariable Long messageId) {
        groupChatService.unpinMessage(messageId, groupId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @GetMapping("/{groupId}/messages/search")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> searchMessages(@PathVariable Long groupId,
                                                                                 @RequestParam String keyword,
                                                                                 @RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(groupChatService.searchMessages(groupId, keyword, page, size)));
    }


    @GetMapping("/{groupId}/message/{messageId}/read-status")
    public ResponseEntity<ApiResponse<List<MessageReadStatusResponse>>> getMessageReadStatus(@PathVariable Long groupId,
                                                                                             @PathVariable Long messageId) {

        return ResponseEntity.ok(ApiResponse.success(groupChatService.getMessageReadStatus(messageId, groupId)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ChatGroupResponse>>> searchGroup(@RequestParam String keyword,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(groupChatService.searchGroups(keyword, page, size)));
    }


}




