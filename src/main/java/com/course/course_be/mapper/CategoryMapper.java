package com.course.course_be.mapper;

import com.course.course_be.dto.response.category.CategoryResponse;
import com.course.course_be.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
     CategoryResponse toCategoryResponse(Category category);

     List<CategoryResponse> toCategoryResponseList(List<Category> categoryList);
}
