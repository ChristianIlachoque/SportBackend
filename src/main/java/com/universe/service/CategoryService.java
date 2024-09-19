package com.universe.service;

import com.universe.controller.dto.CategoryRequest;
import com.universe.controller.dto.CategoryResponse;
import com.universe.persistence.entity.CategoryEntity;
import com.universe.persistence.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    public List<CategoryResponse> getAll(){
        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        categoryRepository.findAll().forEach(categoryEntity -> categoryResponseList.add(
                new CategoryResponse(
                        categoryEntity.getId(),
                        categoryEntity.getName()
                )
        ));
        return categoryResponseList;
    }

    public CategoryResponse getById(UUID id){
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElse(null);
        if(categoryEntity != null)
            return new CategoryResponse(categoryEntity.getId(), categoryEntity.getName());
        return null;
    }

    public CategoryResponse create(CategoryRequest categoryRequest){
        CategoryEntity itemCategory = categoryRepository.save(
                CategoryEntity.builder()
                        .name(categoryRequest.name())
                .build()
        );
        return new CategoryResponse(itemCategory.getId(), itemCategory.getName());
    }
    public CategoryResponse update(UUID id, CategoryRequest categoryRequest){
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElse(null);
        if(categoryEntity != null) {
            categoryEntity.setName(categoryRequest.name());
        }
        CategoryEntity updatedCategoryEntity = categoryRepository.save(categoryEntity);
        return new CategoryResponse(updatedCategoryEntity.getId(), updatedCategoryEntity.getName());
    }
    public boolean delete(UUID id){
        try {
            categoryRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
