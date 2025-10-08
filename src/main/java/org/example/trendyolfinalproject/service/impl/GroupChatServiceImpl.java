package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.*;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ChatGroupMapper;
import org.example.trendyolfinalproject.mapper.ChatMessageMapper;
import org.example.trendyolfinalproject.mapper.JoinRequestMapper;
import org.example.trendyolfinalproject.mapper.MessageReadStatusMapper;
import org.example.trendyolfinalproject.model.enums.*;
import org.example.trendyolfinalproject.model.request.AttachmentRequest;
import org.example.trendyolfinalproject.model.request.ChatGroupRequest;
import org.example.trendyolfinalproject.model.request.ChatMessageRequest;
import org.example.trendyolfinalproject.model.request.MessageRequest;
import org.example.trendyolfinalproject.model.response.*;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.GroupChatService;
import org.example.trendyolfinalproject.service.GroupMessageService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupChatServiceImpl implements GroupChatService {

    private final ChatGroupRepository groupChatRepository;
    private final ChatGroupMapper chatGroupMapper;
    private final GroupMessageService groupMessageService;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final ProductVariantRepository productVariantRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final AttachmentRepository attachmentRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final StarredGroupMessageRepository starredGroupMessageRepository;
    private final DeletedMessageRepository deletedMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final MessageReadStatusRepository messageReadStatusRepository;
    private final JoinRequestRepository joinRequestRepository;
    private final JoinRequestMapper joinRequestMapper;
    private final GroupMessageSearchRepository groupMessageSearchRepository;
    private final MessageReadStatusMapper messageReadStatusMapper;


    @Override
    public ChatGroupResponse createGroupChat(ChatGroupRequest chatGroupRequest) {
        log.info("ActionLog.createGroupChat.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var groupChat = chatGroupMapper.toEntity(chatGroupRequest);
        groupChat.setOwner(user);
        groupChatRepository.save(groupChat);

        groupMessageService.generateAndSaveGroupKey(groupChat);

        GroupMember owner = new GroupMember();
        owner.setGroup(groupChat);
        owner.setUser(user);
        owner.setRole(GroupRole.OWNER);
        owner.setJoinedAt(LocalDateTime.now());
        owner.setAddedById(userId);
        groupChatRepository.save(groupChat);

        groupChat.getMembers().add(owner);
        groupMemberRepository.save(owner);


        if (chatGroupRequest.getMemberIds() != null) {
            for (Long memberId : chatGroupRequest.getMemberIds()) {
                if (!memberId.equals(userId)) {
                    GroupMember member = new GroupMember();
                    member.setGroup(groupChat);
                    member.setUser(userRepository.findById(memberId).orElseThrow(() -> new NotFoundException("User not found with id: " + memberId)));
                    member.setRole(GroupRole.MEMBER);
                    member.setJoinedAt(LocalDateTime.now());
                    groupMemberRepository.save(member);
                    groupChat.getMembers().add(member);
                }
            }
        }


        var response = chatGroupMapper.toResponse(groupChat);
        log.info("ActionLog.createGroupChat.end : ");
        return response;


    }

    @Override
    public void addMemmbers(Long groupId, List<Long> userIds) {
        var userId = getCurrentUserId();
        log.info("ActionLog.addMembers.start memberId: {}, groupId: {}, memberIds: {}", userId, groupId, userIds);
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        var member = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                () -> new NotFoundException("Member not found with id: " + userId)
        );


        var memberPermission = group.getAddMemberPermissions();
        var memberRole = member.getRole();


        if (member.getMuted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are muted in this group.");
        }

        if (memberRole.equals(GroupRole.LEAVED) ||
                memberRole.equals(GroupRole.REMOVED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are leaved or removed from this group.");
        }

        if (memberPermission.equals(GroupPermission.OWNERS_AND_ADMINS)) {
            if (!memberRole.equals(GroupRole.OWNER) && !memberRole.equals(GroupRole.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners and admins can add new members to this group.");
            }
        }


        for (Long id : userIds) {

            var existingMember = groupMemberRepository.findByGroupAndUser_Id(group, id);

            if (existingMember.isEmpty()) {
                var groupMember = new GroupMember();
                groupMember.setGroup(group);
                groupMember.setUser(userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id)));
                groupMember.setRole(GroupRole.MEMBER);
                groupMember.setJoinedAt(LocalDateTime.now());
                groupMember.setAddedById(getCurrentUserId());
                groupMemberRepository.save(groupMember);
                group.getMembers().add(groupMember);
            }

        }


        log.info("ActionLog.addMembers.end memberId: {}, groupId: {}, memberIds: {}", userIds, groupId, userIds);
    }


    @Override
    public void addMemmbersWithEmail(Long groupId, List<String> emails) {
        log.info("ActionLog.addMemmbersWithEmail.start memberId: {}, groupId: {}, emails: {}", getCurrentUserId(), groupId, emails);

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        var member = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                () -> new NotFoundException("Member not found with id: " + getCurrentUserId())
        );

        var memberPermission = group.getAddMemberPermissions();
        var memberRole = member.getRole();


        if (member.getMuted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are muted in this group.");
        }

        if (memberRole.equals(GroupRole.LEAVED) ||
                memberRole.equals(GroupRole.REMOVED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are leaved or removed from this group.");
        }

        if (memberPermission.equals(GroupPermission.OWNERS_AND_ADMINS)) {
            if (!memberRole.equals(GroupRole.OWNER) && !memberRole.equals(GroupRole.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners and admins can add new members to this group.");
            }
        }


        for (String email : emails) {

            var userWithEmail = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found with email: " + email));
            var id = userWithEmail.getId();
            var existingMember = groupMemberRepository.findByGroupAndUser_Id(group, id);

            if (existingMember.isEmpty()) {
                var groupMember = new GroupMember();
                groupMember.setGroup(group);
                groupMember.setUser(userWithEmail);
                groupMember.setRole(GroupRole.MEMBER);
                groupMember.setJoinedAt(LocalDateTime.now());
                groupMember.setAddedById(getCurrentUserId());
                groupMemberRepository.save(groupMember);
                group.getMembers().add(groupMember);
            }

        }
        log.info("ActionLog.addMemmbersWithEmail.end memberId: {}, groupId: {}, emails: {}", getCurrentUserId(), groupId, emails);


    }

    @Override
    public void removeMember(Long groupId, Long userIdToRemove) {
        log.info("ActionLog.removeMember.start memberId: {}, groupId: {}, userIdToRemove: {}", getCurrentUserId(), groupId, userIdToRemove);

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        var member = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                () -> new NotFoundException("Member not found with id: " + getCurrentUserId())
        );

        var memberToRemove = groupMemberRepository.findByGroupAndUser_Id(group, userIdToRemove).orElseThrow(
                () -> new NotFoundException("Member not found with id: " + userIdToRemove)
        );

        var memberRole = member.getRole();

        if (member.getMuted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are muted in this group.");
        }

        if (!memberRole.equals(GroupRole.OWNER) && !memberRole.equals(GroupRole.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners and admins can remove members from this group.");
        }

        if (memberToRemove.getRole().equals(GroupRole.OWNER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't remove the owner of the group.");
        }

        memberToRemove.setRemovedById(getCurrentUserId());
        memberToRemove.setRole(GroupRole.REMOVED);
        groupMemberRepository.save(memberToRemove);
        group.getMembers().remove(memberToRemove);
        log.info("ActionLog.removeMember.end memberId: {}, groupId: {}, userIdToRemove: {}", getCurrentUserId(), groupId, userIdToRemove);


    }

    @Override
    public void sendMessage(Long groupId, ChatMessageRequest dto) {
        log.info("ActionLog.sendMessage.start memberId: {}, groupId: {}, message: {}", getCurrentUserId(), groupId, dto.getText());
        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        String encryptedMessage = groupMessageService.encryptGroupMessage(dto.getText(), group);

        var member = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                () -> new NotFoundException("Member not found with id: " + getCurrentUserId())
        );

        var memberMuted = member.getMuted();
        if (memberMuted) {
            throw new RuntimeException("You are muted in this group.You cant sent a message to this group");
        }

        if (member.getMuted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are muted in this group.");
        }

        var permission = group.getSendMessagePermissions();


        if (member.getRole().equals(GroupRole.LEAVED) ||
                member.getRole().equals(GroupRole.REMOVED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are leaved or removed from this group.");
        }

        if (permission.equals(GroupPermission.OWNERS_AND_ADMINS)) {
            if (!member.getRole().equals(GroupRole.OWNER) && !member.getRole().equals(GroupRole.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners and admins can send messages to this group.");
            }
        }


        GroupMessage message = new GroupMessage();
        message.setGroup(group);
        message.setSender(user);
        message.setType(dto.getType() != null ? dto.getType() : MessageType.TEXT);
        message.setText(encryptedMessage);
        message.setProductVariant(dto.getProductVariantId() != null
                ? productVariantRepository.findById(dto.getProductVariantId()).orElse(null)
                : null);

        groupMessageRepository.save(message);

        if (dto.getAttachments() != null) {
            for (AttachmentRequest attReq : dto.getAttachments()) {
                Attachment attachment = new Attachment();
                attachment.setMessage(message);
                attachment.setUrl(attReq.getUrl());
                attachment.setMimeType(attReq.getMimeType());
                attachment.setSize(attReq.getSize());
                attachmentRepository.save(attachment);
            }
        }

        GroupMessageIndex messageIndex = new GroupMessageIndex();
        messageIndex.setMessage(dto.getText());
        messageIndex.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        messageIndex.setSenderId(getCurrentUserId());
        messageIndex.setGroupId(groupId);
        groupMessageSearchRepository.save(messageIndex);

        List<GroupMember> members = groupMemberRepository.findByGroup(group);
        for (GroupMember m : members) {
            if (!m.getUser().getId().equals(user.getId())) {

                MessageReadStatus status = new MessageReadStatus();
                status.setMessage(message);
                status.setUser(m.getUser());
                status.setIsRead(false);
                messageReadStatusRepository.save(status);
            }
        }

        log.info("ActionLog.sendMessage.end memberId: {}, groupId: {}, message: {}", getCurrentUserId(), groupId, dto.getText());

    }


    @Override
    public void deleteMessage(Long groupId, Long messageId, String scope) {
        log.info("ActionLog.deleteMessage.start memberId: {}, groupId: {}, messageId: {}, scope: {}", getCurrentUserId(), groupId, messageId, scope);
        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        var message = groupMessageRepository.findByIdAndGroup(messageId, group).orElseThrow(
                () -> new NotFoundException("Message not found with id: " + messageId)
        );

        var member = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                () -> new NotFoundException("Member not found in this group with id: " + user.getId())
        );


        var memberRole = member.getRole();


        var starredMessage = starredGroupMessageRepository.findByUserAndMessage_Id(user, messageId);

        if ("me".equalsIgnoreCase(scope)) {
            var deletedMessage = deletedMessageRepository.existsByMemberAndMessage_Id(member, messageId);

            if (deletedMessage) {
                throw new RuntimeException("This message is already deleted by this member");
            }
            if (starredMessage != null) {
                starredGroupMessageRepository.delete(starredMessage);
            }

            var response = new DeletedMessage(member, message);
            deletedMessageRepository.save(response);
            log.info("Message {} hidden for member {}", messageId, user.getRole());
            return;
        }


        if ("all".equalsIgnoreCase(scope)) {
            if (memberRole.equals(GroupRole.ADMIN) || memberRole.equals(GroupRole.OWNER)) {
                message.setDeletedForAll(true);
                message.setDeletedBy(user.getId());
                groupMessageRepository.save(message);
                var starred = starredGroupMessageRepository.findByMessage_Id(messageId);
                if (starred != null) {
                    starredGroupMessageRepository.deleteAll(starred);
                }
                log.info("Message {} deleted for all by {}", messageId, user.getId());
            } else {
                throw new RuntimeException("Only admin or owner can delete message for all");
            }
        }


    }


    @Override
    public Page<ChatMessageResponse> getGroupMessages(Long groupId, int page, int size) {
        log.info("ActionLog.getGroupMessages.start memberId: {}, groupId: {}", getCurrentUserId(), groupId);

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        var member = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                () -> new NotFoundException("Member not found with id: " + getCurrentUserId())
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));


        var messages = groupMessageRepository.findVisibleMessages(group, member, pageable);


        Page<ChatMessageResponse> response = messages.map(msg -> {
            String decryptedText;
            try {
                decryptedText = groupMessageService.decryptGroupMessage(msg.getText(), group);
            } catch (Exception e) {
                decryptedText = "[Decryption failed]";
            }

            var resp = chatMessageMapper.toResponse(msg, decryptedText);
            resp.setAttachments(
                    msg.getAttachments().stream()
                            .map(att -> new AttachmentResponse(att.getId(), att.getUrl(), att.getMimeType(), att.getSize()))
                            .toList()
            );
            return resp;
        });

        var unreadMessages = messageReadStatusRepository
                .findByMessage_Group_IdAndUser_IdAndIsReadFalse(group.getId(), member.getUser().getId());
        unreadMessages.forEach(unread -> {
            unread.setIsRead(true);
            unread.setReadTimestamp(LocalDateTime.now());
        });
        messageReadStatusRepository.saveAll(unreadMessages);

        log.info("ActionLog.getGroupMessages.end memberId: {}, groupId: {}", getCurrentUserId(), groupId);

        return response;

    }


    @Override
    public void joinGroup(Long groupId) {
        log.info("ActionLog.joinGroup.start memberId: {}, groupId: {}", getCurrentUserId(), groupId);

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );
        var member = groupMemberRepository.findByGroupAndUser(group, user).orElse(null);

        if (member != null) {
            throw new AlreadyException("You are already a member of this group.");
        }

        member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupRole.MEMBER);
        member.setAddedById(getCurrentUserId());
        groupMemberRepository.save(member);

        var groupMembers = groupMemberRepository.findByGroup(group);
        groupMembers.forEach(m -> {
            notificationService.sendNotification(m.getUser(), "New member joined the group", NotificationType.NEW_MEMBER, group.getId());
        });
        auditLogService.createAuditLog(user, "Joined group", group.getTitle());

        log.info("ActionLog.joinGroup.end memberId: {}, groupId: {}", getCurrentUserId(), groupId);
    }


    @Override
    public String sendJoinRequest(Long groupId, MessageRequest message) {
        log.info("ActionLog.sendJoinRequest.start memberId: {}, groupId: {}", getCurrentUserId(), groupId);

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var group = chatGroupRepository.findByIdAndVisibility(groupId, GroupVisibility.PRIVATE).orElseThrow(
                () -> new NotFoundException("Private Group not found with id: " + groupId)
        );
        var member = groupMemberRepository.findByGroupAndUser(group, user).orElse(null);

        if (member != null) {
            throw new AlreadyException("You are already a member of this group.");
        }

        var joinRequest = new JoinRequest();
        joinRequest.setGroup(group);
        joinRequest.setRequester(user);
        joinRequest.setRequestedAt(LocalDateTime.now());
        joinRequest.setMessage(message.getMessage());
        joinRequestRepository.save(joinRequest);

        notificationService.sendNotification(group.getOwner(), "New join request", NotificationType.JOIN_REQUEST, group.getId());

        auditLogService.createAuditLog(user, "Sent join request", group.getTitle());

        log.info("ActionLog.sendJoinRequest.end memberId: {}, groupId: {}", getCurrentUserId(), groupId);

        return "Join request sent successfully. Wait for approval.";

    }


    @Override
    public List<JoinRequestResponse> getJoinRequests(Long groupId) {
        log.info("ActionLog.getJoinRequests.start memberId: {}, groupId: {}", getCurrentUserId(), groupId);
        var owner = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));

        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        if (!group.getOwner().equals(owner)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this group.");
        }

        var joinRequests = joinRequestRepository.findByGroup(group);

        if (joinRequests.isEmpty()) {
            throw new NotFoundException("No join requests found for this group.");
        }

        var responseList = joinRequestMapper.toResponseList(joinRequests);

        log.info("ActionLog.getJoinRequests.end memberId: {}, groupId: {}", getCurrentUserId(), groupId);

        return responseList;

    }

    @Transactional
    @Override
    public void approveJoinRequest(Long groupId, Long requestId) {
        log.info("ActionLog.approveJoinRequest.start memberId: {}, requestId: {}", getCurrentUserId(), requestId);

        var owner = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var request = joinRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Join request not found with id: " + requestId));
        var group = request.getGroup();
        var groupReal = chatGroupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id: " + groupId));

        if (group.equals(groupReal)) {
            throw new RuntimeException("Request isnt related with id: " + groupId);
        }

        if (!group.getOwner().equals(owner)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this group.");
        }

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new RuntimeException("You cant approve this request, cause it is not pending.");
        }


        request.setStatus(RequestStatus.APPROVED);
        joinRequestRepository.save(request);

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(request.getRequester());
        member.setRole(GroupRole.MEMBER);
        member.setAddedById(getCurrentUserId());
        member.setJoinedAt(LocalDateTime.now());
        groupMemberRepository.save(member);

        var memebers = groupMemberRepository.findByGroup(group);
        memebers.forEach(m -> {
            if (!m.getUser().equals(request.getRequester())) {
                notificationService.sendNotification(m.getUser(), "New member joined the group: " + group.getTitle(), NotificationType.NEW_MEMBER, group.getId());
            }
        });


        auditLogService.createAuditLog(owner, "Approved join request", group.getTitle());
        notificationService.sendNotification(request.getRequester(), "Join request approved, group:" + group.getTitle() + "", NotificationType.JOIN_REQUEST_APPROVED, group.getId());

        log.info("ActionLog.approveJoinRequest.end memberId: {}, requestId: {}", getCurrentUserId(), requestId);
    }


    @Override
    public void rejectJoinRequest(Long groupId, Long requestId) {
        log.info("ActionLog.rejectJoinRequest.start memberId: {}, requestId: {}", getCurrentUserId(), requestId);
        var owner = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var request = joinRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Join request not found with id: " + requestId));
        var group = request.getGroup();
        var gorupReal = chatGroupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id: " + groupId));

        if (group.equals(gorupReal)) {
            throw new RuntimeException("Request isnt related with id: " + groupId);
        }

        if (!group.getOwner().equals(owner)) {
            throw new RuntimeException("You are not the owner of this group.");
        }

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new RuntimeException("You cant reject this request, cause it is not pending.");
        }


        request.setStatus(RequestStatus.REJECTED);
        joinRequestRepository.save(request);

        auditLogService.createAuditLog(owner, "Rejected join request", group.getTitle());
        notificationService.sendNotification(request.getRequester(), "Your join request rejected , group:" + group.getTitle(), NotificationType.JOIN_REQUEST_REJECTED, group.getId());
        log.info("ActionLog.rejectJoinRequest.end memberId: {}, requestId: {}", getCurrentUserId(), requestId);

    }


    @Override
    public void deleteJoinRequest(Long groupId, Long requestId) {
        log.info("ActionLog.deleteJoinRequest.start memberId: {}, requestId: {}", getCurrentUserId(), requestId);
        var request = joinRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Join request not found with id: " + requestId));

        var group = request.getGroup();
        var gorupReal = chatGroupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id: " + groupId));


        if (!request.getRequester().getId().equals(getCurrentUserId())) {
            throw new RuntimeException("You are not the owner of this request.");
        }

        if (!group.getId().equals(gorupReal.getId())) {
            throw new RuntimeException("Request isnt related with id: " + groupId);
        }

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new RuntimeException("You cant delete this request, cause it is not pending.");
        }

        joinRequestRepository.delete(request);

        auditLogService.createAuditLog(request.getRequester(), "Deleted join request", request.getGroup().getTitle());

        log.info("ActionLog.deleteJoinRequest.end memberId: {}, requestId: {}", getCurrentUserId(), requestId);
    }


    @Override
    public void setAdmin(Long groupId, Long newAdminId) {
        log.info("ActionLog.setAdmin.start memberId: {}, groupId: {}, newAdminId: {}", getCurrentUserId(), groupId, newAdminId);

        var userId = getCurrentUserId();
        var group = chatGroupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id: " + groupId));
        var admin = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId));

        var member = groupMemberRepository.findByGroupAndUser_Id(group, newAdminId).orElseThrow(
                () -> new NotFoundException("Member for being Admin not found with id: " + newAdminId)
        );

        var adminMember = groupMemberRepository.findByGroupAndUser(group, admin).orElseThrow(
                () -> new NotFoundException("Member for being Admin not found with id: " + getCurrentUserId())
        );

        var memberRole = member.getRole();
        if (memberRole.equals(GroupRole.LEAVED) || memberRole.equals(GroupRole.REMOVED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are leaved or removed from this group.");

        }

        if (!adminMember.getRole().equals(GroupRole.OWNER)
                && !adminMember.getRole().equals(GroupRole.ADMIN)) {
            {
                throw new RuntimeException("Only the owner or an admin can set another admin.");
            }
        }

        if (member.getRole().equals(GroupRole.OWNER)
                || member.getRole().equals(GroupRole.ADMIN)) {
            throw new RuntimeException("The member is already an admin.");
        }

        member.setRole(GroupRole.ADMIN);
        groupMemberRepository.save(member);

        auditLogService.createAuditLog(admin, "Set admin", group.getTitle());
        notificationService.sendNotification(member.getUser(), "You are now an admin in group:" + group.getTitle() + "", NotificationType.SET_ADMIN, group.getId());

        log.info("ActionLog.setAdmin.end memberId: {}, groupId: {}, newAdminId: {}", getCurrentUserId(), groupId, newAdminId);

    }


    @Override
    public void leaveGroup(Long groupId) {
        log.info("ActionLog.leaveGroup.start memberId: {}, groupId: {}", getCurrentUserId(), groupId);
        var userId = getCurrentUserId();
        var group = chatGroupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id: " + groupId));
        var member = groupMemberRepository.findByGroupAndUser_Id(group, userId).orElseThrow(
                () -> new NotFoundException("Member not found with id: " + userId)
        );

        var memberRole = member.getRole();

        member.setRole(GroupRole.LEAVED);
        groupMemberRepository.save(member);

        var existingAdmins = groupMemberRepository.findByGroupAndRole(group, GroupRole.ADMIN);
        var existingOwners = groupMemberRepository.findByGroupAndRole(group, GroupRole.OWNER);

        var lastMember = groupMemberRepository.findTopByGroupAndRoleOrderByJoinedAtDesc(group, GroupRole.MEMBER);

        if (memberRole.equals(GroupRole.OWNER)) {
            if (existingAdmins.isEmpty()) {
                lastMember.ifPresent(m -> {
                    m.setRole(GroupRole.ADMIN);
                    groupMemberRepository.save(m);
                });
            }
        } else if (memberRole.equals(GroupRole.ADMIN)) {
            if (existingOwners.isEmpty() && existingAdmins.isEmpty()) {

                lastMember.ifPresent(m -> {
                    m.setRole(GroupRole.ADMIN);
                    groupMemberRepository.save(m);
                });
            }
        }

        var groupMembers = groupMemberRepository.findByGroup(group);

        if (!groupMembers.isEmpty()) {
            groupMembers.forEach(m -> {
                notificationService.sendNotification(m.getUser(), "Member left the group, user :" + member.getUser().getName(), NotificationType.LEAVE_GROUP, group.getId());
            });
        }
        auditLogService.createAuditLog(member.getUser(), "Left group", group.getTitle());

        log.info("ActionLog.leaveGroup.end memberId: {}, groupId: {}", getCurrentUserId(), groupId);

    }

    @Override
    public void pinMessage(Long messageId, Long groupId) {
        log.info("ActionLog.pinMessage.start memberId: {}, messageId: {}, groupId: {}", getCurrentUserId(), messageId, groupId);

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );
        var message = groupMessageRepository.findByIdAndGroup(messageId, group).orElseThrow(
                () -> new NotFoundException("Message not found with id: " + messageId)
        );

        var member = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                () -> new NotFoundException("Member not found in this group with id: " + user.getId())
        );

        var lastPinnedMessage = groupMessageRepository.findByPinnedAndGroup(true, group).orElse(null);

        var memberRole = member.getRole();

        var permission = group.getSettingPermissions();

        if (memberRole.equals(GroupRole.LEAVED) || memberRole.equals(GroupRole.REMOVED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are leaved or removed from this group.");

        }

        if (!permission.equals(GroupPermission.ALL_MEMBERS)
                && memberRole.equals(GroupRole.MEMBER)
        ) {
            throw new RuntimeException("You dont have permission to pin messages");

        }

        if (lastPinnedMessage != null) {
            lastPinnedMessage.setPinned(false);
            groupMessageRepository.save(lastPinnedMessage);
        }

        message.setPinned(true);
        groupMessageRepository.save(message);

        var groupMember = groupMemberRepository.findByGroupAndUser(group, user);
        groupMember.ifPresent(m -> {
            notificationService.sendNotification(m.getUser(), "Message pinned", NotificationType.PIN_MESSAGE, group.getId());
        });
        auditLogService.createAuditLog(user, "Pinned message", group.getTitle());


        log.info("ActionLog.pinMessage.end memberId: {}, messageId: {}, groupId: {}", getCurrentUserId(), messageId, groupId);

    }

    @Override
    public void unpinMessage(Long messageId, Long groupId) {
        log.info("ActionLog.unpinMessage.start memberId: {}, messageId: {}, groupId: {}", getCurrentUserId(), messageId, groupId);

        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );
        var message = groupMessageRepository.findByPinnedAndGroup(true, group).orElseThrow(
                () -> new NotFoundException("Message not found with id: " + messageId)
        );

        var member = groupMemberRepository.findByGroupAndUser_Id(group, getCurrentUserId()).orElseThrow(
                () -> new NotFoundException("Member not found in this group with id: " + getCurrentUserId())
        );

        var memberRole = member.getRole();

        var permission = group.getSettingPermissions();

        var lastPinnedMessage = groupMessageRepository.findByPinnedAndGroup(true, group).orElse(null);

        if (memberRole.equals(GroupRole.LEAVED) || memberRole.equals(GroupRole.REMOVED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are leaved or removed from this group.");

        }

        if (!permission.equals(GroupPermission.ALL_MEMBERS)
                && memberRole.equals(GroupRole.MEMBER)
        ) {
            throw new RuntimeException("You dont have permission to unpin messages");

        }

        if (lastPinnedMessage == null) {
            throw new RuntimeException("There is no pinned message");
        }
        message.setPinned(false);
        groupMessageRepository.save(message);

        auditLogService.createAuditLog(member.getUser(), "Unpinned message", group.getTitle());
        log.info("ActionLog.unpinMessage.end memberId: {}, messageId: {}, groupId: {}", getCurrentUserId(), messageId, groupId);

    }

    @Override
    public List<ChatMessageResponse> searchMessages(Long groupId, String keyword, int page, int size) {
        log.info("ActionLog.searchMessages.start memberId: {}, groupId: {}, keyword: {}, page: {}, size: {}", getCurrentUserId(), groupId, keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        var member = groupMemberRepository.findByGroupAndUser_Id(group, getCurrentUserId()).orElseThrow(
                () -> new NotFoundException("Member not found in this group with id: " + getCurrentUserId())
        );

        Page<GroupMessageIndex> found = groupMessageSearchRepository
                .searchMessagesByKeywordAndGroup(keyword.toLowerCase().replaceAll("\\s+", " "), groupId, pageable);


        if (found.isEmpty()) {
            throw new NotFoundException("No messages found for keyword: " + keyword);
        }

        var response = chatMessageMapper.toResponseList(found.getContent());

        auditLogService.createAuditLog(member.getUser(), "Searched messages", group.getTitle());
        log.info("ActionLog.searchMessages.end memberId: {}, groupId: {}, keyword: {}, page: {}, size: {}", getCurrentUserId(), groupId, keyword, page, size);

        return response;


    }

    @Override
    public List<MessageReadStatusResponse> getMessageReadStatus(Long messageId, Long groupId) {
        log.info("ActionLog.getMessageReadStatus.start memberId: {}, messageId: {}, groupId: {}", getCurrentUserId(), messageId, groupId);

        var group = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        var member = groupMemberRepository.findByGroupAndUser_Id(group, getCurrentUserId()).orElseThrow(
                () -> new NotFoundException("Member not found in this group with id: " + getCurrentUserId())
        );

        var message = groupMessageRepository.findByIdAndGroup(messageId, group).orElseThrow(
                () -> new NotFoundException("Message not found with id: " + messageId)
        );

        var readStatus = messageReadStatusRepository.findByMessage_IdWithUser(messageId);

        if (!message.getSender().getId().equals(getCurrentUserId())) {
            throw new RuntimeException("You can  read only your own message");

        }

        var response = messageReadStatusMapper.toResponseList(readStatus);
        log.info("ActionLog.getMessageReadStatus.end memberId: {}, messageId: {}, groupId: {}", getCurrentUserId(), messageId, groupId);
        return response;

    }

    @Override
    public List<ChatGroupResponse> searchGroups(String keyword, int page, int size) {
        log.info("ActionLog.searchGroups.start memberId: {}, keyword: {}, page: {}, size: {}", getCurrentUserId(), keyword, page, size);
        Pageable pageable = PageRequest.of(page, size);

        var found = chatGroupRepository.searchGroupsByKeyword(keyword.toLowerCase().replaceAll("\\s+", " "), pageable);

        if (found.isEmpty()) {
            throw new NotFoundException("No groups found for keyword: " + keyword);
        }

        var response = chatGroupMapper.toResponseList(found.getContent());

        log.info("ActionLog.searchGroups.end memberId: {}, keyword: {}, page: {}, size: {}", getCurrentUserId(), keyword, page, size);

        return response;
    }



    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}
