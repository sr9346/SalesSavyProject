package com.user.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.user.entity.ProductImage;

import jakarta.transaction.Transactional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProduct_ProductId(Integer productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductImage p WHERE p.product.productId = :productId")
    void deleteByProductId(Integer productId);
}
