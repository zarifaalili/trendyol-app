package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.GroupMessage;
import org.example.trendyolfinalproject.dao.entity.MessageReadStatus;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.response.MessageReadStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageReadStatusMapperTest {

    private MessageReadStatusMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MessageReadStatusMapper.class);
    }

    @Test
    void testToResponse() {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Alice");

        GroupMessage message = new GroupMessage();
        message.setId(10L);

        MessageReadStatus status = new MessageReadStatus();
        status.setId(100L);
        status.setUser(user);
        status.setMessage(message);
        status.setIsRead(true);
        status.setReadTimestamp(LocalDateTime.now());

        // when
        MessageReadStatusResponse response = mapper.toResponse(status);

        // then
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(10L, response.getMessageId());
        assertEquals(1L, response.getUserId());
        assertEquals("Alice", response.getUsername());
        assertTrue(response.getIsRead());
        assertNotNull(response.getReadTimestamp());
    }

    @Test
    void testToResponseList() {
        // given
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Alice");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Bob");

        GroupMessage msg1 = new GroupMessage();
        msg1.setId(10L);
        GroupMessage msg2 = new GroupMessage();
        msg2.setId(20L);

        MessageReadStatus status1 = new MessageReadStatus();
        status1.setId(100L);
        status1.setUser(user1);
        status1.setMessage(msg1);
        status1.setIsRead(true);
        status1.setReadTimestamp(LocalDateTime.now());

        MessageReadStatus status2 = new MessageReadStatus();
        status2.setId(101L);
        status2.setUser(user2);
        status2.setMessage(msg2);
        status2.setIsRead(false);
        status2.setReadTimestamp(null);

        List<MessageReadStatus> statuses = List.of(status1, status2);

        // when
        List<MessageReadStatusResponse> responses = mapper.toResponseList(statuses);

        // then
        assertEquals(2, responses.size());

        assertEquals(100L, responses.get(0).getId());
        assertEquals("Alice", responses.get(0).getUsername());
        assertTrue(responses.get(0).getIsRead());

        assertEquals(101L, responses.get(1).getId());
        assertEquals("Bob", responses.get(1).getUsername());
        assertFalse(responses.get(1).getIsRead());
        assertNull(responses.get(1).getReadTimestamp());
    }
}
