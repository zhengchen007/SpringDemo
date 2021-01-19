package com.olo.ding.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.olo.ding.entity.DealTaskEntity;
import com.olo.ding.service.DealTaskService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/dealTask")
public class DealTaskController {
	 /**
	 * 执行人
	 * 请求url：POST http://crmmobile1.olo-home.com:8084/dealTask/executorFileUpload
	 * @param viewPeopleId
	 * @return
	 */
	@Autowired
	DealTaskService dealTaskService;
 		@ApiOperation(value = "执行人附件上传", notes="执行人附件上传")
	 	@RequestMapping(value = "/executorFileUpload",headers = "content-type=multipart/form-data", method = RequestMethod.POST,produces= {"text/html;charset=utf-8"})
	 	@ResponseBody
	    public String executorFileUpload(HttpServletRequest request, HttpServletResponse response
	    		)
	            {
 			System.out.println("进入执行人附件上传接口！");
	 		Map<String, Object> returnMap = new HashMap<String, Object>();
	 		//String missionid = (String) parm.get("missionid");
			String jsonStr = "";
			try {
				List<Map<String, Object>> list = dealTaskService.executorFileUpload(request, response);
				returnMap.put("code", "201");
				returnMap.put("data", list);
				jsonStr = JSON.toJSONString(returnMap,SerializerFeature.WriteMapNullValue);
				return jsonStr;
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
				returnMap.put("code", "501");
				jsonStr = JSON.toJSONString(returnMap,SerializerFeature.WriteMapNullValue); 
				return jsonStr;
			}
	    	
	    }
 		
 		 /**
 		 * 监督人
 		 * 请求url：POST http://crmmobile1.olo-home.com:8084/dealTask/proctorFileUpload
 		 * @param viewPeopleId
 		 * @return
 		 */
 		@ApiOperation(value = "监督人附件上传", notes="监督人附件上传")
	 	@RequestMapping(value = "/proctorFileUpload",headers = "content-type=multipart/form-data", method = RequestMethod.POST,produces= {"text/html;charset=utf-8"})
	 	@ResponseBody
	    public String proctorFileUpload(HttpServletRequest request, HttpServletResponse response
	    		)
	            {
 			System.out.println("进入监督人附件上传接口！");
	 		Map<String, Object> returnMap = new HashMap<String, Object>();
	 		//String missionid = (String) parm.get("missionid");
			String jsonStr = "";
			try {
				List<Map<String, Object>> list = dealTaskService.proctorFileUpload(request, response);
				returnMap.put("code", "201");
				returnMap.put("data", list);
				jsonStr = JSON.toJSONString(returnMap,SerializerFeature.WriteMapNullValue);
				return jsonStr;
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
				returnMap.put("code", "501");
				jsonStr = JSON.toJSONString(returnMap,SerializerFeature.WriteMapNullValue); 
				return jsonStr;
			}
	    	
	    }
 		
 		/**
 		 * 任务详情查看
 		 * @param requestId
 		 * @param taskResource
 		 * @return
 		 */
 		@ApiOperation(value = "首页待办任务详情查看", notes="首页待办任务详情查看")
 		@ApiImplicitParam(name="requestId",value="请求id",dataType="Integer", paramType = "query",required=true,example="xxxxx")
	 	@RequestMapping(value = "/queryNeedToDealTask",method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
	 	@ResponseBody
	    public String queryNeedToDealTask(Integer requestId,Integer taskResource
	    		)
	            {
 			System.out.println("进入首页待办任务详情查看接口！");
 			return dealTaskService.queryNeedToDealTask(requestId, taskResource);
	    }
 		
 		
 		/**
 		 * 流程提交和退回操作
 		 * @param dealTaskEntity
 		 * @return
 		 */
 		@ApiOperation(value = "流程处理（提交和退回）", notes="流程处理（提交和退回）")
 		@RequestMapping(value = "/excuteWorkFlow", method = RequestMethod.POST)
	 	@ResponseBody
	 	//Integer requestId,Integer userId,Integer submitType,Integer taskResource,List<> zxrfjdz,list<>jdrfjdz
 		public String excuteWorkFlow(@RequestBody DealTaskEntity dealTaskEntity) {
 			
 			System.out.println("进入流程处理（提交和退回）！");
 			return dealTaskService.excuteWorkFlow(dealTaskEntity);
 		}
 		
 		
 		/**
 		 * 流程提交和退回操作
 		 * @param dealTaskEntity
 		 * @return
 		 */
 		@ApiOperation(value = "流程处理（提交和退回）", notes="流程处理（提交和退回）")
 		@RequestMapping(value = "/excutePgdWorkFlow", method = RequestMethod.POST)
	 	@ResponseBody
	 	//Integer requestId,Integer userId,Integer submitType,Integer taskResource,List<> zxrfjdz,list<>jdrfjdz
 		public String excutePgdWorkFlow(@RequestBody DealTaskEntity dealTaskEntity) {
 			
 			return dealTaskService.excutePgdWorkFlow(dealTaskEntity);
 		}
 		
 		/**
 		 * 流程退回的时候判断任务是否超时
 		 * 如果超时就直接提交，如果不超时就打开弹窗,0:直接提交，1：打开弹窗
 		 * @param dealTaskEntity
 		 * @return
 		 */
 		@RequestMapping(value = "/isOvertime", method = RequestMethod.POST)
	 	@ResponseBody
 		public String isOvertime(@RequestBody DealTaskEntity dealTaskEntity) {
 			return dealTaskService.isOvertime(dealTaskEntity);
 		}
 		
 		/**
 		 * 更新截止日期
 		 * @param dealTaskEntity
 		 * @return
 		 */
 		@RequestMapping(value = "/updateJzrq", method = RequestMethod.POST)
	 	@ResponseBody
 		public String updateJzrq(@RequestBody DealTaskEntity dealTaskEntity) {
 			return dealTaskService.updateJzrq(dealTaskEntity);
 		}
 		
 		
}
