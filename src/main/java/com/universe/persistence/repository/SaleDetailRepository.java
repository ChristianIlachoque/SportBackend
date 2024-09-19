package com.universe.persistence.repository;

import com.universe.persistence.entity.SaleDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetailEntity, UUID> {
}
