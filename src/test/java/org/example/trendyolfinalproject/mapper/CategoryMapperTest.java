package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Category;
import org.example.trendyolfinalproject.model.request.CategoryCreateRequest;
import org.example.trendyolfinalproject.model.response.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = Mappers.getMapper(CategoryMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest(
                "Electronics",
                "All electronic devices",
                1L
        );

        // when
        Category entity = categoryMapper.toEntity(request);

        // then
        assertNotNull(entity);
        assertEquals("Electronics", entity.getName());
        assertEquals("All electronic devices", entity.getDescription());
        assertNull(entity.getParentCategory()); // ignore olunmuşdu
        assertTrue(entity.getSubCategories().isEmpty()); // ignore olunmuşdu
    }

    @Test
    void testToResponse() {
        // given
        Category parent = new Category();
        parent.setId(10L);
        parent.setName("ParentCategory");
        parent.setDescription("Parent Desc");

        Category sub1 = new Category();
        sub1.setId(2L);
        sub1.setName("Sub1");
        sub1.setDescription("SubCategory 1");
        sub1.setParentCategory(parent);

        Category sub2 = new Category();
        sub2.setId(3L);
        sub2.setName("Sub2");
        sub2.setDescription("SubCategory 2");
        sub2.setParentCategory(parent);

        Set<Category> subs = new HashSet<>();
        subs.add(sub1);
        subs.add(sub2);

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("All electronic devices");
        category.setParentCategory(parent);
        category.setSubCategories(subs);

        // when
        CategoryResponse response = categoryMapper.toResponse(category);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Electronics", response.getName());
        assertEquals("All electronic devices", response.getDescription());
        assertEquals(10L, response.getParentCategoryId());
        assertEquals("ParentCategory", response.getParentCategoryName());

        assertNotNull(response.getSubCategories());
        assertEquals(2, response.getSubCategories().size());

        List<String> subNames = response.getSubCategories()
                .stream()
                .map(CategoryResponse::getName)
                .toList();
        assertTrue(subNames.contains("Sub1"));
        assertTrue(subNames.contains("Sub2"));
    }

    @Test
    void testToResponseList() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Books");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Clothes");

        List<CategoryResponse> responseList = categoryMapper.toResponseList(List.of(cat1, cat2));

        assertEquals(2, responseList.size());
        assertEquals("Books", responseList.get(0).getName());
        assertEquals("Clothes", responseList.get(1).getName());
    }
}
