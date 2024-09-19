package com.universe.controller;

import com.universe.controller.dto.PurchaseRequest;
import com.universe.controller.dto.PurchaseResponse;
import com.universe.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/purchase")
@PreAuthorize("hasRole('ADMIN')")
public class PurchaseController {
    @Autowired
    PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(purchaseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        PurchaseResponse purchaseResponse = purchaseService.getById(id);
        if(purchaseResponse != null) return ResponseEntity.ok(purchaseResponse);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid PurchaseRequest purchaseRequest){
        return ResponseEntity.ok(purchaseService.create(purchaseRequest));
    }
}
