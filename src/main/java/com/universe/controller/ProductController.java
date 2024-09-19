package com.universe.controller;

import com.universe.controller.dto.ProductRequest;
import com.universe.controller.dto.ProductResponse;
import com.universe.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/product")
@PreAuthorize("hasRole('ADMIN')")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        ProductResponse productResponse = productService.getById(id);
        if (productResponse != null) return ResponseEntity.ok(productResponse);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ProductRequest productRequest){
        return ResponseEntity.ok(productService.create(productRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody @Valid ProductRequest productRequest){
        return ResponseEntity.ok(productService.update(id, productRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        boolean wasDeleted = productService.delete(id);
        if (wasDeleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/uploadImage/{id}")
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile image,
            @PathVariable UUID id
    ) throws IOException {
        return ResponseEntity.ok(productService.uploadImage(id, image));
    }

    @PatchMapping("/uploadImageAWS/{id}")
    public ResponseEntity<?> uploadImageAWS(
            @RequestParam("image") MultipartFile image,
            @PathVariable UUID id
    ) throws IOException {
        return ResponseEntity.ok(productService.uploadImageAWS(id, image));
    }
}
