package com.commerce.auth.entity;

import
import

@Entity
@Table(name="users")
public class User {
	
	@Id
	private Long id;
	@Column
	private String email;
	@Column
	private String passwordHash;
	@column
	private Set<String> roles;


	private Instant createdAt = Instant.now();






}

