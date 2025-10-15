package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.request.ChatGroupRequest;
import org.example.trendyolfinalproject.model.response.ChatGroupResponse;
import org.example.trendyolfinalproject.model.enums.GroupVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatGroupMapperTest {

    private ChatGroupMapper chatGroupMapper;

    @BeforeEach
    void setUp() {
        chatGroupMapper = Mappers.getMapper(ChatGroupMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        ChatGroupRequest request = new ChatGroupRequest(
                "Study Group",
                "Group for project discussion",
                GroupVisibility.PRIVATE,
                List.of(1L, 2L, 3L)
        );

        // when
        ChatGroup entity = chatGroupMapper.toEntity(request);

        // then
        assertNotNull(entity);
        assertEquals("Study Group", entity.getTitle());
        assertEquals("Group for project discussion", entity.getDescription());
        assertEquals(GroupVisibility.PRIVATE, entity.getVisibility());

        // owner və members MapStruct tərəfindən null olacaq, çünki mapper-de map edilməyib
        assertNull(entity.getOwner());
        assertTrue(entity.getMembers().isEmpty());
    }

    @Test
    void testToResponse() {
        // given
        User owner = new User();
        owner.setId(100L);

        ChatGroup group = new ChatGroup();
        group.setId(1L);
        group.setTitle("Study Group");
        group.setDescription("Group for project discussion");
        group.setVisibility(GroupVisibility.PRIVATE);
        group.setOwner(owner);

        // when
        ChatGroupResponse response = chatGroupMapper.toResponse(group);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Study Group", response.getTitle());
        assertEquals("Group for project discussion", response.getDescription());
        assertEquals(GroupVisibility.PRIVATE, response.getVisibility());
        assertEquals(100L, response.getOwnerId());
        assertNotNull(response.getCreatedAt()); // MapStruct default olaraq null verəcək əgər entity-də set olunmayıbsa
    }

    @Test
    void testToResponseList() {
        // given
        ChatGroup group1 = new ChatGroup();
        group1.setId(1L);
        group1.setTitle("Group 1");

        ChatGroup group2 = new ChatGroup();
        group2.setId(2L);
        group2.setTitle("Group 2");

        // when
        List<ChatGroupResponse> responses = chatGroupMapper.toResponseList(List.of(group1, group2));

        // then
        assertEquals(2, responses.size());
        assertEquals("Group 1", responses.get(0).getTitle());
        assertEquals("Group 2", responses.get(1).getTitle());
    }
}
