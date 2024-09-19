package com.universe.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.universe.controller.dto.Asset;
import com.universe.controller.dto.CategoryResponse;
import com.universe.controller.dto.ProductRequest;
import com.universe.controller.dto.ProductResponse;
import com.universe.persistence.entity.CategoryEntity;
import com.universe.persistence.entity.InventoryEntity;
import com.universe.persistence.entity.ProductEntity;
import com.universe.persistence.repository.CategoryRepository;
import com.universe.persistence.repository.InventoryRepository;
import com.universe.persistence.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final Path rootFolder = Paths.get("uploads");

    private final static String BUCKET = "";
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    private AmazonS3Client s3Client;

    public List<ProductResponse> getAll(){
        List<ProductResponse> productResponseList = new ArrayList<>();
        productRepository.findAll().forEach(productEntity -> {
            InventoryEntity inventoryEntity = inventoryRepository.findByProductId(productEntity.getId()).orElse(null);
            productResponseList.add(
                    new ProductResponse(
                            productEntity.getId(),
                            productEntity.getName(),
                            productEntity.getDescription(),
                            productEntity.getPrice(),
                            productEntity.getImage(),
                            new CategoryResponse(
                                    productEntity.getCategory().getId(),
                                    productEntity.getCategory().getName()
                            ),
                            inventoryEntity.getStock()
                            )
            );
        });
        return productResponseList;
    }

    public ProductResponse getById(UUID id){
        ProductEntity productEntity = productRepository.findById(id).orElse(null);
        if(productEntity != null){
            InventoryEntity inventoryEntity = inventoryRepository.findByProductId(productEntity.getId()).orElse(null);
            return new ProductResponse(
                    productEntity.getId(),
                    productEntity.getName(),
                    productEntity.getDescription(),
                    productEntity.getPrice(),
                    productEntity.getImage(),
                    new CategoryResponse(
                            productEntity.getCategory().getId(),
                            productEntity.getCategory().getName()
                    ),
                    inventoryEntity.getStock()
            );
        }
        return null;
    }

    public ProductResponse create(ProductRequest productRequest){
        CategoryEntity categoryEntity = categoryRepository.findById(productRequest.categoryId()).orElse(null);
        ProductEntity productEntity = productRepository.save(
                ProductEntity.builder()
                        .name(productRequest.name())
                        .description(productRequest.description())
                        .price(productRequest.price())
                        .image(productRequest.image())
                        .category(categoryEntity)
                        .build()
        );

        // Create Inventory with 0 stock
        inventoryRepository.save(
                InventoryEntity.builder()
                        .product(productEntity)
                        .stock(0)
                        .build()
        );

        return new ProductResponse(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getPrice(),
                productEntity.getImage(),
                new CategoryResponse(
                        productEntity.getCategory().getId(),
                        productEntity.getCategory().getName()
                ),
                0
        );
    }

    public ProductResponse update(UUID id, ProductRequest productRequest){
        ProductEntity productEntity = productRepository.findById(id).orElse(null);
        if(productEntity != null){
            CategoryEntity categoryEntity = categoryRepository.findById(productRequest.categoryId()).orElse(null);
            productEntity.setName(productRequest.name());
            productEntity.setDescription(productRequest.description());
            productEntity.setPrice(productRequest.price());
            productEntity.setCategory(categoryEntity);
        }

        InventoryEntity inventoryEntity = inventoryRepository.findByProductId(productEntity.getId()).orElse(null);
        ProductEntity updatedProductEntity = productRepository.save(productEntity);

        return new ProductResponse(
                updatedProductEntity.getId(),
                updatedProductEntity.getName(),
                updatedProductEntity.getDescription(),
                updatedProductEntity.getPrice(),
                updatedProductEntity.getImage(),
                new CategoryResponse(
                        updatedProductEntity.getCategory().getId(),
                        updatedProductEntity.getCategory().getName()
                ),
                inventoryEntity.getStock()
        );
    }

    public boolean delete(UUID id){
        try {
            // Delete Inventory
            InventoryEntity inventoryEntity = inventoryRepository.findByProductId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + id));
            inventoryRepository.deleteById(inventoryEntity.getId());
            productRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public ProductResponse uploadImage(UUID id, MultipartFile image) throws IOException {
        String imageUploadedName = id.toString().concat(image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")));
        String newPath = "/uploads/" + imageUploadedName;

        ProductEntity productEntity = productRepository.findById(id).orElse(null);
        if(productEntity != null) {
            if(productEntity.getImage() != null) {
                Path oldImagePath = this.rootFolder.resolve(productEntity.getImage().substring("/uploads/".length()));

                if (Files.exists(oldImagePath)) {
                    Files.delete(oldImagePath);
                }
            }
            productEntity.setImage(newPath);
        }

        Files.copy(image.getInputStream(), this.rootFolder.resolve(imageUploadedName));
        InventoryEntity inventoryEntity = inventoryRepository.findByProductId(productEntity.getId()).orElse(null);

        ProductEntity updatedProductEntity = productRepository.save(productEntity);
        return new ProductResponse(
                updatedProductEntity.getId(),
                updatedProductEntity.getName(),
                updatedProductEntity.getDescription(),
                updatedProductEntity.getPrice(),
                updatedProductEntity.getImage(),
                new CategoryResponse(
                        updatedProductEntity.getCategory().getId(),
                        updatedProductEntity.getCategory().getName()
                ),
                inventoryEntity.getStock()
        );
    }

    public ProductResponse uploadImageAWS(UUID id, MultipartFile image){
        ProductEntity productEntity = productRepository.findById(id).orElse(null);

        if(productEntity != null){
            if(productEntity.getImage() != null) {
                deleteObject(productEntity.getImage().substring(productEntity.getImage().lastIndexOf("/") + 1));
            }
            String key = putObject(id, image);
            String newPath = getObjectUrl(key);
            productEntity.setImage(newPath);
        }
        InventoryEntity inventoryEntity = inventoryRepository.findByProductId(productEntity.getId()).orElse(null);
        ProductEntity updatedProductEntity = productRepository.save(productEntity);
        return new ProductResponse(
                updatedProductEntity.getId(),
                updatedProductEntity.getName(),
                updatedProductEntity.getDescription(),
                updatedProductEntity.getPrice(),
                updatedProductEntity.getImage(),
                new CategoryResponse(
                        updatedProductEntity.getCategory().getId(),
                        updatedProductEntity.getCategory().getName()
                ),
                inventoryEntity.getStock()
        );
    }

    public String putObject(UUID id, MultipartFile image){
        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        String key = id.toString().concat(extension);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        try{
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, key, image.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putObjectRequest);
            return key;
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public Asset getObject(String key){
        S3Object s3Object = s3Client.getObject(BUCKET, key);
        ObjectMetadata metadata = s3Object.getObjectMetadata();

        try{
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            return new Asset(bytes, metadata.getContentType());
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public void deleteObject(String key){
        s3Client.deleteObject(BUCKET, key);
    }

    public String getObjectUrl(String key){
        return String.format("https://%s.s3.amazonaws.com/%s", BUCKET, key);
    }
}
