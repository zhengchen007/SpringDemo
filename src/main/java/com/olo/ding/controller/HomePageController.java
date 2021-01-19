package com.olo.ding.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.olo.ding.entity.HomePageEntity;
import com.olo.ding.entity.HrmEntity;
import com.olo.ding.mapper.HomePageMapper;
import com.olo.ding.service.HomePageService;
import com.olo.ding.utils.JwtUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/homePage")
public class HomePageController {
	@Autowired
	private HomePageService homePageService;
	/**
	 * 获取首页列表数据
	 * 请求url：GET http://crmmobile1.olo-home.com:8039/homePage/getListMsg?userId=27089&type=(0待办，1已办)
	 * @param Integer userId,Integer type
	 * @return
	 * 返回值：{"code":"200","data":{"rw":[{"cutoffTime":"-187天0小时","isOverTime":0,"requestId":"4909516","taskDemandTime":"2019-12-24","taskDistributor":"陈铮","taskName":"测试会议2","taskReceiver":"陈铮","taskResourceName":"会议"}],"pgd":[]}}
	 * rw：任务列表
	 * cutoffTime：截止时间
	 * isOverTime：是否逾期，0：已逾期，1：未逾期
	 * taskDemandTime：任务截止日期
	 * taskDistributor:任务派发人
	 * taskReceiver：任务接收人
	 * taskResourceName：来源
	 * taskName：任务名称
	 * pgd：派工单列表，里面的字段和任务列表字段一致
	 */
	 @ApiOperation(value = "获取首页列表数据", notes="获取首页列表数据")
	 @ApiImplicitParams({
		 @ApiImplicitParam(name="userId",value="当前登录用户",dataType="Integer", paramType = "query",required=true,example="27089"),
		 @ApiImplicitParam(name="type",value="查询类型",dataType="Integer", paramType = "query",required=true,example="0")})
	 @RequestMapping(value="/getListMsg" ,method=RequestMethod.GET)
	    public String getNeedToDoListMsg(Integer userId,Integer type){
				return homePageService.getListMsg(userId,type);
	    }

		/**
		 * 组织架构所有人员查询，请求类型：GET，请求url：http:/localhost:8080/EnterpriseCalendar/calendar/queryAllPeople
		 * 返回参数如{"code":"200","data":[{"name":"陈铮","id":"27089","bm":"IT项目部","fb":"总部（职能、研发、生产）","zw":"软件开发工程师"}]}
		 * name：姓名 id：登陆用户id bm：部门名称 fb：分部 zw：职位
		 * 
		 * @param type
		 *            查询类型 0:查全量，1:查同部门 ，2：根据条件搜索
		 * @param keyword
		 *            根据电话或者姓名查询（传手机号或者姓名），查询同部门人员（传userid）
		 * @return Json字符串
		 */
	 	@ApiOperation(value = "人员查询", notes="人员查询")
	 	@ApiImplicitParams({
		@ApiImplicitParam(name="type",value="类型(0空是全量，1是模糊查询)",dataType="String", paramType = "query",required=false,example=""),
		@ApiImplicitParam(name="keyword",value="模糊查询关键字，手机号码或者姓名",dataType="String", paramType = "query",required=false,example="陈铮"),
	 	@ApiImplicitParam(name="pageNum",value="分页页数",dataType="String", paramType = "query",required=true,example="10"),
	 	@ApiImplicitParam(name="pageSize",value="每页条数",dataType="String", paramType = "query",required=true,example="10")})
		@RequestMapping(value = "/queryAllPeople", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
		@ResponseBody
		public String queryAllPeople(@RequestParam(value = "type") String type,
				@RequestParam(value = "keyword") String keyword, @RequestParam(value = "pageSize") String pageSize,
				@RequestParam(value = "pageNum") String pageNum,
				@RequestParam(value = "userId") Integer userId
				) {
			Map<String, Object> map = new HashMap<String, Object>();
			String jsonStr = "";
			try {
				map = homePageService.queryAllPeople(type, keyword, pageSize, pageNum,userId);
				map.put("code", "201");
				jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
			} catch (Exception e) {
				System.out.println(e);
				map.clear();
				map.put("code", "501");
				jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
			}
			return jsonStr;
		}
	 	
	 	
	 	/**
	 	 * 查询下级人员
		 */
		@RequestMapping(value = "/querySubordinate", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
		@ResponseBody
		public String querySubordinat(
				@RequestParam(value = "keyword") String keyword,
				@RequestParam(value = "pageSize") String pageSize,
				@RequestParam(value = "pageNum") String pageNum,
				@RequestParam(value = "userId") Integer userId) {
			Map<String, Object> map = new HashMap<String, Object>();
			String jsonStr = "";
			try {
				map = homePageService.querySubordinate(userId, pageSize, pageNum,keyword);
				map.put("code", "201");
				jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
			} catch (Exception e) {
				System.out.println(e);
				map.clear();
				map.put("code", "501");
				jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
			}
			return jsonStr;
		}
	 	
	 /**
	    *  获取当前登录人的下级
	  * @param args
	  */
		@ApiOperation(value = "获取当前登录人的下级", notes="获取当前登录人的下级")
	 	@ApiImplicitParams({
	 	@ApiImplicitParam(name="userId",value="当前登录人id",dataType="Integer", paramType = "query",required=true,example="123")})
		
		@RequestMapping(value = "/queryBranchTree", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
		@ResponseBody
	 public String queryBranchTree(Integer userId) {
			
			List<HrmEntity> list = new ArrayList<HrmEntity>();
			Map<String, Object> map = new HashMap<String, Object>();
			String jsonStr = "";
			try {
				list = homePageService.queryBranchPeople(userId);
				map.put("code", "201");
				map.put("data", list);
				jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
			} catch (Exception e) {
				System.out.println(e);
				map.clear();
				map.put("code", "501");
				jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
			}
			return jsonStr;
	 }

		/**
		    *  获取所有人树
		  * @param args
		  */
			@ApiOperation(value = "获取组织架构树", notes="获取组织架构树")
			@RequestMapping(value = "/queryOrganizationTree", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
			@ApiImplicitParams({
			 	@ApiImplicitParam(name="userId",value="当前登录人id",dataType="Integer", paramType = "query",required=true,example="123"),
			 	@ApiImplicitParam(name="type",value="type(0：查询全量，1：查询下级)",dataType="Integer", paramType = "query",required=true,example="123")
			}
			)
			@ResponseBody
		public String queryOrganizationTree(Integer userId,Integer type) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			String jsonStr = "";
			try {
				map = homePageService.queryOrganizationTree(userId,type);
				map.put("code", "201");
				jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
			} catch (Exception e) {
				System.out.println(e);
				map.clear();
				map.put("code", "501");
				jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
			}
			return jsonStr;
		}
		
			
	/**
	 * 获取职级人树
	 * 
	 * @param args
	 */
	@ApiOperation(value = "获取职级树", notes = "获取职级树")
	@RequestMapping(value = "/queryPmTree", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
	@ApiImplicitParams({
	 	@ApiImplicitParam(name="userId",value="当前登录人id",dataType="Integer", paramType = "query",required=true,example="123"),
	 	@ApiImplicitParam(name="type",value="type(0：查询全量，1：查询下级)",dataType="Integer", paramType = "query",required=true,example="123")
	}
	)
	@ResponseBody
	public String queryPmTree(Integer userId,Integer type) {

		Map<String, Object> map = new HashMap<String, Object>();
		String jsonStr = "";
		try {
			map = homePageService.getPmTree(userId,type);
			map.put("code", "201");
			jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
		} catch (Exception e) {
			System.out.println(e);
			map.clear();
			map.put("code", "501");
			jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
		}
		return jsonStr;
	}
	
	
	@RequestMapping(value = "/queryPmPeople", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
	@ResponseBody
	public String queryPmPeople(@RequestParam(value = "pm") String pm,
			@RequestParam(value = "pageSize") String pageSize,
			@RequestParam(value = "pageNum") String pageNum
			) {
		Map<String, Object> map = new HashMap<String, Object>();
		String jsonStr = "";
		try {
			map = homePageService.queryPmPeople(pm,pageSize,pageNum);
			map.put("code", "201");
			jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
		} catch (Exception e) {
			System.out.println(e);
			map.clear();
			map.put("code", "501");
			jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
		}
		return jsonStr;
	}
	
	@RequestMapping(value = "getAuth", method = RequestMethod.GET, produces = "test/json;charset=UTF-8")
	@ApiImplicitParams({
	 	@ApiImplicitParam(name="userId",value="当前登录人id",dataType="Integer", paramType = "query",required=true,example="123"),
	})
	@ResponseBody
	public String getAuth(@RequestParam(value = "userId") String userId, HttpServletResponse response)
			throws UnsupportedEncodingException {
		String token = JwtUtil.sign(userId);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("code", "200");
		returnMap.put("userId", userId);
		returnMap.put("acsesstoken", token);
		String jsonStr = JSON.toJSONString(returnMap,SerializerFeature.WriteMapNullValue);
		return jsonStr;
	}
	 
	@Autowired
	HomePageMapper homePageMapper;
	@RequestMapping(value = "/queryZjId", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
	@ResponseBody
	public String queryZjId() {
		Map<String, Object> map = new HashMap<String, Object>();
		String jsonStr = "";
		try {
			
			List<HrmEntity> list = homePageMapper.queryAllZJ();
			map.put("data", list);
			map.put("code", "201");
			jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
		} catch (Exception e) {
			System.out.println(e);
			map.clear();
			map.put("code", "501");
			jsonStr = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
		}
		return jsonStr;
	}
}
