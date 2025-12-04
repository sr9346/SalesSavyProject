package com.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.user.entity.CartItem;
import com.user.entity.ProductImage;
import com.user.entity.Product;
import com.user.entity.User;
import com.user.repo.CartItemRepository;
import com.user.repo.ProductImageRepository;
import com.user.repo.ProductRepository;
import com.user.repo.UserRepository;

@Service
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartRepository;
    private final ProductImageRepository productImageRepository;

    public CartService(UserRepository userRepository,
                       ProductRepository productRepository,
                       CartItemRepository cartRepository,ProductImageRepository productImageRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.productImageRepository = productImageRepository;
    }

    public void addToCart(Integer userId, Integer productId, Integer quantity) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existing = cartRepository.findByUserAndProduct(userId, productId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartRepository.save(item);
        } else {
            CartItem newItem = new CartItem(user, product, quantity);
            cartRepository.save(newItem);
        }
    }

    
    
    public Map<String, Object> getCartItems(int userId) {


        List<CartItem> cartItems = cartRepository.findCartItemsWithProductDetails(userId);

        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        response.put("username", user.getUsername());
        response.put("role", user.getRole().toString());

        List<Map<String, Object>> products = new ArrayList<>();
        int overallTotalPrice = 0;

        for (CartItem cartItem : cartItems) {
            Map<String, Object> productDetails = new HashMap<>();

            // Get product details
            Product product = cartItem.getProduct();

            // Fetch product images
            List<ProductImage> productImages =
                    productImageRepository.findByProduct_ProductId(product.getProductId());

            String imageUrl = (productImages != null && !productImages.isEmpty())
                    ? productImages.get(0).getImageUrl()
                    : "default-image.jpg";

            // Populate product details
            productDetails.put("product_id", product.getProductId());
            productDetails.put("name", product.getName());
            productDetails.put("image", imageUrl);
            productDetails.put("quantity", cartItem.getQuantity());
            productDetails.put("price", product.getPrice());
            productDetails.put("total_price", cartItem.getQuantity() * product.getPrice().doubleValue());

            products.add(productDetails);

   
            overallTotalPrice += cartItem.getQuantity() * product.getPrice().doubleValue();
        }

        Map<String, Object> cart = new HashMap<>();
        cart.put("products", products);
        cart.put("overall_total_price", overallTotalPrice);

        response.put("cart", cart);

        return response;
    }
    
    
    
    public void updateCartItemQuantity( int userId, int productId, int quantity) {

        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Fetch cart item for this user & product
        Optional<CartItem> existingItem =
        		cartRepository.findByUserAndProduct(user.getUserId(), product.getProductId());
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();

            if (quantity <= 0) {
                cartRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(quantity);
                cartRepository.save(cartItem);
            }
        } else {
            throw new IllegalArgumentException("Cart item not found");
        }
    }
    
    public void deleteCartItem(int userId, int productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cartRepository.deleteCartItem(userId, productId);
    }


}