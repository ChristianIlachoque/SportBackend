package com.universe;

import com.universe.persistence.entity.*;
import com.universe.persistence.repository.CategoryRepository;
import com.universe.persistence.repository.InventoryRepository;
import com.universe.persistence.repository.ProductRepository;
import com.universe.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SportBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportBackendApplication.class, args);
    }
    // Pendent
    /*
    * Config ec2 to build in at cloud
    * implement reservations
    * */

    @Bean
    CommandLineRunner init(UserRepository userRepository, CategoryRepository categoryRepository, ProductRepository productRepository, InventoryRepository inventoryRepository) {
        return args -> {
            /* Create PERMISSIONS */
            PermissionEntity createPermission = PermissionEntity.builder()
                    .name("CREATE")
                    .build();

            PermissionEntity readPermission = PermissionEntity.builder()
                    .name("READ")
                    .build();

            PermissionEntity updatePermission = PermissionEntity.builder()
                    .name("UPDATE")
                    .build();

            PermissionEntity deletePermission = PermissionEntity.builder()
                    .name("DELETE")
                    .build();

            PermissionEntity refactorPermission = PermissionEntity.builder()
                    .name("REFACTOR")
                    .build();

            /* Create ROLES */
            RoleEntity roleAdmin = RoleEntity.builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
                    .build();

            RoleEntity roleUser = RoleEntity.builder()
                    .roleEnum(RoleEnum.USER)
                    .permissionList(Set.of(createPermission, readPermission))
                    .build();

            RoleEntity roleInvited = RoleEntity.builder()
                    .roleEnum(RoleEnum.INVITED)
                    .permissionList(Set.of(readPermission))
                    .build();

            RoleEntity roleDeveloper = RoleEntity.builder()
                    .roleEnum(RoleEnum.DEVELOPER)
                    .permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission))
                    .build();

            /* CREATE USERS */
            UserEntity userSantiago = UserEntity.builder()
                    .username("chris")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleAdmin))
                    .build();

            UserEntity userDaniel = UserEntity.builder()
                    .username("daniel")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleUser))
                    .build();

            UserEntity userAndrea = UserEntity.builder()
                    .username("andrea")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleInvited))
                    .build();

            UserEntity userAnyi = UserEntity.builder()
                    .username("anyi")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleDeveloper))
                    .build();

            userRepository.saveAll(List.of(userSantiago, userDaniel, userAndrea, userAnyi));

            /* CREATE CATEGORIES */
            CategoryEntity bebidas = CategoryEntity.builder()
                    .name("Bebidas")
                    .build();

            CategoryEntity comestibles = CategoryEntity.builder()
                    .name("Comestibles")
                    .build();

            categoryRepository.saveAll(List.of(bebidas, comestibles));


            /* CREATE PRODUCTS */

            ProductEntity cocacola = ProductEntity.builder()
                    .name("Coca Cola")
                    .description("Gaseosa CocaCola")
                    .price(8.00)
                    .category(bebidas)
                    .build();

            ProductEntity inkacola = ProductEntity.builder()
                    .name("Inka Cola")
                    .description("Gaseosa InkaCola")
                    .price(10.50)
                    .category(bebidas)
                    .build();

            ProductEntity pastel = ProductEntity.builder()
                    .name("Pastel")
                    .description("Paste de Chocolate")
                    .price(0.50)
                    .category(comestibles)
                    .build();

            productRepository.saveAll(List.of(cocacola, inkacola, pastel));


            /* CREATE INVETORY */

            InventoryEntity iInkacola = InventoryEntity.builder()
                    .stock(0)
                    .product(inkacola)
                    .build();

            InventoryEntity iCocacola = InventoryEntity.builder()
                    .stock(0)
                    .product(cocacola)
                    .build();

            InventoryEntity iPastel = InventoryEntity.builder()
                    .stock(0)
                    .product(pastel)
                    .build();

            inventoryRepository.saveAll(List.of(iInkacola, iCocacola, iPastel));

        };
    }
}
