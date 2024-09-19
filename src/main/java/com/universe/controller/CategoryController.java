package com.universe.controller;

import com.universe.controller.dto.CategoryRequest;
import com.universe.controller.dto.CategoryResponse;
import com.universe.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/category")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(categoryService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        CategoryResponse categoryResponse = categoryService.getById(id);
        if (categoryResponse != null) return ResponseEntity.ok(categoryResponse);
        return ResponseEntity.notFound().build();
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CategoryRequest categoryRequest){
        return ResponseEntity.ok(categoryService.create(categoryRequest));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody @Valid CategoryRequest categoryRequest){
        return ResponseEntity.ok(categoryService.update(id, categoryRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        boolean wasDeleted = categoryService.delete(id);
        if (wasDeleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
