package com.olo.ding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.olo.ding.entity.TaskDistributeEntity;
import com.olo.ding.service.CycleTaskService;

@RestController
@RequestMapping(value="/cycleTask")
public class CycleTaskController {
	@Autowired
	private CycleTaskService CycleTaskService;
	@RequestMapping(value = "/getCycleTaskList", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
	@ResponseBody
	public String getCycleTaskList(@RequestParam(value = "userId") Integer userId
			) {
		return CycleTaskService.queryCrycleTaskList(userId);
	}
	
	@RequestMapping(value = "/getCycleTaskDetail", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
	@ResponseBody
	public String getCycleTaskDetail(@RequestParam(value = "taskId") String taskId
			) {
		return CycleTaskService.queryCycleTaskDetail(taskId);
	}
	
	/**
	 * 提前终止
	 */
	@RequestMapping(value = "/aheadofStop", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
	@ResponseBody
	public String aheadofStop(@RequestParam(value = "taskId") String taskId
			) {
		return CycleTaskService.aheadofStop(taskId);
	}
	
	/**
	 * 修改周期要务
	 */
	@RequestMapping(value = "/modify", method = RequestMethod.POST, produces = "text/html;charset=UTF-8;")
	@ResponseBody
	public String modify(@RequestBody TaskDistributeEntity taskDisTributeEntity
			) {
		return CycleTaskService.modify(taskDisTributeEntity);
	}
	
	/**
	 * 周期要务修改明细查询
	 */
	@RequestMapping(value = "/modifyDeatil", method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
	@ResponseBody
	public String modifyDeatil(String taskId
			) {
		return CycleTaskService.modifyDeatil(taskId);
	}
}
