package com.user.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.entity.User;
import java.util.*;


@Repository
public interface UserRepo extends JpaRepository<User,Integer>{
	
	
	Optional<User> findByemail(String email);
	
	Optional<User> findByusername(String username);
	
	
}
