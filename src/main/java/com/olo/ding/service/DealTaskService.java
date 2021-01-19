package com.olo.ding.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.olo.ding.entity.DealTaskEntity;

public interface DealTaskService {
	List<Map<String, Object>> executorFileUpload(HttpServletRequest request, HttpServletResponse response)throws Exception;
	List<Map<String, Object>> proctorFileUpload(HttpServletRequest request, HttpServletResponse response)throws Exception;
	String queryNeedToDealTask(Integer requestId,Integer taskResource);
	String excuteWorkFlow(DealTaskEntity dealTaskEntity);
	String excutePgdWorkFlow(DealTaskEntity dealTaskEntity);
	String isOvertime(DealTaskEntity dealTaskEntity);
	String updateJzrq(DealTaskEntity dealTaskEntity);
}
