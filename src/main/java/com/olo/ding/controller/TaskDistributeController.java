package com.olo.ding.controller;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.olo.ding.entity.TaskDetailEntity;
import com.olo.ding.entity.TaskDistributeEntity;
import com.olo.ding.mapper.TaskDistributeMapper;
import com.olo.ding.service.AddTaskService;
import com.olo.ding.service.TaskDistributeService;
import com.olo.ding.utils.CZDateUtil;
import com.olo.ding.utils.HttpRequester;
import com.olo.ding.utils.MapToJson;
import com.olo.ding.utils.WeChatService;

import cn.com.weaver.services.webservices.WorkflowServicePortTypeProxy;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import weaver.workflow.webservices.WorkflowBaseInfo;
import weaver.workflow.webservices.WorkflowMainTableInfo;
import weaver.workflow.webservices.WorkflowRequestInfo;
import weaver.workflow.webservices.WorkflowRequestTableField;
import weaver.workflow.webservices.WorkflowRequestTableRecord;

@RestController
@RequestMapping(value="/taskDistribute")
public class TaskDistributeController {
	
	@Autowired
	private AddTaskService addTaskService;
	@Autowired
	private TaskDistributeService taskDistributeService;
	static final String url_zs_del =  "http://crmmobile1.olo-home.com:8038/quartz/quartz/job/delete";
	static final String url_cs_del =  "http://10.201.2.231:8080/quartz/job/delete";
	
	static final String url_zs_add = "http://crmmobile1.olo-home.com:8038/quartz/quartz/httpJob/add";
	static final String url_cs_add = "http://10.201.0.65:8089/quartz/httpJob/add";
	/**
	 * 获取新增列表界面
	 * @param Integer userId,Integer type
	 * @return
	 */
	 @ApiOperation(value = "获取新增列表界面", notes="获取新增列表界面")
	 @ApiImplicitParams({
		 @ApiImplicitParam(name="userId",value="当前登录用户",dataType="Integer", paramType = "query",required=true,example="27089"),
		 @ApiImplicitParam(name="type",value="查询类型",dataType="Integer", paramType = "query",required=true,example="0"),
		 @ApiImplicitParam(name="taskResource",value="任务来源类型",dataType="Integer", paramType = "query",required=true,example="0")})
	 @RequestMapping(value="/getDistributeList",method=RequestMethod.GET)
	    public String getNeedToDoListMsg(Integer userId,Integer type,Integer taskResource){
			return taskDistributeService.getDistributeList(userId,type,taskResource);
	    }
	 
	 /**
	  * 任务创建
	  * 请求url：POST http://crmmobile1.olo-home.com:8084/taskDistribute/addTask
	  * 参数：{"sfwzq":"0","rwksrq":"2020-09-01","rwkssj":"15:00:00","rwjsrq":"2020-10-01","rwjssj":"16:00:00","rwmc":"测试任务","cjr":"27089","zxr":"27089","jdr":"27089","xzr":"27089","rwfj":"[www.baidu.com]","cfzq":"1","userId":"27089"}
	  * @param sfwzq：是否为周期任务，0：是，1，否;
	  * rwksrq：任务开始日期;
	  * rwkssj：任务开始时间;
	  * rwjsrq：任务结束日期;
	  * rwjssj：任务结束时间;
	  * rwmc：任务名称;
	  * cjr：创建人;
	  * zxr：执行人;
	  * jdr：监督人;
	  * xzr：协作人;
	  * rwfj：任务附件;
	  * cfzq：重复周期  0按天，1按周，2按月，3按季度;
	  * userId：userId;
	  * @return
	  */
	 @RequestMapping(value="/addTask",method = RequestMethod.POST,produces={"application/json;charset=utf-8"},consumes={"application/json;charset=utf-8"})
	    public String addTask(@RequestBody TaskDistributeEntity distributeEntity){
		 Map<String, Object> returnMap = new HashMap<String, Object>();
			try {
				return addTaskService.addMission(distributeEntity);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			returnMap.put("code", 500);
				return  JSONObject.fromObject(returnMap).toString();
	    }
	 
	 
	 /**
		 * 任务附件上传
		 * 请求url：POST http://crmmobile1.olo-home.com:8084/taskDistribute/missionUpload
		 * @param viewPeopleId
		 * @return
		 */
	 		@ApiOperation(value = "任务附件上传", notes="任务附件上传")
		 	@RequestMapping(value = "/missionUpload",headers = "content-type=multipart/form-data", method = RequestMethod.POST,produces= {"text/html;charset=utf-8"})
		 	@ResponseBody
		    public String missionUpload(HttpServletRequest request, HttpServletResponse response
		    		)
		            {
	 			System.out.println("进入附件上传接口！");
		 		Map<String, Object> returnMap = new HashMap<String, Object>();
		 		//String missionid = (String) parm.get("missionid");
				String jsonStr = "";
				try {
					List<Map<String, Object>> list = addTaskService.missionUploadMethod(request, response);
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
	 		 * 任务明细查询（派发出去的任务）
	 		 * @param missionId
	 		 * @return
	 		 */
	 		@ApiOperation(value = "任务明细查询", notes="任务明细查询")
	 	    @ApiImplicitParam(name="taskId",value="任务id",dataType="Integer", paramType = "query",required=true,example="27089")
		 	@RequestMapping(value = "/queryTaskDeail",method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
		 	@ResponseBody
	 		public String queryTaskDeail(Integer taskId) {
	 				try {
						return addTaskService.queryTaskDeail(taskId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
	 		}
	 		
	 		
	 		/**
	 		 * test
	 		 */
		 	@RequestMapping(value = "/test1",method = RequestMethod.POST, produces = "text/html;charset=UTF-8;")
		 	@ResponseBody
	 		public String test1(Integer taskId) {
		 		System.out.println(123);
		 		return "127.0.1 success!";
	 		}
		 	
			
			
			@RequestMapping(value = "/stopZqTask",method = RequestMethod.POST, produces = "text/html;charset=UTF-8;")
		 	@ResponseBody
	 		public String stopZqTask(@RequestParam(name = "jobName") String jobName,
                    @RequestParam(name = "jobGroup") String jobGroup) {
				try {
					Map<String, Object> bodyMap_del = new HashMap<String, Object>();
					Map<String, Object> parmMap = new HashMap<String, Object>();
					parmMap.put("jobName", jobName);
					parmMap.put("jobGroup", jobGroup);
					HttpRequester.requestPOST(url_zs_del, MapToJson.mapToJson(bodyMap_del).toString(), "application/json;charset=utf-8");
				} catch (Exception e) {
					// TODO: handle exception
					return "success!";
				}
				return jobGroup;
	 		}
			
			
			
			@Autowired
		 	TaskDistributeMapper taskDistributeMapper;
			@RequestMapping(value = "/addZqTask",method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
		 	@ResponseBody
	 		public String addZqTask(@RequestParam(name="uuid") String uuid) {
				SimpleDateFormat sdf = new SimpleDateFormat("00 mm HH dd MM ? yyyy");
				SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd");
				WorkflowServicePortTypeProxy	proxy	= new WorkflowServicePortTypeProxy();
				Map<String, Object> uuidMap = new HashMap<String, Object>();
				uuidMap.put("uuid", uuid);
				List<TaskDistributeEntity> list = taskDistributeMapper.queryUfTableByUuid(uuidMap);
				Map<String, Object> returnMap = new HashMap<String, Object>();
				System.out.println(!list.isEmpty());
				if (!list.isEmpty()) {
					try {
					//如果当前时间和周期要务执行的第一期时间的cron相同或者sfzx=0或者sfwjd=0就执行生成流程
					//if (CZDateUtil.getCron(sdf.parse(sdf.format(new Date()))).equals(CZDateUtil.getCron(sdf.parse(sdf.format(list.get(0).getCron()))))||"0".equals(list.get(0).getSfzx())||"0".equals(list.get(0).getSfwjd())) {
					// 生成任务流程
						
						
					//时间间隔计算，如果为按天或者周重复的，获取当前日期，加上时间间隔，如果有等于当前日期的就触发
				    boolean flag = true;
					String firstTaskDate = list.get(0).getDyckssj();
					if ("0".equals(list.get(0).getCfzq())) {
							//开始时间加上间隔循环判断，如果等于当前日期yyyy-mm-dd就触发，当开始时间加上循环间隔大于结束日期就结束
							String cfrq = "";
							int a = 0;
							do {
								if (a==0) {
									cfrq = firstTaskDate;
								}else {
									cfrq = sdf_time.format(CZDateUtil.addDate(sdf_time.parse(cfrq), list.get(0).getIntervalNum()+1, -1));
								}
								
								a = a+1;
								System.out.println(sdf_time.parse(cfrq).getTime());
								System.out.println(sdf_time.parse(sdf_time.format(new Date())).getTime());
							} while (sdf_time.parse(cfrq).getTime()<sdf_time.parse(sdf_time.format(new Date())).getTime());
								
							if (sdf_time.parse(cfrq).getTime()>sdf_time.parse(sdf_time.format(new Date())).getTime()) {
								flag = false;
							}
							
					}
					
					if ("1".equals(list.get(0).getCfzq())) {
							//开始时间加上间隔循环判断，如果等于当前日期yyyy-mm-dd就触发，当开始时间加上循环间隔大于结束日期就结束
							String cfrq = "";
							int a = 0;
							do {
								if (a==0) {
									cfrq = firstTaskDate;
								}else {
									cfrq = sdf_time.format(CZDateUtil.addDate(sdf_time.parse(cfrq), list.get(0).getIntervalNum()+7, -1));
								}
								
								a = a+1;
								System.out.println(sdf_time.parse(cfrq).getTime());
								System.out.println(sdf_time.parse(sdf_time.format(new Date())).getTime());
							} while (sdf_time.parse(cfrq).getTime()<sdf_time.parse(sdf_time.format(new Date())).getTime());
								
							if (sdf_time.parse(cfrq).getTime()>sdf_time.parse(sdf_time.format(new Date())).getTime()) {
								flag = false;
							}
							
					
					}
					
					if ("2".equals(list.get(0).getCfzq())) {
					
							//开始时间加上间隔循环判断，如果等于当前日期yyyy-mm-dd就触发，当开始时间加上循环间隔大于结束日期就结束
							String cfrq = "";
							int a = 0;
							do {
								if (a==0) {
									cfrq = firstTaskDate;
								}else {
									cfrq = sdf_time.format(CZDateUtil.addDate(sdf_time.parse(cfrq), list.get(0).getIntervalNum()+1,0));
								}
								
								a = a+1;
								System.out.println(sdf_time.parse(cfrq).getTime());
								System.out.println(sdf_time.parse(sdf_time.format(new Date())).getTime());
							} while (sdf_time.parse(cfrq).getTime()<sdf_time.parse(sdf_time.format(new Date())).getTime());
								
							if (sdf_time.parse(cfrq).getTime()>sdf_time.parse(sdf_time.format(new Date())).getTime()) {
								flag = false;
							}
						
					}
					
					if ("3".equals(list.get(0).getCfzq())) {
						
						//开始时间加上间隔循环判断，如果等于当前日期yyyy-mm-dd就触发，当开始时间加上循环间隔大于结束日期就结束
						String cfrq = "";
						int a = 0;
						do {
							if (a==0) {
								cfrq = firstTaskDate;
							}else {
								cfrq = sdf_time.format(CZDateUtil.addDate(sdf_time.parse(cfrq), list.get(0).getPeriods(),1));
							}
							
							a = a+1;
							System.out.println(sdf_time.parse(cfrq).getTime());
							System.out.println(sdf_time.parse(sdf_time.format(new Date())).getTime());
						} while (sdf_time.parse(cfrq).getTime()<sdf_time.parse(sdf_time.format(new Date())).getTime());
							
						if (sdf_time.parse(cfrq).getTime()>sdf_time.parse(sdf_time.format(new Date())).getTime()) {
							flag = false;
						}
					
				}
					
					if (flag) {
						SimpleDateFormat formatter_Date = new SimpleDateFormat("yyyy-MM-dd");
						Date date_now = new Date(System.currentTimeMillis());
						String date_now_str = formatter_Date.format(date_now);
						String zxr = list.get(0).getZxr();
						String jdr = list.get(0).getJdr();
						String xzrStr = list.get(0).getXzr();
						String xzrName = "";
						if (StringUtils.isEmpty(xzrStr)&&xzrStr !=null) {
							List<String> xzr =  Arrays.asList(xzrStr.split(",")); 
							xzrName =  Optional.ofNullable(xzr).orElse(Arrays.asList("")).stream().collect(Collectors.joining(","));
						}
						String cjr = list.get(0).getCjr();
						// 任务描述
						String missionContent = list.get(0).getRwnrStr();
						String rwfjStr = list.get(0).getRwfj();
						//List<Object> rwfjObj = JSONObject.parseArray(rwfjStr);
					    //List<String> rwfjArray = rwfjObj.stream().map(Object::toString).collect(Collectors.toList());
						List<String> rwfjArray = Arrays.asList(rwfjStr.substring(1,rwfjStr.length()-1).split(","));
						// 主字段
						List<String> zxrList = Arrays.asList(zxr.split(","));
						
						for (int j = 0; j < zxrList.size(); j++) {
							WorkflowRequestTableField[] mainTableFields = new WorkflowRequestTableField[13]; // 字段信息
							mainTableFields[0] = new WorkflowRequestTableField();
							mainTableFields[0].setFieldName("name");//
							mainTableFields[0].setFieldValue(list.get(0).getRwmc());//
							mainTableFields[0].setView(true);// 字段是否可见
							mainTableFields[0].setEdit(true);// 字段是否可编辑
							mainTableFields[1] = new WorkflowRequestTableField();
							mainTableFields[1].setFieldName("zxr");//
							mainTableFields[1].setFieldValue(zxrList.get(j));//
							mainTableFields[1].setView(true);// 字段是否可见
							mainTableFields[1].setEdit(true);// 字段是否可编辑

							mainTableFields[2] = new WorkflowRequestTableField();
							mainTableFields[2].setFieldName("jdr");//
							mainTableFields[2].setFieldValue(jdr);//
							mainTableFields[2].setView(true);// 字段是否可见
							mainTableFields[2].setEdit(true);// 字段是否可编辑

							mainTableFields[3] = new WorkflowRequestTableField();
							mainTableFields[3].setFieldName("xzr");//
							mainTableFields[3].setFieldValue(xzrName);//
							mainTableFields[3].setView(true);// 字段是否可见
							mainTableFields[3].setEdit(true);// 字段是否可编辑

							mainTableFields[4] = new WorkflowRequestTableField();
							mainTableFields[4].setFieldName("re_cjr");//
							mainTableFields[4].setFieldValue(cjr);//
							mainTableFields[4].setView(true);// 字段是否可见
							mainTableFields[4].setEdit(true);// 字段是否可编辑

							String missionStartTime = "";
							String missionEndTime = "";
							if ("0".equals(list.get(0).getSfwzq())) {
								String zyyqscJson = list.get(0).getZyyqscJson();
								String zykssjJson = list.get(0).getZykssjJson();
								Map<String, Object> map = new HashMap<String, Object>();
								Gson gson = new Gson();
								map = gson.fromJson(zykssjJson, map.getClass());
								missionStartTime = date_now_str+" "+CZDateUtil.bw(Integer.parseInt(new java.text.DecimalFormat("0").format(map.get("hour"))))+":"+CZDateUtil.bw(Integer.parseInt(new java.text.DecimalFormat("0").format(map.get("mins"))));
								//作业时长
								List<String> zyscList = Arrays.asList(zyyqscJson.split(","));
								String jzsj = CZDateUtil.getJzDate(zyscList, missionStartTime);
								if (!"-1".equals(jzsj)) {
									missionEndTime = jzsj;
								}
							}else {
								 missionStartTime = date_now_str+" "+list.get(0).getRwkssj();
								 missionEndTime = list.get(0).getRwjsrq()+" "+list.get(0).getRwjssj();
							}
							
							mainTableFields[5] = new WorkflowRequestTableField();
							mainTableFields[5].setFieldName("rwksrq");//
							mainTableFields[5].setFieldValue(missionStartTime);//
							mainTableFields[5].setView(true);// 字段是否可见
							mainTableFields[5].setEdit(true);// 字段是否可编辑
							
							mainTableFields[6] = new WorkflowRequestTableField();
							mainTableFields[6].setFieldName("rwjzrq");//
							mainTableFields[6].setFieldValue(missionEndTime);//
							mainTableFields[6].setView(true);// 字段是否可见
							mainTableFields[6].setEdit(true);// 字段是否可编辑
							
							String fileNameStr = "";
							String fileUrlStr = "";
							String head = "http:";
							String url = "";
							String name = "";

							if (rwfjArray != null && rwfjArray.size() > 0) {
								for (int g = 0; g < rwfjArray.size(); g++) {
									fileNameStr += (head + rwfjArray.get(g).substring(rwfjArray.get(g).lastIndexOf("/") + 1,
											rwfjArray.get(g).length())) + "|";
									fileUrlStr += rwfjArray.get(g)+ "|";
								}
								url = fileUrlStr.substring(0, fileUrlStr.length() - 1);
								name = fileNameStr.substring(0, fileNameStr.length() - 1);
							}

							System.out.println("===============同步url为===============" + url);
							System.out.println("===============同步fileName为===============" + name);
							mainTableFields[7] = new WorkflowRequestTableField();
							mainTableFields[7].setFieldName("rwfj");//
							mainTableFields[7].setFieldType(name);// http:开头代表该字段为附件字段
							mainTableFields[7].setFieldValue(url);// 附件地址
							mainTableFields[7].setView(true);// 字段是否可见 
							mainTableFields[7].setEdit(true);// 字段是否可编辑

							
							mainTableFields[8] = new WorkflowRequestTableField();
							mainTableFields[8].setFieldName("rwfjdz");//
							mainTableFields[8].setFieldValue(url);//
							mainTableFields[8].setView(true);// 字段是否可见
							mainTableFields[8].setEdit(true);// 字段是否可编辑
							    
							mainTableFields[9] = new WorkflowRequestTableField();
							mainTableFields[9].setFieldName("cfzq");//
							mainTableFields[9].setFieldValue(list.get(0).getCfzq());//
							mainTableFields[9].setView(true);// 字段是否可见
							mainTableFields[9].setEdit(true);// 字段是否可编辑
							
							mainTableFields[10] = new WorkflowRequestTableField();
							mainTableFields[10].setFieldName("taskResource");//
							mainTableFields[10].setFieldValue(list.get(0).getRwly());//
							mainTableFields[10].setView(true);// 字段是否可见
							mainTableFields[10].setEdit(true);// 字段是否可编辑
							
							mainTableFields[11] = new WorkflowRequestTableField();
							mainTableFields[11].setFieldName("rwjmid");//
							mainTableFields[11].setFieldValue(String.valueOf(list.get(0).getTaskId()));//
							mainTableFields[11].setView(true);// 字段是否可见
							mainTableFields[11].setEdit(true);// 字段是否可编辑

							mainTableFields[12] = new WorkflowRequestTableField();
							mainTableFields[12].setFieldName("lcyy");//
							mainTableFields[12].setFieldValue("0");//
							mainTableFields[12].setView(true);// 字段是否可见
							mainTableFields[12].setEdit(true);// 字段是否可编辑
							
							String requestName = list.get(0).getRwmc();

							WorkflowRequestTableRecord[] mainTableRecords = new WorkflowRequestTableRecord[1];
							mainTableRecords[0] = new WorkflowRequestTableRecord();
							mainTableRecords[0].setWorkflowRequestTableFields(mainTableFields);
							WorkflowMainTableInfo mainTableInfo = new WorkflowMainTableInfo();
							mainTableInfo.setRequestRecords(mainTableRecords);

							WorkflowBaseInfo baseInfo = new WorkflowBaseInfo();
							//正式环境65542
							//测试环境65542
							baseInfo.setWorkflowId("65542");
							WorkflowRequestInfo requestInfo = new WorkflowRequestInfo();
							requestInfo.setCreatorId("1");
							requestInfo.setRequestLevel("1");
							requestInfo.setRequestName(requestName);
							requestInfo.setWorkflowMainTableInfo(mainTableInfo);
							requestInfo.setWorkflowBaseInfo(baseInfo);
							try {
								String result = proxy.doCreateWorkflowRequest(requestInfo, 1);
								WeChatService.newSendGet(zxrList.get(j), "您有一条新的任务，任务名称为："+list.get(0).getRwmc()+",请登录我乐钉钉及时处理。");
								//如果当前时间和周期要务执行的第一期时间的cron更新是否执行为0
								if (CZDateUtil.getCron(sdf.parse(sdf.format(new Date()))).equals(list.get(0).getCron())) {
									Map<String, Object> sfzxMap = new HashMap<String, Object>();
									sfzxMap.put("taskId", list.get(0).getTaskId());
									sfzxMap.put("sfzx", "0");
									taskDistributeMapper.updateSfzx(sfzxMap);
								}
								Map<String, Object> parmTaskidMap = new HashMap<String, Object>();
								parmTaskidMap.put("requestId", result);
								List<TaskDetailEntity> listId = taskDistributeMapper.queryTaskId(parmTaskidMap);
								//把任务内容插入任务流程明细表
									List<String> rwnrList = Arrays.asList(missionContent.split("&"));
									Map<String, Object> parmRwnr = new HashMap<String, Object>();
									for (int i = 0; i < rwnrList.size(); i++) {
										if (!StringUtils.isEmpty(rwnrList.get(i))&&!"null".equals(rwnrList.get(i))&&rwnrList.get(i)!=null) {
											parmRwnr.clear();
											parmRwnr.put("mainId", listId.get(0).getId());
											parmRwnr.put("rwnr", rwnrList.get(i));
											taskDistributeMapper.addRwnr(parmRwnr);
										}
									}
									
									//如果是非周期任务,更新uf_olo_ding_task中的sfcf（是否触发任务）
									if ("1".equals(list.get(0).getSfwzq())) {
										Map<String, Object> updateParm = new HashMap<String,Object>();
										updateParm.put("taskId", list.get(0).getTaskId());
										taskDistributeMapper.updateSfcf(updateParm);
									}
									//如果是周期任务，更新uf_olo_ding_task中的cfsj（触发任务的时间）
									if ("0".equals(list.get(0).getSfwzq())) {
										Map<String, Object> updateParm = new HashMap<String,Object>();
										updateParm.put("cfsj", date_now_str+",");
										updateParm.put("taskId", list.get(0).getTaskId());
										taskDistributeMapper.updateScsj(updateParm);
									}
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						  }
						//}
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
					}
				}
				returnMap.put("code", "201");
				return JSONObject.fromObject(returnMap).toString();
	 		}
			
			
			@RequestMapping(value = "/addZqTaskByHand",method = RequestMethod.GET, produces = "text/html;charset=UTF-8;")
		 	@ResponseBody
	 		public String addZqTaskByHand(@RequestParam(name="uuid") String uuid) {
				SimpleDateFormat sdf = new SimpleDateFormat("00 mm HH dd MM ? yyyy");
				WorkflowServicePortTypeProxy	proxy	= new WorkflowServicePortTypeProxy();
				Map<String, Object> uuidMap = new HashMap<String, Object>();
				uuidMap.put("uuid", uuid);
				List<TaskDistributeEntity> list = taskDistributeMapper.queryUfTableByUuid(uuidMap);
				Map<String, Object> returnMap = new HashMap<String, Object>();
				System.out.println(!list.isEmpty());
				if (!list.isEmpty()) {
					try {
						SimpleDateFormat formatter_Date = new SimpleDateFormat("yyyy-MM-dd");
						Date date_now = new Date(System.currentTimeMillis());
						String date_now_str = formatter_Date.format(date_now);
						String zxr = list.get(0).getZxr();
						String jdr = list.get(0).getJdr();
						String xzrStr = list.get(0).getXzr();
						String xzrName = "";
						if (StringUtils.isEmpty(xzrStr)&&xzrStr !=null) {
							List<String> xzr =  Arrays.asList(xzrStr.split(",")); 
							xzrName =  Optional.ofNullable(xzr).orElse(Arrays.asList("")).stream().collect(Collectors.joining(","));
						}
						String cjr = list.get(0).getCjr();
						// 任务描述
						String missionContent = list.get(0).getRwnrStr();
						String rwfjStr = list.get(0).getRwfj();
						List<String> rwfjArray = Arrays.asList(rwfjStr.substring(1,rwfjStr.length()-1).split(","));
						// 主字段
						List<String> zxrList = Arrays.asList(zxr.split(","));
						List<TaskDistributeEntity> aleradyCreateWorkFlow = taskDistributeMapper.aleradyCreateworkFlow(uuidMap);
						List<String> zxrAlList = new ArrayList<String>();
						if ((!aleradyCreateWorkFlow.isEmpty())&&aleradyCreateWorkFlow!=null) {
							for (int i = 0; i < aleradyCreateWorkFlow.size(); i++) {
								zxrAlList.add(aleradyCreateWorkFlow.get(i).getZxr());
							}
						}
						
						for (int j = 0; j < zxrList.size(); j++) {
							if (!zxrAlList.contains(zxrList.get(j))) {
							
							WorkflowRequestTableField[] mainTableFields = new WorkflowRequestTableField[13]; // 字段信息
							mainTableFields[0] = new WorkflowRequestTableField();
							mainTableFields[0].setFieldName("name");//
							mainTableFields[0].setFieldValue(list.get(0).getRwmc());//
							mainTableFields[0].setView(true);// 字段是否可见
							mainTableFields[0].setEdit(true);// 字段是否可编辑
							mainTableFields[1] = new WorkflowRequestTableField();
							mainTableFields[1].setFieldName("zxr");//
							mainTableFields[1].setFieldValue(zxrList.get(j));//
							mainTableFields[1].setView(true);// 字段是否可见
							mainTableFields[1].setEdit(true);// 字段是否可编辑

							mainTableFields[2] = new WorkflowRequestTableField();
							mainTableFields[2].setFieldName("jdr");//
							mainTableFields[2].setFieldValue(jdr);//
							mainTableFields[2].setView(true);// 字段是否可见
							mainTableFields[2].setEdit(true);// 字段是否可编辑

							mainTableFields[3] = new WorkflowRequestTableField();
							mainTableFields[3].setFieldName("xzr");//
							mainTableFields[3].setFieldValue(xzrName);//
							mainTableFields[3].setView(true);// 字段是否可见
							mainTableFields[3].setEdit(true);// 字段是否可编辑

							mainTableFields[4] = new WorkflowRequestTableField();
							mainTableFields[4].setFieldName("re_cjr");//
							mainTableFields[4].setFieldValue(cjr);//
							mainTableFields[4].setView(true);// 字段是否可见
							mainTableFields[4].setEdit(true);// 字段是否可编辑

							String missionStartTime = "";
							String missionEndTime = "";
							if ("0".equals(list.get(0).getSfwzq())) {
								String zyyqscJson = list.get(0).getZyyqscJson();
								String zykssjJson = list.get(0).getZykssjJson();
								Map<String, Object> map = new HashMap<String, Object>();
								Gson gson = new Gson();
								map = gson.fromJson(zykssjJson, map.getClass());
								missionStartTime = date_now_str+" "+CZDateUtil.bw(Integer.parseInt(new java.text.DecimalFormat("0").format(map.get("hour"))))+":"+CZDateUtil.bw(Integer.parseInt(new java.text.DecimalFormat("0").format(map.get("mins"))));
								//作业时长
								List<String> zyscList = Arrays.asList(zyyqscJson.split(","));
								String jzsj = CZDateUtil.getJzDate(zyscList, missionStartTime);
								if (!"-1".equals(jzsj)) {
									missionEndTime = jzsj;
								}
							}else {
								 missionStartTime = date_now_str+" "+list.get(0).getRwkssj();
								 missionEndTime = list.get(0).getRwjsrq()+" "+list.get(0).getRwjssj();
							}
							
							mainTableFields[5] = new WorkflowRequestTableField();
							mainTableFields[5].setFieldName("rwksrq");//
							mainTableFields[5].setFieldValue(missionStartTime);//
							mainTableFields[5].setView(true);// 字段是否可见
							mainTableFields[5].setEdit(true);// 字段是否可编辑
							
							mainTableFields[6] = new WorkflowRequestTableField();
							mainTableFields[6].setFieldName("rwjzrq");//
							mainTableFields[6].setFieldValue(missionEndTime);//
							mainTableFields[6].setView(true);// 字段是否可见
							mainTableFields[6].setEdit(true);// 字段是否可编辑
							
							String fileNameStr = "";
							String fileUrlStr = "";
							String head = "http:";
							String url = "";
							String name = "";

							if (rwfjArray != null && rwfjArray.size() > 0) {
								for (int g = 0; g < rwfjArray.size(); g++) {
									fileNameStr += (head + rwfjArray.get(g).substring(rwfjArray.get(g).lastIndexOf("/") + 1,
											rwfjArray.get(g).length())) + "|";
									fileUrlStr += rwfjArray.get(g)+ "|";
								}
								url = fileUrlStr.substring(0, fileUrlStr.length() - 1);
								name = fileNameStr.substring(0, fileNameStr.length() - 1);
							}

							System.out.println("===============同步url为===============" + url);
							System.out.println("===============同步fileName为===============" + name);
							mainTableFields[7] = new WorkflowRequestTableField();
							mainTableFields[7].setFieldName("rwfj");//
							mainTableFields[7].setFieldType(name);// http:开头代表该字段为附件字段
							mainTableFields[7].setFieldValue(url);// 附件地址
							mainTableFields[7].setView(true);// 字段是否可见 
							mainTableFields[7].setEdit(true);// 字段是否可编辑

							
							mainTableFields[8] = new WorkflowRequestTableField();
							mainTableFields[8].setFieldName("rwfjdz");//
							mainTableFields[8].setFieldValue(url);//
							mainTableFields[8].setView(true);// 字段是否可见
							mainTableFields[8].setEdit(true);// 字段是否可编
							    
							mainTableFields[9] = new WorkflowRequestTableField();
							mainTableFields[9].setFieldName("cfzq");//
							mainTableFields[9].setFieldValue(list.get(0).getCfzq());//
							mainTableFields[9].setView(true);// 字段是否可见
							mainTableFields[9].setEdit(true);// 字段是否可编辑
							
							mainTableFields[10] = new WorkflowRequestTableField();
							mainTableFields[10].setFieldName("taskResource");//
							mainTableFields[10].setFieldValue(list.get(0).getRwly());//
							mainTableFields[10].setView(true);// 字段是否可见
							mainTableFields[10].setEdit(true);// 字段是否可编辑
							
							mainTableFields[11] = new WorkflowRequestTableField();
							mainTableFields[11].setFieldName("rwjmid");//
							mainTableFields[11].setFieldValue(String.valueOf(list.get(0).getTaskId()));//
							mainTableFields[11].setView(true);// 字段是否可见
							mainTableFields[11].setEdit(true);// 字段是否可编辑

							mainTableFields[12] = new WorkflowRequestTableField();
							mainTableFields[12].setFieldName("lcyy");//
							mainTableFields[12].setFieldValue("0");//
							mainTableFields[12].setView(true);// 字段是否可见
							mainTableFields[12].setEdit(true);// 字段是否可编辑
							
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
							String nowDate = df.format(new Date());
							String requestName = "任务流程-" + nowDate;

							WorkflowRequestTableRecord[] mainTableRecords = new WorkflowRequestTableRecord[1];
							mainTableRecords[0] = new WorkflowRequestTableRecord();
							mainTableRecords[0].setWorkflowRequestTableFields(mainTableFields);
							WorkflowMainTableInfo mainTableInfo = new WorkflowMainTableInfo();
							mainTableInfo.setRequestRecords(mainTableRecords);

							WorkflowBaseInfo baseInfo = new WorkflowBaseInfo();
							//正式环境65542
							//测试环境65542
							baseInfo.setWorkflowId("65542");
							WorkflowRequestInfo requestInfo = new WorkflowRequestInfo();
							requestInfo.setCreatorId(cjr);
							requestInfo.setRequestLevel("1");
							requestInfo.setRequestName(requestName);
							requestInfo.setWorkflowMainTableInfo(mainTableInfo);
							requestInfo.setWorkflowBaseInfo(baseInfo);
							try {
								String result = proxy.doCreateWorkflowRequest(requestInfo, 1);
								WeChatService.newSendGet(zxrList.get(j), "您有一条新的任务，任务名称为："+list.get(0).getRwmc()+",请登录我乐钉钉及时处理。");
								//如果当前时间和周期要务执行的第一期时间的cron更新是否执行为0
								if (CZDateUtil.getCron(sdf.parse(sdf.format(new Date()))).equals(list.get(0).getCron())) {
									Map<String, Object> sfzxMap = new HashMap<String, Object>();
									sfzxMap.put("taskId", list.get(0).getTaskId());
									sfzxMap.put("sfzx", "0");
									taskDistributeMapper.updateSfzx(sfzxMap);
								}
								Map<String, Object> parmTaskidMap = new HashMap<String, Object>();
								parmTaskidMap.put("requestId", result);
								List<TaskDetailEntity> listId = taskDistributeMapper.queryTaskId(parmTaskidMap);
								//把任务内容插入任务流程明细表
									List<String> rwnrList = Arrays.asList(missionContent.split("&"));
									Map<String, Object> parmRwnr = new HashMap<String, Object>();
									for (int i = 0; i < rwnrList.size(); i++) {
										if (!StringUtils.isEmpty(rwnrList.get(i))&&!"null".equals(rwnrList.get(i))&&rwnrList.get(i)!=null) {
											parmRwnr.clear();
											parmRwnr.put("mainId", listId.get(0).getId());
											parmRwnr.put("rwnr", rwnrList.get(i));
											taskDistributeMapper.addRwnr(parmRwnr);
										}
									}
									
									//如果是非周期任务,更新uf_olo_ding_task中的sfcf（是否触发任务）
									if ("1".equals(list.get(0).getSfwzq())) {
										Map<String, Object> updateParm = new HashMap<String,Object>();
										updateParm.put("taskId", list.get(0).getTaskId());
										taskDistributeMapper.updateSfcf(updateParm);
									}
									//如果是周期任务，更新uf_olo_ding_task中的cfsj（触发任务的时间）
									if ("0".equals(list.get(0).getSfwzq())) {
										Map<String, Object> updateParm = new HashMap<String,Object>();
										updateParm.put("cfsj", date_now_str+",");
										updateParm.put("taskId", list.get(0).getTaskId());
										taskDistributeMapper.updateScsj(updateParm);
									}
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						  }
						}
						//}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
					}
				}
				returnMap.put("code", "201");
				return JSONObject.fromObject(returnMap).toString();
	 		}
			
			 @RequestMapping(value="/deleteNotPf",method = RequestMethod.POST,produces={"application/json;charset=utf-8"},consumes={"application/json;charset=utf-8"})
			    public String deleteNotPf(@RequestBody TaskDistributeEntity distributeEntity){
				 Map<String, Object> returnMap = new HashMap<String, Object>();
					try {
						return taskDistributeService.deleteNotPf(distributeEntity);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					returnMap.put("code", 500);
						return  JSONObject.fromObject(returnMap).toString();
			    }
			 
			 
			public static void main(String[] args) {
				try {
					SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd");
					//System.out.println(CZDateUtil.addDate(new Date(), 3, -1));
					String cfrq = sdf_time.format(CZDateUtil.addDate(sdf_time.parse("2020-08-21"),5*(2+1), -1));
					System.out.println(cfrq);
					System.out.println("2020-08-26".substring(8,10));
					System.out.println(CZDateUtil.addDate(sdf_time.parse("2021-01-01"), 4+1, -1));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
}
