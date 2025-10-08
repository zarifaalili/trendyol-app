package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.ChatGroupRequest;
import org.example.trendyolfinalproject.model.request.ChatMessageRequest;
import org.example.trendyolfinalproject.model.request.MessageRequest;
import org.example.trendyolfinalproject.model.response.ChatGroupResponse;
import org.example.trendyolfinalproject.model.response.ChatMessageResponse;
import org.example.trendyolfinalproject.model.response.JoinRequestResponse;
import org.example.trendyolfinalproject.model.response.MessageReadStatusResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GroupChatService {

    ChatGroupResponse createGroupChat(ChatGroupRequest chatGroupRequest);

    void addMemmbers(Long groupId, List<Long> userIds);

    void addMemmbersWithEmail(Long groupId, List<String> emails);

    void removeMember(Long groupId, Long userIdToRemove);

    void sendMessage(Long groupId, ChatMessageRequest dto);

    void deleteMessage(Long groupId, Long messageId, String scope);

    Page<ChatMessageResponse> getGroupMessages(Long groupId, int page, int size);

    void joinGroup(Long groupId);

    String sendJoinRequest(Long groupId, MessageRequest message);

    List<JoinRequestResponse> getJoinRequests(Long groupId);

    void approveJoinRequest(Long groupId, Long requestId);

    void rejectJoinRequest(Long groupId, Long requestId);

    void deleteJoinRequest(Long groupId, Long requestId);

    void setAdmin(Long groupId, Long newAdminId);

    void leaveGroup(Long groupId);

    void pinMessage(Long messageId, Long groupId);

    void unpinMessage(Long messageId, Long groupId);

    List<ChatMessageResponse> searchMessages(Long groupId, String keyword, int page, int size);

    List<MessageReadStatusResponse> getMessageReadStatus(Long messageId, Long groupId);

    List<ChatGroupResponse> searchGroups(String keyword, int page, int size);

}
