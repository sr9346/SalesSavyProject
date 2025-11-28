package com.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.entity.User;
import com.user.repository.UserRepo;


@Service
public class UserService {
	
	private final UserRepo userrepo;
	private final BCryptPasswordEncoder passwordencoder;
	
	public UserService(UserRepo userrepo) {
		super();
		this.userrepo = userrepo;
		this.passwordencoder = new BCryptPasswordEncoder();
	}
	
	
	public User registerUser(User user) {
		
		if(userrepo.findByusername(user.getUsername()).isPresent()){
			throw new RuntimeException("UserName is Alredy Presner");
			
		}
		if(userrepo.findByemail(user.getEmail()).isPresent()){
			throw new RuntimeException("Email  is Alredy Present");
			
		}
		
		user.setPassword(passwordencoder.encode(user.getPassword()));
		return userrepo.save(user);
		
	}
	

}
