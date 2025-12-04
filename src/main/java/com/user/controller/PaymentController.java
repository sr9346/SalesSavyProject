package com.user.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.user.entity.OrderItem;
import com.user.entity.User;
import com.user.repo.UserRepository;
import com.user.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RequestMapping("/api/payment")
public class PaymentController {

   
    private final PaymentService paymentService;
    private  final UserRepository userRepository;
    

    	@Autowired
    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
		super();
		this.paymentService = paymentService;
		this.userRepository = userRepository;
	}

	@PostMapping("/create")
    public ResponseEntity<String> createPaymentOrder(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {

        try {
            // Fetch authenticated user
            User user = (User) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            // Extract totalAmount and cartItems
            BigDecimal totalAmount =
                    new BigDecimal(requestBody.get("totalAmount").toString());

            List<Map<String, Object>> cartItemsRaw =
                    (List<Map<String, Object>>) requestBody.get("cartItems");

            // Convert raw cart items → List<OrderItem>
            List<OrderItem> cartItems = cartItemsRaw.stream().map(item -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId((Integer) item.get("productId"));
                orderItem.setQuantity((Integer) item.get("quantity"));

                BigDecimal pricePerUnit =
                        new BigDecimal(item.get("price").toString());

                orderItem.setPricePerUnit(pricePerUnit);
                orderItem.setTotalPrice(
                        pricePerUnit.multiply(
                                BigDecimal.valueOf((Integer) item.get("quantity"))
                        )
                );

                return orderItem;
            }).collect(Collectors.toList());
            String razorpayOrderId =
                    paymentService.createOrder(user.getUserId(), totalAmount, cartItems);

            return ResponseEntity.ok(razorpayOrderId);

        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating Razorpay order: " + e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request data: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {

        try {
            // Fetch authenticated user
            User user = (User) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            int userId = user.getUserId();

            
            String razorpayOrderId = (String) requestBody.get("razorpayOrderId");
            String razorpayPaymentId = (String) requestBody.get("razorpayPaymentId");
            String razorpaySignature = (String) requestBody.get("razorpaySignature");

            // Call the payment service to verify the payment
            boolean isVerified = paymentService.verifyPayment(
                    razorpayOrderId,
                    razorpayPaymentId,
                    razorpaySignature,
                    userId
            );

            if (isVerified) {
                return ResponseEntity.ok("Payment verified successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Payment verification failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying payment: " + e.getMessage());
        }
    }
}

