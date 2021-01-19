package com.olo.ding.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.olo.ding.entity.HomePageEntity;
import com.olo.ding.entity.HrmEntity;
import com.olo.ding.mapper.HomePageMapper;
import com.olo.ding.service.HomePageService;
import com.olo.ding.service.RedisService;
import com.olo.ding.utils.CZDateUtil;
import com.olo.ding.utils.GetHrmMsg;
import com.olo.ding.utils.Node;
import com.olo.ding.utils.NodeUtil;
import com.olo.ding.utils.Pager;

import net.sf.json.JSONArray;


@Service("HomePageService")
@Transactional(rollbackFor = Exception.class)
public class HomePageServiceImpl implements HomePageService{
	private static final String SUCCESSCODE="201";
	private static final String FILEDCODE ="501";
	@Autowired
	private HomePageMapper homePageMapper;
	 @Autowired
	private RedisService redisService;
	
	
	//任务未逾期个数
	private static Integer taskWyqCount =0;
	//任务逾期个数
	private static Integer taskYqCount =0;
	 //派工单未逾期个数
	 private static Integer pgdWyqCount =0;
	 //派工单逾期个数
	 private static Integer pgdYqCount =0;
	 //任务即将过期个数
	 private static Integer taskJjgq = 0;
	 //派工单即将过期个数
	 private static Integer pgdJjgq = 0;
	@Override
	public String getListMsg(Integer userId,Integer type) {
		SimpleDateFormat df_time_s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		SimpleDateFormat df_time_hour = new SimpleDateFormat("yyyy-MM-dd HH");// 设置日期格式
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		//获取任务列表、计算截止时间、计算是否逾期
		//String rwNodeId = type==0?"90550":"90551,90552";
		Map<String, Object> parmMap = new HashMap<String, Object>();
		if (type == 0) {
			//待办
			parmMap.put("type", "0");
		}else if(type == 1){
			//已办
			parmMap.put("type", "1");
		}
		parmMap.put("userId", userId);
	 	List<HomePageEntity> list = homePageMapper.getTaskList(parmMap);
	 	list.stream().forEach(a->{
	 		try {
	 			//如果是待办，计算截止日期是截止时间~当前时间,如果是未逾期，显示截止时间，截止时间-当前时间,如果是已逾期，显示已逾期，当前时间-截止时间
	 			//如果是已办，计算截止日期是截止时间~处理时间,如果是未逾期，显示截止时间，截止时间-流程处理时间，如果是已逾期，显示已逾期，处理时间-截止时间
	 			if (type == 0) {
	 				a.setIsOverTime(df_time_hour.parse(a.getTaskDemandTime()).compareTo(df_time_hour.parse(df_time_hour.format(new Date())))>=0?1:0);
					if (a.getIsOverTime() == 1) {
						//未逾期
						a.setCutoffTime(CZDateUtil.getDatePoor(df_time_hour.parse(a.getTaskDemandTime()),df_time_hour.parse(df_time_hour.format(new Date()))));
					}else if(a.getIsOverTime() == 0){
						//已逾期
						a.setCutoffTime(CZDateUtil.getDatePoor(df_time_hour.parse(df_time_hour.format(new Date())),df_time_hour.parse(a.getTaskDemandTime())));
					} 
				}else if(type == 1) {
	 				a.setIsOverTime(df_time_hour.parse(a.getTaskDemandTime()).compareTo(df_time_hour.parse(a.getOperateDateAndTime()))>=0?1:0);
	 				if (a.getIsOverTime() == 1) {
						//未逾期
						a.setCutoffTime(CZDateUtil.getDatePoor(df_time_hour.parse(a.getTaskDemandTime()),df_time_hour.parse(a.getOperateDateAndTime())));
					}else if(a.getIsOverTime() == 0){
						//已逾期
						a.setCutoffTime(CZDateUtil.getDatePoor(df_time_hour.parse(a.getOperateDateAndTime()),df_time_hour.parse(a.getTaskDemandTime())));
					} 
				}
				
				if (a.getIsOverTime()==0) {
					taskYqCount = taskYqCount+1;
				}else if(a.getIsOverTime()==1) {
					taskWyqCount = taskWyqCount+1;
				}
				
				//即将逾期
				if (df_time.parse(a.getTaskDemandTime()).compareTo(df_time_hour.parse(df_time_hour.format(new Date().getTime()+24*3600000)))<=0) {
					taskJjgq = taskJjgq+1;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
				System.out.println(e);
			}
	 		
	 	});
	 	//map.put("rw", list);
	 	//获取派工单数据
	 	String dealType = type==0?"0":"1";
	 	Map<String, Object> pgdParmMap = new HashMap<String, Object>();
	 	pgdParmMap.put("userId", userId);
	 	pgdParmMap.put("type", dealType);
	 	List<HomePageEntity> list_pgd = homePageMapper.getPgdList(pgdParmMap);	
	 	list_pgd.stream().forEach(a->{
				try {
					Date jzrq;
					jzrq = df_time.parse(df_time_s.format(df_time_s.parse(a.getSjldjssj()).getTime()+Double.parseDouble(a.getJdbzsj())*3600000));
					System.out.println(a.getSjldjssj());
		 			System.out.println(df_time.format(jzrq));
		 			//如果是待办，计算截止日期是截止时间~当前时间,如果是未逾期，显示截止时间，截止时间-当前时间,如果是已逾期，显示已逾期，当前时间-截止时间
		 			//如果是已办，计算截止日期是截止时间~处理时间,如果是未逾期，显示截止时间，截止时间-流程处理时间，如果是已逾期，显示已逾期，处理时间-截止时间
		 			if (type == 0) {
						a.setIsOverTime(df_time_s.parse(df_time_s.format(df_time_s.parse(a.getSjldjssj()).getTime()+Double.parseDouble(a.getJdbzsj())*3600000)).compareTo(df_time_s.parse(df_time_s.format(new Date())))>=0?1:0);
						if (a.getIsOverTime() == 1) {
							//未逾期
							a.setCutoffTime(CZDateUtil.getDatePoor(jzrq,df_time.parse(df_time.format(new Date()))));
						}else if(a.getIsOverTime() == 0){
							//已逾期
							a.setCutoffTime(CZDateUtil.getDatePoor(df_time.parse(df_time.format(new Date())),jzrq));
						} 
					}else if(type == 1) {
						a.setIsOverTime(df_time_s.parse(df_time_s.format(df_time_s.parse(a.getSjldjssj()).getTime()+Double.parseDouble(a.getJdbzsj())*3600000)).compareTo(df_time_s.parse(a.getOperatedate()))>=0?1:0);
		 				if (a.getIsOverTime() == 1) {
							//未逾期
							a.setCutoffTime(CZDateUtil.getDatePoor(jzrq,df_time.parse(a.getOperatedate())));
						}else if(a.getIsOverTime() == 0){
							//已逾期
							a.setCutoffTime(CZDateUtil.getDatePoor(df_time.parse(a.getOperatedate()),jzrq));
						} 
					}
						//未逾期
						if (a.getIsOverTime() ==1) {
							pgdWyqCount = pgdWyqCount+1;
						}else if (a.getIsOverTime() ==0) {
							pgdYqCount = pgdYqCount+1; 
						}
						//即将逾期
						double pgdjzsj_double = df_time_s.parse(a.getSjldjssj()).getTime()+Double.parseDouble(a.getJdbzsj())*3600000;
						long pgdjzsj_long = new Double(pgdjzsj_double).longValue();
						String jzsj = df_time_s.format(new Date(pgdjzsj_long));
						if (df_time_s.parse(jzsj).compareTo(df_time_s.parse(df_time_s.format(new Date().getTime()+24*3600000)))<=0) {
							pgdJjgq = pgdJjgq+1;
						}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e);
				}
	 		
	 		
	 	});
	 	list.addAll(list_pgd);
	 	list.sort(Comparator.comparing(HomePageEntity::getReceivetime).reversed());
	 	map.put("all",list);
	 	map.put("isNotOverTimeCount",taskWyqCount+pgdWyqCount);
	 	map.put("isOverTimeCount",taskYqCount+pgdYqCount);
		map.put("isFallDue",taskJjgq+pgdJjgq);
	 	//获取报表阈值数据
	 	returnMap.put("code",SUCCESSCODE);
	 	returnMap.put("data",map);
	 	JSONObject jsonObj = new JSONObject(returnMap);
	 	taskWyqCount = 0;
	 	pgdWyqCount = 0;
	 	taskYqCount = 0;
	 	pgdYqCount = 0;
	 	taskJjgq = 0;
	 	pgdJjgq = 0;
		return JSONObject.toJSONString(jsonObj,SerializerFeature.WriteMapNullValue);
		} catch (Exception e2) {
			// TODO: handle exception
			taskWyqCount = 0;
		 	pgdWyqCount = 0;
		 	taskYqCount = 0;
		 	pgdYqCount = 0;
		 	taskJjgq = 0;
		 	pgdJjgq = 0;
		 	returnMap.put("code",FILEDCODE);
		 	return null;
		}
		
	}
	
	
	
	private List<Node> orgRecursion(List<Node> orgList, String pid) {
		List<Node> nodeList = new ArrayList<Node>();
		for (Node org : orgList) {
			if (org.getPid() != null) {
				//遍历出父id等于参数的id，add进子节点集合
				if (org.getPid().equals(pid)) {
					//递归遍历下一级
					orgRecursion(orgList, org.getId());
					//末级机构才添加进去(依自己业务定义)
					if (org.getChildren() == null) {
						nodeList.add(org);
					}
				}
			}
		}
		return nodeList;
	}
	/**
	 * 查询在职人员参数为空的时候查全量
	 * type=2的时候只能查询自己的下级
	 * 
	 * @return
	 */
	public Map<String, Object> queryAllPeople(String type, String keyword, String pageSize, String pageNum,Integer userId)
			throws Exception {
		Map<String, Object> parmMap = new HashMap<String, Object>();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		parmMap.put("keyword", keyword);
		parmMap.put("type", type);
		List<HrmEntity> list = new ArrayList<HrmEntity>(); 
		if ("0".equals(type)||"1".equals(type)) {
			list = homePageMapper.getAllPeople(parmMap);
		}else if("2".equals(type)){
			parmMap.clear();
			parmMap.put("keyword", keyword);
			parmMap.put("type", "1");
			list = homePageMapper.getAllPeople(parmMap);
			Map<String, Object> xjMap = this.queryOrganizationTree(userId, 1);
			List<String> organizationTree = new ArrayList<String>();
			List<Node> node_list = (List<Node>) xjMap.get("data");
			if (organizationTree == null ||organizationTree.isEmpty()) {
				
			}else {
			list.forEach(a->{
				organizationTree.forEach(b->{
					if (b.equals(a.getId())) {
						Map<String, Object> hrmMap = new HashMap<String, Object>();
						hrmMap.put("id", (!StringUtils.isEmpty(a.getId())) ? a.getId() : "");
						hrmMap.put("name", (!StringUtils.isEmpty(a.getName())) ? a.getName() : "");
						hrmMap.put("fb", (!StringUtils.isEmpty(a.getFb())) ? a.getFb() : "");
						hrmMap.put("bm", (!StringUtils.isEmpty(a.getBm())) ? a.getBm() : "");
						hrmMap.put("zw", (!StringUtils.isEmpty(a.getZw())) ? a.getZw() : "");
						listMap.add(hrmMap);
					}
				});
			});
			}
		}
		if (!type.equals("2")) {
			for (HrmEntity hrmModel : list) {
				Map<String, Object> hrmMap = new HashMap<String, Object>();
				hrmMap.put("id", (!StringUtils.isEmpty(hrmModel.getId())) ? hrmModel.getId() : "");
				hrmMap.put("name", (!StringUtils.isEmpty(hrmModel.getName())) ? hrmModel.getName() : "");
				hrmMap.put("fb", (!StringUtils.isEmpty(hrmModel.getFb())) ? hrmModel.getFb() : "");
				hrmMap.put("bm", (!StringUtils.isEmpty(hrmModel.getBm())) ? hrmModel.getBm() : "");
				hrmMap.put("zw", (!StringUtils.isEmpty(hrmModel.getZw())) ? hrmModel.getZw() : "");
				listMap.add(hrmMap);
			}
		}
			Map<String, Object> returnMap = new HashMap<String, Object>();
			List<Map<String, Object>> listPager = listMap.isEmpty()?listMap:Pager.getPage(listMap, pageSize, pageNum);
			returnMap.put("data", listPager);
			Map<String, Object> pageMap = new HashMap<String, Object>();
			pageMap.put("totalSize", list.size());
			pageMap.put("pageSize", pageSize);
			pageMap.put("pageNum", pageNum);
			returnMap.put("pageInfo", pageMap);

		return returnMap;
	}
	
	/**
	 * 获取所有人树
	 * @return
	 */
	public Map<String, Object> queryOrganizationTree(Integer userId,Integer type) throws Exception{
		//判断缓存中是否有数据，有就从缓存中取，没有就从数据库中取值
		String redis_organizationTree = redisService.get("organizationTree_"+userId+"_"+type);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if (redis_organizationTree !=null && !StringUtils.isEmpty(redis_organizationTree) && !("[]").equals(redis_organizationTree)) {
			Gson gson = new Gson();  
			String jsonStr = redisService.get("organizationTree_"+userId+"_"+type);
			List<Node> organizationTree = gson.fromJson(jsonStr, new TypeToken<List<Node>>(){}.getType()); 
			returnMap.put("data", organizationTree);
		}else {
		List<Node> copyList = new ArrayList<Node>();
		//通过userId获取企业微信的部门id
		List<Integer> list_dep= GetHrmMsg.gerWxDepId(userId.toString());
		Integer depId = list_dep.get(0);
		String depid = "";
		String rootId= "";
		if (userId == 730 || type == 0) {
			depid = "244";
			rootId = "2992682017";
		}else {
			depid = depId.toString();
			rootId = "244";
		}
		List<Map<String, Object>> list = GetHrmMsg.getDepartment(depid);
		for (int i = 0; i < list.size(); i++) {
			Node node = new Node();
			node.setId(String.valueOf(list.get(i).get("id")).trim());
			node.setPid(String.valueOf(list.get(i).get("parentid")).trim());
			node.setTitle((String) list.get(i).get("name"));
			node.setIsUser("0");
			copyList.add(node);
		}

		List<Node> personListSign = new ArrayList<Node>();
		List<Map<String, Object>> listPeople = GetHrmMsg.getHrmByDept(depid);
		for (int i = 0; i < listPeople.size(); i++) {
			Node node = new Node();
			node.setId(String.valueOf(listPeople.get(i).get("userid")).trim());
			JSONArray array = (JSONArray) listPeople.get(i).get("department");
			node.setPid(String.valueOf(array.get(0)).trim());
			node.setTitle(String.valueOf(listPeople.get(i).get("name")).trim());
			node.setIsUser("1");
			personListSign.add(node);
		}
		copyList.addAll(personListSign);
		List<Node> listMap = new ArrayList<Node>();
		listMap = NodeUtil.toTreeList(copyList, rootId);
		returnMap.put("data", listMap);
		Gson gson = new Gson();
		String jsonStr = gson.toJson(listMap);
		redisService.set("organizationTree_"+userId+"_"+type,jsonStr);
		//redisService.expire("organizationTree_"+userId+"_"+type, 60*60*60L);
		}
		return returnMap;
	}
	
	/**
	 * 获取当前登录人的直接下级
	 * @throws Exception 
	 */
	@Override
	public List<HrmEntity> queryBranchPeople(Integer userId) throws Exception {
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("userId", userId);
		List<HrmEntity> list = homePageMapper.queryBranchPeople(parmMap);
		return list;
	
	}

	/**
	 * 获取职级树
	 * 
	 * @return
	 */
	public Map<String, Object> getPmTree(Integer userId, Integer type) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			String redis_pmTree = redisService.get("pmTree_"+userId+"_"+type);
			if (redis_pmTree !=null && !StringUtils.isEmpty(redis_pmTree)) {
				Gson gson = new Gson();  
				String jsonStr = redisService.get("pmTree_"+userId+"_"+type);
				List<Node> pmTree = gson.fromJson(jsonStr, new TypeToken<List<Node>>(){}.getType()); 
				returnMap.put("data", pmTree);
			}else {
				List<Node> list1 = new ArrayList<Node>();
				List<Node> list2 = new ArrayList<Node>();
				Map<String, Object> parmQlMap = new HashMap<String, Object>();
				parmQlMap.put("userId", userId.toString());
				List<Node> treeList = new ArrayList<Node>();
				if (type == 0) {
					treeList= homePageMapper.queryPmTreeQl(parmQlMap);
				}else if(type == 1) {
					treeList= homePageMapper.queryPmTree(parmQlMap);
				}
				for (Node node : treeList) {
					if (type == 0) {
						if (node.getPid() == null || StringUtils.isEmpty(node.getPid())) {
							node.setPid("-1");
						} else {

						}
					} else if (type == 1) {
						if (node.getId().equals(userId.toString())) {
							node.setPid("-1");
							System.out.println("找到根节点");
						} else {

						}
					}
					list1.add(node);

				}
				list2 = NodeUtil.toTreeList(list1, "-1");
				Gson gson = new Gson();
				String jsonStr = gson.toJson(list2);
				redisService.set("pmTree_"+userId+"_"+type,jsonStr);
				//redisService.expire("pmTree_"+userId+"_"+type, 60*60*60L);
				returnMap.put("data", list2);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return returnMap;
		
	}

	public Map<String, Object> querySubordinate(Integer userId,String pageSize, String pageNum,String keyword)
			throws Exception {
			Map<String, Object> parmMap = new HashMap<String, Object>();
			parmMap.put("userId", userId);
			parmMap.put("keyword", keyword);
			List<HrmEntity> list = new ArrayList<HrmEntity>(); 
			list = homePageMapper.querySubordinate(parmMap);
			Map<String, Object> returnMap = new HashMap<String, Object>();
			List<HrmEntity> listPager = list.isEmpty()?list:Pager.getPage(list, pageSize, pageNum);
			returnMap.put("data", listPager);
			Map<String, Object> pageMap = new HashMap<String, Object>();
			pageMap.put("totalSize", list.size());
			pageMap.put("pageSize", pageSize);
			pageMap.put("pageNum", pageNum);
			returnMap.put("pageInfo", pageMap);

		return returnMap;
	} 

	
	
	public Map<String, Object> queryPmPeople(String pm,String pageSize,String pageNum)throws Exception{
		Map<String, Object> parmMap = new HashMap<String, Object>();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<String> pmList = new ArrayList<String>();
		if ((!StringUtils.isEmpty(pm))&&pm!=null) {
			pmList = Arrays.asList(pm.replace("[","").replace("]","").split(","));
		}
		parmMap.put("list", pmList);
		List<HrmEntity> list = new ArrayList<HrmEntity>(); 
		
			list = homePageMapper.getPmPeople(parmMap);
			list.forEach(a->{
						Map<String, Object> hrmMap = new HashMap<String, Object>();
						hrmMap.put("id", (!StringUtils.isEmpty(a.getId())) ? a.getId() : "");
						hrmMap.put("name", (!StringUtils.isEmpty(a.getName())) ? a.getName() : "");
						hrmMap.put("fb", (!StringUtils.isEmpty(a.getFb())) ? a.getFb() : "");
						hrmMap.put("bm", (!StringUtils.isEmpty(a.getBm())) ? a.getBm() : "");
						hrmMap.put("zw", (!StringUtils.isEmpty(a.getZw())) ? a.getZw() : "");
						listMap.add(hrmMap);
			});
			
			List<Map<String, Object>> listPager = listMap.isEmpty()?listMap:Pager.getPage(listMap, pageSize, pageNum);
			returnMap.put("data", listPager);
			Map<String, Object> pageMap = new HashMap<String, Object>();
			pageMap.put("totalSize", list.size());
			pageMap.put("pageSize", pageSize);
			pageMap.put("pageNum", pageNum);
			returnMap.put("pageInfo", pageMap);

			return returnMap;
	}
	
}
