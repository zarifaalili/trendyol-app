package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Category;
import org.example.trendyolfinalproject.model.request.CategoryCreateRequest;
import org.example.trendyolfinalproject.model.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryCreateRequest request);


    @Mapping(source = "parentCategory.id", target = "parentCategoryId")
    @Mapping(source = "parentCategory.name", target = "parentCategoryName")
    @Mapping(target = "subCategories", source = "subCategories", qualifiedByName = "mapSubCategoriesToResponseList")
    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);


    @Named("mapSubCategoriesToResponseList")
    default List<CategoryResponse> mapSubCategoriesToResponseList(Set<Category> subCategories) {
        if (subCategories == null) {
            return null;
        }
        return subCategories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
