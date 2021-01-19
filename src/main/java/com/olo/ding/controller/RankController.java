package com.olo.ding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.olo.ding.service.RankService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/rankPage")
public class RankController {
	 @Autowired
	 RankService rankService;
	 /**
	  * 获取排名数据接口
	  * 请求url：GET http://crmmobile1.olo-home.com:8084/rankPage/getRank?userId=27089
	  * @param userId
	  * @return
	  * {"code":"200","data":{"First30":[{"name":"陈铮","score":86,"rank":1},{"name":"薛敏","score":8,"rank":2},{"name":"Nina","score":6,"rank":3},{"name":"刘奔","score":5,"rank":4},{"name":"张晶晶","score":2,"rank":5},{"name":"吴梦","score":1,"rank":6},{"name":"何慧","score":1,"rank":7}],"currentQuarter":"第二季度","currentYear":"2020年","groupByDep":[{"name":"陈铮","score":86,"rank":1},{"name":"薛敏","score":8,"rank":2},{"name":"刘奔","score":5,"rank":3}],"needToGetScore":43,"End30":[{"name":"吴梦","score":1,"rank":1},{"name":"何慧","score":1,"rank":2},{"name":"张晶晶","score":2,"rank":3},{"name":"刘奔","score":5,"rank":4},{"name":"Nina","score":6,"rank":5},{"name":"薛敏","score":8,"rank":6},{"name":"陈铮","score":86,"rank":7}],"completionRate":"200.0%","scoreByPersonal":{"27089":86},"personalRank":[{"name":"陈铮","score":86,"rank":1}]}}
	  * First30：公司排名前三十列表
	  * name：姓名
	  * score：得分
	  * rank：排名
	  * End30：公司排名后三十列表
	  * currentQuarter：当前季度
	  * currentYear:当前年份
	  * groupByDep：同部门排名列表
	  * needToGetScore：应得分
	  * completionRate：按时完成率
	  * personalRank：[{"name":"陈铮","score":86,"rank":1}]，score，我的得分，rank：公司排名
	  */
	 @ApiOperation(value = "获取首页排名数据", notes="获取首页排名数据")
	 @ApiImplicitParams({
		 @ApiImplicitParam(name="userId",value="当前登录用户",dataType="Integer", paramType = "query",required=true,example="27089"),
		 @ApiImplicitParam(name="type",value="周期类型(0本季度 1 本年度)",dataType="Integer", paramType = "query",required=true,example="27089")}
		 )
	 @RequestMapping(value="/getRank",method=RequestMethod.GET)
	    public String getRank(Integer userId,Integer type){
		 	System.out.println(123);
			return rankService.getRank(userId,type);
	    }
}
