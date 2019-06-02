package com.sicau.tiredsys.entity;

import lombok.Data;

@Data
public class User {
	private String openid;
	private String userName;
	private String avatarUrl;
	private String password;
	private Integer fingerprint;
	private String role;


	public User(String userName, String password,String openId,String role) {
		this.userName = userName;
		this.password = password;
		this.openid = openId;
		this.role = role;
	}

	public User() {
	}
}
