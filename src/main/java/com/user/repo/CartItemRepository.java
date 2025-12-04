package com.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.entity.CartItem;
import com.user.entity.Product;
import com.user.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query("SELECT c FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId")
    Optional<CartItem> findByUserAndProduct(@Param("userId") Integer userId,
                                            @Param("productId") Integer productId);

    @Query("SELECT c FROM CartItem c WHERE c.user.userId = :userId")
    List<CartItem> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM CartItem c " +
           "JOIN FETCH c.product p " +
           "LEFT JOIN FETCH p.images img " +
           "WHERE c.user.userId = :userId")
    List<CartItem> findCartItemsWithProductDetails(@Param("userId") int userId);


    @Modifying
    @Transactional
    @Query("UPDATE CartItem c SET c.quantity = :quantity WHERE c.id = :id")
    void updateCartItemQuantity(@Param("id") int cartItemId,
                                @Param("quantity") int quantity);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId")
    void deleteCartItem(@Param("userId") int userId,
                        @Param("productId") int productId);

    void deleteAllByUserUserId(int userId);


  
}

                                            
