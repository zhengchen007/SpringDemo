package com.olo.ding.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.olo.ding.entity.TaskDistributeEntity;

public interface AddTaskService {
	 String addMission(TaskDistributeEntity taskDistributeEntity) throws Exception;
	 List<Map<String, Object>> missionUploadMethod(HttpServletRequest request, HttpServletResponse response)throws Exception;
	 String queryTaskDeail(Integer taskId)throws Exception;
}
