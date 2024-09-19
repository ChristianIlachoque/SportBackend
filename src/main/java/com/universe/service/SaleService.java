package com.universe.service;

import com.universe.controller.dto.*;
import com.universe.persistence.entity.InventoryEntity;
import com.universe.persistence.entity.ProductEntity;
import com.universe.persistence.entity.SaleDetailEntity;
import com.universe.persistence.entity.SaleEntity;
import com.universe.persistence.repository.InventoryRepository;
import com.universe.persistence.repository.ProductRepository;
import com.universe.persistence.repository.SaleDetailRepository;
import com.universe.persistence.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SaleService {
    @Autowired
    SaleRepository saleRepository;

    @Autowired
    SaleDetailRepository saleDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    public List<SaleResponse> getAll(){
        List<SaleResponse> saleResponseList = new ArrayList<>();
        saleRepository.findAll().forEach(saleEntity -> {
            List<SaleDetailResponse> saleDetailResponseList = new ArrayList<>();
            saleEntity.getSaleDetailList().forEach(saleDetailEntity -> saleDetailResponseList.add(
                    new SaleDetailResponse(
                            saleDetailEntity.getId(),
                            new ProductResponse(
                                    saleDetailEntity.getProduct().getId(),
                                    saleDetailEntity.getProduct().getName(),
                                    saleDetailEntity.getProduct().getDescription(),
                                    saleDetailEntity.getProduct().getPrice(),
                                    saleDetailEntity.getProduct().getImage(),
                                    new CategoryResponse(
                                            saleDetailEntity.getProduct().getCategory().getId(),
                                            saleDetailEntity.getProduct().getCategory().getName()
                                    )
                            ),
                            saleDetailEntity.getQuantity(),
                            saleDetailEntity.getUnitPrice(),
                            saleDetailEntity.getSubtotal()
                    )
            ));

            saleResponseList.add(
                    new SaleResponse(
                            saleEntity.getId(),
                            saleEntity.getDate(),
                            saleEntity.getTotalPrice(),
                            saleDetailResponseList
                    )
            );
        });

        return saleResponseList;
    }

    public SaleResponse getById(UUID id){
        SaleEntity saleEntity = saleRepository.findById(id).orElse(null);
        if(saleEntity != null){
            List<SaleDetailResponse> saleDetailResponseList = new ArrayList<>();
            saleEntity.getSaleDetailList().forEach(saleDetailEntity -> saleDetailResponseList.add(
                    new SaleDetailResponse(
                            saleDetailEntity.getId(),
                            new ProductResponse(
                                    saleDetailEntity.getProduct().getId(),
                                    saleDetailEntity.getProduct().getName(),
                                    saleDetailEntity.getProduct().getDescription(),
                                    saleDetailEntity.getProduct().getPrice(),
                                    saleDetailEntity.getProduct().getImage(),
                                    new CategoryResponse(
                                            saleDetailEntity.getProduct().getCategory().getId(),
                                            saleDetailEntity.getProduct().getCategory().getName()
                                    )
                            ),
                            saleDetailEntity.getQuantity(),
                            saleDetailEntity.getUnitPrice(),
                            saleDetailEntity.getSubtotal()
                    )
            ));
            return new SaleResponse(
                    saleEntity.getId(),
                    saleEntity.getDate(),
                    saleEntity.getTotalPrice(),
                    saleDetailResponseList
            );
        }
        return null;
    }

    public SaleResponse create(SaleRequest saleRequest){
        if(saleRequest.saleDetailList() == null || saleRequest.saleDetailList().isEmpty()){
            throw  new IllegalArgumentException("Sale must to have at least one SaleDetail");
        }

        double totalPrice = saleRequest.saleDetailList().stream().mapToDouble(saleDetailItem -> saleDetailItem.quantity() * saleDetailItem.unitPrice()).sum();
        SaleEntity saleEntity = saleRepository.save(
                SaleEntity.builder()
                        .date(saleRequest.date())
                        .totalPrice(totalPrice)
                        .build()
        );

        // Create SaleDetail List
        List<SaleDetailEntity> saleDetailEntityList = new ArrayList<>();
        List<SaleDetailResponse> saleDetailResponseList = new ArrayList<>();
        saleRequest.saleDetailList().forEach(saleDetailRequest -> {
            ProductEntity productEntity = productRepository.findById(saleDetailRequest.product()).orElse(null);
            if (productEntity != null){
                SaleDetailEntity saleDetailEntity = saleDetailRepository.save(
                        SaleDetailEntity.builder()
                                .quantity(saleDetailRequest.quantity())
                                .unitPrice(saleDetailRequest.unitPrice())
                                .subtotal(saleDetailRequest.quantity() * saleDetailRequest.unitPrice())
                                .sale(saleEntity)
                                .product(productEntity)
                                .build()
                );

                // Update Inventory
                InventoryEntity inventoryEntity = inventoryRepository.findByProductId(productEntity.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + productEntity.getId()));
                int newStock = inventoryEntity.getStock() - saleDetailRequest.quantity();
                if (newStock < 0) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + productEntity.getName());
                }
                inventoryEntity.setStock(newStock);
                inventoryRepository.save(inventoryEntity);

                saleDetailEntityList.add(saleDetailEntity);
                saleDetailResponseList.add(
                        new SaleDetailResponse(
                                saleDetailEntity.getId(),
                                new ProductResponse(
                                        saleDetailEntity.getProduct().getId(),
                                        saleDetailEntity.getProduct().getName(),
                                        saleDetailEntity.getProduct().getDescription(),
                                        saleDetailEntity.getProduct().getPrice(),
                                        saleDetailEntity.getProduct().getImage(),
                                        new CategoryResponse(
                                                saleDetailEntity.getProduct().getCategory().getId(),
                                                saleDetailEntity.getProduct().getCategory().getName()
                                        )
                                ),
                                saleDetailEntity.getQuantity(),
                                saleDetailEntity.getUnitPrice(),
                                saleDetailEntity.getSubtotal()
                        )
                );
            }
        });

        saleEntity.setSaleDetailList(saleDetailEntityList);
        saleRepository.save(saleEntity);

        return new SaleResponse(
                saleEntity.getId(),
                saleEntity.getDate(),
                saleEntity.getTotalPrice(),
                saleDetailResponseList
        );
    }
}
