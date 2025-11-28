package com.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.entity.User;
import com.user.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	
	private final UserService userservice;

	@Autowired
	public UserController(UserService userservice) {
		super();
		this.userservice = userservice;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody User user) {
		
		try {
			User registereduser = userservice.registerUser(user);
			return ResponseEntity.ok(Map.of("message","User registed successfull","User",registereduser));
		}
		catch(RuntimeException e) {
			return  ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));
			
		}
		
	}
}
