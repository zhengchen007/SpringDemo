package com.olo.ding.controller;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.olo.ding.entity.TaskDetailEntity;
import com.olo.ding.mapper.TaskDistributeMapper;
import com.olo.ding.utils.JwtUtil;
import com.olo.ding.utils.WeChatCorpInfo;
import com.olo.ding.utils.WeChatService;


@Controller
@RequestMapping("/login")
public class LoginController {
	private WeChatService	weChatService;

	@Autowired
	public LoginController(WeChatService weChatService) {
		this.weChatService = weChatService;
	}

	@Autowired
	private TaskDistributeMapper taskDistributeMapper;

	@RequestMapping(method = RequestMethod.GET, produces = "test/json;charset=UTF-8")
	public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userAgent = request.getHeader("user-agent");
		System.out.println("userAgent" + userAgent);

		String code = request.getParameter("code");
		String userId = weChatService.getWeChatUserInfo(code);

		if (userAgent.contains("wxwork") && userAgent.contains("MicroMessenger") || true) {
			System.out.println("userId==="+userId);
			if (StringUtils.isEmpty(userId)) { // 
				String token = JwtUtil.sign(userId);
				Map<String, Object> parmMap = new HashMap<String, Object>();
				parmMap.put("id", userId);
				List<TaskDetailEntity> userNameList = taskDistributeMapper.getPeopleName(parmMap);
				String userName = userNameList.get(0).getLastname();
				userName = java.net.URLEncoder.encode(userName, "UTF-8");
				System.out.println("anonymousUser and url="+ "redirect:http://crmmobile1.olo-home.com:8038/calendar/#/?userId=" + userId
						+ "&code=200&userName=" + userName + "&acsesstoken=" + token);
				return "redirect:http://crmmobile1.olo-home.com:8038/calendar/#/?userId=" + userId
						+ "&code=200&userName=" + userName + "&acsesstoken=" + token;

			} else {
				// 认证
				String token = JwtUtil.sign(userId);
				System.out.println("userAgent" + userAgent);
				Map<String, Object> parmMap = new HashMap<String, Object>();
				parmMap.put("id", userId);
				List<TaskDetailEntity> userNameList = taskDistributeMapper.getPeopleName(parmMap);
				String userName = userNameList.get(0).getLastname();
				System.out.println(userName);
				System.out.println(
						"redirect:crmmobile1.olo-home.com:8038/calendar/#/?userId=" + userId + "&code=200&userName="
								+ URLEncoder.encode(userName.toString(), "UTF-8") + "&acsesstoken=" + token);
				userName = java.net.URLEncoder.encode(userName, "UTF-8");
				System.out.println("userid and url="+"redirect:http://crmmobile1.olo-home.com:8038/calendar/#/?userId=" + userId
						+ "&code=200&userName=" + userName + "&acsesstoken=" + token);
				return "redirect:http://crmmobile1.olo-home.com:8038/calendar/#/?userId=" + userId
						+ "&code=200&userName=" + userName + "&acsesstoken=" + token;

			}
		} else {
			// return "openInWeChatWarning";
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap.put("code", "500");
			String jsonStr = JSONObject.toJSONString(returnMap);
			return jsonStr;
		}
	}

	@RequestMapping(value = "getAuth", method = RequestMethod.GET, produces = "test/json;charset=UTF-8")
	@ResponseBody
	public String getAuth(@RequestParam(value = "userId") String userId, HttpServletResponse response)
			throws UnsupportedEncodingException {
		String token = JwtUtil.sign(userId);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("id", userId);
		List<TaskDetailEntity> userNameList = taskDistributeMapper.getPeopleName(parmMap);
		String userName = userNameList.get(0).getLastname();
		returnMap.put("code", "200");
		returnMap.put("userId", userId);
		returnMap.put("userName", userName);
		returnMap.put("acsesstoken", token);
		userName = java.net.URLEncoder.encode(userName, "UTF-8");
		String jsonStr = JSON.toJSONString(returnMap);
		return jsonStr;
	}


}
