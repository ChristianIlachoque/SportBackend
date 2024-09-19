package com.universe.controller;

import com.universe.controller.dto.SaleRequest;
import com.universe.controller.dto.SaleResponse;
import com.universe.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sale")
@PreAuthorize("hasRole('ADMIN')")
public class SaleController {
    @Autowired
    SaleService saleService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(saleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        SaleResponse saleResponse = saleService.getById(id);
        if(saleResponse != null) return ResponseEntity.ok(saleResponse);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid SaleRequest saleRequest){
        return ResponseEntity.ok(saleService.create(saleRequest));
    }
}
