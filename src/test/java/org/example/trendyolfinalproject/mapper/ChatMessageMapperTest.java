package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.GroupMessage;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.request.ChatMessageRequest;
import org.example.trendyolfinalproject.model.response.ChatMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageMapperTest {

    private ChatMessageMapper chatMessageMapper;

    @BeforeEach
    void setUp() {
        chatMessageMapper = Mappers.getMapper(ChatMessageMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        ChatMessageRequest request = new ChatMessageRequest();
        request.setType(org.example.trendyolfinalproject.model.enums.MessageType.TEXT);
        request.setText("Hello World");

        User sender = new User();
        sender.setId(1L);

        ChatGroup group = new ChatGroup();
        group.setId(2L);

        ProductVariant variant = new ProductVariant();
        variant.setId(3L);

        // when
        GroupMessage entity = chatMessageMapper.toEntity(request, sender, group, variant);

        // then
        assertNotNull(entity);
        assertEquals("Hello World", entity.getText());
        assertEquals(sender, entity.getSender());
        assertEquals(group, entity.getGroup());
        assertEquals(variant, entity.getProductVariant());
        assertEquals(org.example.trendyolfinalproject.model.enums.MessageType.TEXT, entity.getType());
        assertNull(entity.getId()); // ignore=true
    }

    @Test
    void testToResponse() {
        // given
        User sender = new User();
        sender.setId(1L);

        ChatGroup group = new ChatGroup();
        group.setId(2L);

        ProductVariant variant = new ProductVariant();
        variant.setId(3L);

        GroupMessage message = GroupMessage.builder()
                .id(10L)
                .sender(sender)
                .group(group)
                .productVariant(variant)
                .text("Encrypted Text")
                .deletedForAll(false)
                .edited(true)
                .build();

        String decryptedMessage = "Hello World";

        // when
        ChatMessageResponse response = chatMessageMapper.toResponse(message, decryptedMessage);

        // then
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getSenderId());
        assertEquals(2L, response.getGroupId());
        assertEquals(3L, response.getProductVariantId());
        assertEquals("Hello World", response.getText());
        assertTrue(response.getEdited());
        assertFalse(response.getDeletedForAll());
    }

    @Test
    void testToResponseList() {
        // given
        org.example.trendyolfinalproject.dao.entity.GroupMessageIndex index1 =
                new org.example.trendyolfinalproject.dao.entity.GroupMessageIndex();
        index1.setId("100");
        index1.setSenderId(1L);
        index1.setGroupId(2L);
        index1.setMessage("Test 1");
        index1.setTimestamp(Instant.now());

        org.example.trendyolfinalproject.dao.entity.GroupMessageIndex index2 =
                new org.example.trendyolfinalproject.dao.entity.GroupMessageIndex();
        index2.setId("101");
        index2.setSenderId(2L);
        index2.setGroupId(3L);
        index2.setMessage("Test 2");
        index2.setTimestamp(Instant.now());

        // when
        List<ChatMessageResponse> responses =
                chatMessageMapper.toResponseList(List.of(index1, index2));

        // then
        assertEquals(2, responses.size());
        assertEquals(100L, responses.get(0).getId());
        assertEquals("Test 1", responses.get(0).getText());
        assertEquals(1L, responses.get(0).getSenderId());

        assertEquals(101L, responses.get(1).getId());
        assertEquals("Test 2", responses.get(1).getText());
        assertEquals(2L, responses.get(1).getSenderId());
    }
}
