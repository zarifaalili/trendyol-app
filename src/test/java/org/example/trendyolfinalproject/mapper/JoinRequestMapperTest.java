package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.JoinRequest;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.enums.RequestStatus;
import org.example.trendyolfinalproject.model.response.JoinRequestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JoinRequestMapperTest {

    private JoinRequestMapper joinRequestMapper;

    @BeforeEach
    void setUp() {
        joinRequestMapper = Mappers.getMapper(JoinRequestMapper.class);
    }

    @Test
    void testToResponse() {
        // given
        ChatGroup group = new ChatGroup();
        group.setId(10L);
        group.setTitle("Test Group");

        User requester = new User();
        requester.setId(5L);
        requester.setEmail("test@example.com");

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setId(1L);
        joinRequest.setGroup(group);
        joinRequest.setRequester(requester);
        joinRequest.setMessage("Please let me join");
        joinRequest.setRequestedAt(LocalDateTime.now());
        joinRequest.setStatus(RequestStatus.PENDING);

        // when
        JoinRequestResponse response = joinRequestMapper.toResponse(joinRequest);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getGroupId());
        assertEquals("Test Group", response.getGroupName());
        assertEquals(5L, response.getRequesterId());
        assertEquals("test@example.com", response.getRequesterEmail());
        assertEquals("Please let me join", response.getMessage());
        assertEquals(RequestStatus.PENDING, response.getStatus());
        assertNotNull(response.getRequestedAt());
    }

    @Test
    void testToResponseList() {
        // given
        ChatGroup group1 = new ChatGroup();
        group1.setId(10L);
        group1.setTitle("Group 1");

        User requester1 = new User();
        requester1.setId(5L);
        requester1.setEmail("user1@example.com");

        JoinRequest jr1 = new JoinRequest();
        jr1.setId(1L);
        jr1.setGroup(group1);
        jr1.setRequester(requester1);
        jr1.setMessage("Join request 1");
        jr1.setRequestedAt(LocalDateTime.now());
        jr1.setStatus(RequestStatus.PENDING);

        ChatGroup group2 = new ChatGroup();
        group2.setId(20L);
        group2.setTitle("Group 2");

        User requester2 = new User();
        requester2.setId(6L);
        requester2.setEmail("user2@example.com");

        JoinRequest jr2 = new JoinRequest();
        jr2.setId(2L);
        jr2.setGroup(group2);
        jr2.setRequester(requester2);
        jr2.setMessage("Join request 2");
        jr2.setRequestedAt(LocalDateTime.now());
        jr2.setStatus(RequestStatus.APPROVED);

        List<JoinRequest> joinRequests = List.of(jr1, jr2);

        // when
        List<JoinRequestResponse> responses = joinRequestMapper.toResponseList(joinRequests);

        // then
        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getId());
        assertEquals("Group 1", responses.get(0).getGroupName());
        assertEquals("user1@example.com", responses.get(0).getRequesterEmail());

        assertEquals(2L, responses.get(1).getId());
        assertEquals("Group 2", responses.get(1).getGroupName());
        assertEquals("user2@example.com", responses.get(1).getRequesterEmail());
    }
}
