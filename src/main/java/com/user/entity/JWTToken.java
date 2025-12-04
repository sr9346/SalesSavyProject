package com.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "jwt_tokens")
public class JWTToken {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tokenId;
	
	@ManyToOne
	@JoinColumn(name = "user_id",nullable = false)
	private User user;
	
	@Column(nullable= false)
	private String token;
	
	@Column(nullable= true)
	private LocalDateTime expiresAt;

	public JWTToken(User user2, String token2, LocalDateTime localDateTime) {
		super();
		// TODO Auto-generated constructor stub
	}

	public JWTToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JWTToken(Integer tokenId, User user, String token, LocalDateTime expiresAt) {
		super();
		this.tokenId = tokenId;
		this.user = user;
		this.token = token;
		this.expiresAt = expiresAt;
	}

	public Integer getTokenId() {
		return tokenId;
	}

	public void setTokenId(Integer tokenId) {
		this.tokenId = tokenId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	

}
