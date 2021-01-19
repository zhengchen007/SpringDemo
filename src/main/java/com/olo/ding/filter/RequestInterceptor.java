package com.olo.ding.filter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.olo.ding.utils.JwtUtil;


public class RequestInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, // 2

			HttpServletResponse response, Object handler) throws Exception {

		try {

			String ip = request.getRemoteAddr();
			System.out.println("当前登陆ip为====" + ip);

			
			  if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1") ||
			  ip.equals("10.100.0.2") || ("180.101.230.205").equals(ip) ||
			  "114.222.185.210".equals(ip) || "180.111.132.67".equals(ip) ||"10.201.255.23".equals(ip) ||
			  "61.132.53.203".equals(ip) ||"10.201.2.231".equals(ip)||"10.201.255.113".equals(ip)||"10.201.0.65".equals(ip)) { return true; }
			 

			String url = request.getRequestURI();
			System.out.println("url===" + url);
			Map<String, String[]> params = request.getParameterMap();
			String token = "";
			Iterator<Entry<String, String[]>> it = params.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String[]> entry = it.next();
				System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
				if ("acsesstoken".equals(entry.getKey())) {
					token = entry.getValue()[0];
				}
				System.out.println("acsesstoken=" + token);
			}
			if ("/homePage/queryBranchTree".equals(url) || "/oloDingDing/homePage/getAuth".equals(url)
					|| "/oloDingDing/login".equals(url) || "/taskDistribute/missionUpload".equals(url)||"/px/test".equals(url)
					||"/oloDingDing/cycleTask/getCycleTaskList".equals(url)|| "/oloDingDing/cycleTask/getCycleTaskDetail".equals(url)
					||"/oloDingDing/dealTask/executorFileUpload".equals(url)||"/oloDingDing/taskDistribute/addZqTask".equals(url)
					||"/oloDingDing/taskDistribute/stopZqTask".equals(url)||"/oloDingDing/taskDistribute/stopZqTask".equals(url)
					||"/oloDingDing/taskDistribute/addZqTaskByHand".equals(url)
					) {
				return true;
			} else {
				if (JwtUtil.verify(token)) {
					return true;
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("code", "501");
					map.put("message", "login filed!");
					String jsonStr = JSON.toJSONString(map);
					PrintWriter out = null;
					out = response.getWriter();
					out.append(jsonStr);
					return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("拦截器报错===" + e);
		}
		return false;
	}

	@Override

	public void postHandle(HttpServletRequest request, // 3

			HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		/*
		 * long startTime = (Long) request.getAttribute("startTime");
		 * 
		 * request.removeAttribute("startTime");
		 * 
		 * long endTime = System.currentTimeMillis();
		 * 
		 * System.out.println("响应时间为==="+ new Long(endTime - startTime) + "ms");
		 * 
		 * request.setAttribute("handlingTime", endTime - startTime);
		 */

	}

}
