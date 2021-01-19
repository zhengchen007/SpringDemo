package com.olo.ding.entity;

import java.util.Map;

import javax.persistence.Table;

import org.springframework.data.annotation.Id;
@Table(name="formtable_main_621_dt7")
public class TestEntity {
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String id;
	private String mainid;
	private String space;
	public String getMainid() {
		return mainid;
	}
	public void setMainid(String mainid) {
		this.mainid = mainid;
	}
	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
	
	

}
