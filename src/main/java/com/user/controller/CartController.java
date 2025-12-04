package com.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.entity.User;
import com.user.repo.UserRepository;
import com.user.service.CartService;

import jakarta.servlet.http.HttpServletRequest;



@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RequestMapping("/api/cart")
public class CartController {
	
	private final CartService cartService;
	private final UserRepository userRepository;
	
	
	
	@Autowired
	public CartController(CartService cartService, UserRepository userRepository) {
		super();
		this.cartService = cartService;
		this.userRepository = userRepository;
	}




	@PostMapping("/add")
	public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {

	    // FIX: Convert safely from JSON string/number
	    Integer productId = Integer.parseInt(request.get("productId").toString());
	    Integer quantity = Integer.parseInt(request.get("quantity").toString());
	    String username = request.get("username").toString();

	    // Get User
	    User user = userRepository.findByusername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    // FIX: pass userId, not user object
	    cartService.addToCart(user.getUserId(), productId, quantity);

	    return ResponseEntity.ok("Added");
	}

	
	
	 @GetMapping("/items")
	    public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {

	       
	        User user = (User) request.getAttribute("authenticatedUser");
	        Map<String, Object> cartItems = cartService.getCartItems(user.getUserId());

	        return ResponseEntity.ok(cartItems);
	    }
	 
	 @PutMapping("/update")
	 public ResponseEntity<Void> updateCartItemQuantity(
	         @RequestBody Map<String, Object> request) {

	     // Extract data from request
	     String username = (String) request.get("username");
	     int productId = (int) request.get("productId");
	     int quantity = (int) request.get("quantity");

	     User user = userRepository.findByusername(username)
	             .orElseThrow(() -> new IllegalArgumentException(
	                     "User not found with username: " + username));

	     // Update the cart item quantity
	     cartService.updateCartItemQuantity(
	             user.getUserId(), productId, quantity);

	     return ResponseEntity.status(HttpStatus.OK).build();
	 }
	 
	 
	 @DeleteMapping("/delete")
	 public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request) {

	     String username = (String) request.get("username");
	     int productId = (int) request.get("productId");

	     // Fetch the user using username
	     User user = userRepository.findByusername(username)
	             .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

	     // Delete the cart item
	     cartService.deleteCartItem(user.getUserId(), productId);

	     return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	 }


	
	
	
	

	
	
	





}
