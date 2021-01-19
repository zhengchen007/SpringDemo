package com.olo.ding.service;

import java.util.List;
import java.util.Map;

import com.olo.ding.entity.HrmEntity;

public interface HomePageService {
	String getListMsg(Integer userId,Integer type);
	Map<String, Object> queryAllPeople(String type, String keyword, String pageSize, String pageNum,Integer userId) throws Exception;
	List<HrmEntity> queryBranchPeople(Integer userId) throws Exception;
	Map<String, Object> queryOrganizationTree(Integer userId,Integer type) throws Exception;
	Map<String, Object> getPmTree(Integer userId,Integer type)throws Exception;
	Map<String, Object> querySubordinate(Integer userId,String pageSize, String pageNum,String keyword) throws Exception;
	Map<String, Object> queryPmPeople(String pm, String pageSize, String pageNum) throws Exception;
	
	
}
