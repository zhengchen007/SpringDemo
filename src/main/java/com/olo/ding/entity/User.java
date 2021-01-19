package com.olo.ding.entity;

public class User {
	
	private String addr;
	private String name;
	private Integer score;
	
	public User(String name,String addr,Integer score) {
		
	this.name = name;
	this.addr = addr;
	this.score = score;
		
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}
