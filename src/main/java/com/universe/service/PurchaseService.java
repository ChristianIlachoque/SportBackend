package com.universe.service;

import com.universe.controller.dto.*;
import com.universe.persistence.entity.InventoryEntity;
import com.universe.persistence.entity.ProductEntity;
import com.universe.persistence.entity.PurchaseDetailEntity;
import com.universe.persistence.entity.PurchaseEntity;
import com.universe.persistence.repository.InventoryRepository;
import com.universe.persistence.repository.ProductRepository;
import com.universe.persistence.repository.PurchaseDetailRepository;
import com.universe.persistence.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseService {
    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    PurchaseDetailRepository purchaseDetailRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ProductRepository productRepository;

    public List<PurchaseResponse> getAll(){
        List<PurchaseResponse> purchaseResponseList = new ArrayList<>();
        purchaseRepository.findAll().forEach(purchaseEntity -> {
            List<PurchaseDetailResponse> purchaseDetailResponseList = new ArrayList<>();
            purchaseEntity.getPurchaseDetailList().forEach(purchaseDetailEntity -> purchaseDetailResponseList.add(
                    new PurchaseDetailResponse(
                            purchaseDetailEntity.getId(),
                            new ProductResponse(
                                    purchaseDetailEntity.getProduct().getId(),
                                    purchaseDetailEntity.getProduct().getName(),
                                    purchaseDetailEntity.getProduct().getDescription(),
                                    purchaseDetailEntity.getProduct().getPrice(),
                                    purchaseDetailEntity.getProduct().getImage(),
                                    new CategoryResponse(
                                            purchaseDetailEntity.getProduct().getCategory().getId(),
                                            purchaseDetailEntity.getProduct().getCategory().getName()
                                    )
                            ),
                            purchaseDetailEntity.getQuantity(),
                            purchaseDetailEntity.getUnitPrice(),
                            purchaseDetailEntity.getSubtotal()
                    )
            ));

            purchaseResponseList.add(
                    new PurchaseResponse(
                            purchaseEntity.getId(),
                            purchaseEntity.getDate(),
                            purchaseEntity.getTotalPrice(),
                            purchaseDetailResponseList
                    )
            );
        });

        return purchaseResponseList;
    }

    public PurchaseResponse getById(UUID id){
        PurchaseEntity purchaseEntity = purchaseRepository.findById(id).orElse(null);
        if(purchaseEntity != null){
            List<PurchaseDetailResponse> purchaseDetailResponseList = new ArrayList<>();
            purchaseEntity.getPurchaseDetailList().forEach(purchaseDetailEntity -> purchaseDetailResponseList.add(
                    new PurchaseDetailResponse(
                            purchaseDetailEntity.getId(),
                            new ProductResponse(
                                    purchaseDetailEntity.getProduct().getId(),
                                    purchaseDetailEntity.getProduct().getName(),
                                    purchaseDetailEntity.getProduct().getDescription(),
                                    purchaseDetailEntity.getProduct().getPrice(),
                                    purchaseDetailEntity.getProduct().getImage(),
                                    new CategoryResponse(
                                            purchaseDetailEntity.getProduct().getCategory().getId(),
                                            purchaseDetailEntity.getProduct().getCategory().getName()
                                    )
                            ),
                            purchaseDetailEntity.getQuantity(),
                            purchaseDetailEntity.getUnitPrice(),
                            purchaseDetailEntity.getSubtotal()
                    )
            ));
            return new PurchaseResponse(
                    purchaseEntity.getId(),
                    purchaseEntity.getDate(),
                    purchaseEntity.getTotalPrice(),
                    purchaseDetailResponseList
            );
        }
        return null;
    }

    public PurchaseResponse create(PurchaseRequest purchaseRequest){
        if(purchaseRequest.purchaseDetailList() == null || purchaseRequest.purchaseDetailList().isEmpty()){
            throw  new IllegalArgumentException("Purchase must to have at least one PurchaseDetail");
        }

        double totalPrice = purchaseRequest.purchaseDetailList().stream().mapToDouble(purchaseDetailItem -> purchaseDetailItem.quantity() * purchaseDetailItem.unitPrice()).sum();
        PurchaseEntity purchaseEntity = purchaseRepository.save(
                PurchaseEntity.builder()
                        .date(purchaseRequest.date())
                        .totalPrice(totalPrice)
                        .build()
        );

        // Create PurchaseDetail List
        List<PurchaseDetailEntity> purchaseDetailEntityList = new ArrayList<>();
        List<PurchaseDetailResponse> purchaseDetailResponseList = new ArrayList<>();
        purchaseRequest.purchaseDetailList().forEach(purchaseDetailRequest -> {
            ProductEntity productEntity = productRepository.findById(purchaseDetailRequest.product()).orElse(null);
            if(productEntity != null){
                PurchaseDetailEntity purchaseDetailEntity = purchaseDetailRepository.save(
                        PurchaseDetailEntity.builder()
                                .quantity(purchaseDetailRequest.quantity())
                                .unitPrice(purchaseDetailRequest.unitPrice())
                                .subtotal(purchaseDetailRequest.quantity() * purchaseDetailRequest.unitPrice())
                                .purchase(purchaseEntity)
                                .product(productEntity)
                                .build()
                );

                // Update Inventory
                InventoryEntity inventoryEntity = inventoryRepository.findByProductId(productEntity.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + productEntity.getId()));
                int newStock = inventoryEntity.getStock() + purchaseDetailRequest.quantity();
                inventoryEntity.setStock(newStock);
                inventoryRepository.save(inventoryEntity);

                purchaseDetailEntityList.add(purchaseDetailEntity);
                purchaseDetailResponseList.add(
                        new PurchaseDetailResponse(
                                purchaseDetailEntity.getId(),
                                new ProductResponse(
                                        purchaseDetailEntity.getProduct().getId(),
                                        purchaseDetailEntity.getProduct().getName(),
                                        purchaseDetailEntity.getProduct().getDescription(),
                                        purchaseDetailEntity.getProduct().getPrice(),
                                        purchaseDetailEntity.getProduct().getImage(),
                                        new CategoryResponse(
                                                purchaseDetailEntity.getProduct().getCategory().getId(),
                                                purchaseDetailEntity.getProduct().getCategory().getName()
                                        )
                                ),
                                purchaseDetailEntity.getQuantity(),
                                purchaseDetailEntity.getUnitPrice(),
                                purchaseDetailEntity.getSubtotal()
                        )
                );
            }
        });

        purchaseEntity.setPurchaseDetailList(purchaseDetailEntityList);
        purchaseRepository.save(purchaseEntity);

        return new PurchaseResponse(
                purchaseEntity.getId(),
                purchaseEntity.getDate(),
                purchaseEntity.getTotalPrice(),
                purchaseDetailResponseList
        );
    }
}
