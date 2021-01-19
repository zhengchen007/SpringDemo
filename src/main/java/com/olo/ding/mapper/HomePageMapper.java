package com.olo.ding.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.olo.ding.entity.HomePageEntity;
import com.olo.ding.entity.HrmEntity;
import com.olo.ding.utils.Node;

@Component
@Mapper
public interface HomePageMapper {
	
	List<HomePageEntity> getTaskList(Map<String, Object> map); 
	
	List<HomePageEntity> getPgdList(Map<String, Object> map);
	
	List<HrmEntity>getAllPeople(Map<String, Object> map);
	
	List<HrmEntity>queryBranchPeople(Map<String, Object> map); 
	
	List<Node>queryPmTree(Map<String, Object> map);
	
	List<Node>queryPmTreeQl(Map<String, Object> map);
	
	List<HrmEntity>getPhone(Map<String, Object>map);
	
	List<HrmEntity>querySubordinate(Map<String, Object> map);
	
	List<HrmEntity>getPmPeople(Map<String, Object> map);
	
	List<HrmEntity>queryAllZJ();
	
	
}
