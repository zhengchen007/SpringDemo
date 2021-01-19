package com.olo.ding.utils;

import java.sql.Array;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



public class GetHrmMsg {
	
	public static String			accessToken	= "";
	public static String			expires_in	= "";										// token有效时间：7200秒(2小时)
	public static long				time		= 0;
	
	
	/**
	 * 获取所有部门
	 * 
	 * @param id
	 *            244为总部
	 * @return
	 */
	public static List<Map<String, Object>> getDepartment(String id) {
		getToken();
		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=" + accessToken + "&id=" + id;
		JSONArray arr = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse rp = null;
		try {
			rp = httpClient.execute(httpget);
			if (rp.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				String conResult = EntityUtils.toString(rp.getEntity());
				System.out.println("conResult:" + conResult);
				JSONObject sobj = new JSONObject();	
				sobj = sobj.fromObject(conResult);
				String name = sobj.getString("department");// 部门名称
				arr = JSONArray.fromObject(name);
			} else {
				String err = rp.getStatusLine().getStatusCode() + "";
			}
		} catch (Exception e) {
		}
		List<Map<String, Object>> list = arr;
		return list;
	}
	
	
	public static void getToken() {

		// 未获取token时调用接口获取token
		if ("".equals(accessToken) && "".equals(expires_in)) {
			// bb.writeLog("startgetToken");
			String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CORPID&corpsecret=CORPSECRET";
			String last_wechat_url = url.replace("CORPID", "wxd14b79941294f63b").replace("CORPSECRET",
					"xFRrNSPj-4KfB6DExnp0LsdBrlPbLxdcGKqGjBHT7bMFa2lS61E4l4Wdvvtu7g1v");
			// 调用接口查询token
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(last_wechat_url);
			HttpResponse rp = null;
			try {
				rp = httpClient.execute(httpget);
				if (rp.getStatusLine().getStatusCode() == 200) {
					/* 读返回数据 */
					String conResult = EntityUtils.toString(rp.getEntity());
					JSONObject sobj = new JSONObject();
					sobj = sobj.fromObject(conResult);
					accessToken = sobj.getString("access_token");
					expires_in = sobj.getString("expires_in");
					time = new Date().getTime();
					// bb.writeLog("accessToken:"+accessToken);
					// bb.writeLog("expires_in:"+expires_in);
				} else {
					String err = rp.getStatusLine().getStatusCode() + "";
					System.out.println("$$$err="+err);
				}
			} catch (Exception e) {
			}
		} else// token存在时判断token是否有效，无效则重新获取，有效则使用原有token
		{
			long curtime = new Date().getTime();
			long subtime = curtime - time;
			if (subtime > 7000000) {
				// 重新获取token
				// bb.writeLog("startgetTokenAgain");
				String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CORPID&corpsecret=CORPSECRET";
				String last_wechat_url = url.replace("CORPID", "wxd14b79941294f63b").replace("CORPSECRET",
						"xFRrNSPj-4KfB6DExnp0LsdBrlPbLxdcGKqGjBHT7bMFa2lS61E4l4Wdvvtu7g1v");
				// 调用接口查询token
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(last_wechat_url);
				HttpResponse rp = null;
				try {
					rp = httpClient.execute(httpget);
					if (rp.getStatusLine().getStatusCode() == 200) {
						/* 读返回数据 */
						String conResult = EntityUtils.toString(rp.getEntity());
						JSONObject sobj = new JSONObject();
						sobj = sobj.fromObject(conResult);
						accessToken = sobj.getString("access_token");
						expires_in = sobj.getString("expires_in");
						time = new Date().getTime();
						// bb.writeLog("accessToken:"+accessToken);
						// bb.writeLog("expires_in:"+expires_in);
					} else {
						String err = rp.getStatusLine().getStatusCode() + "";
						System.out.println("$$$err="+err);
					}
				} catch (Exception e) {
				}
			}
		}
		
	}
	
	/**
	 * 获取部门下所有人员不包括职级
	 * 
	 * @param deptid
	 *            244为总部
	 * @return
	 */
	// 递归获取部门成员
	public static List<Map<String, Object>> getHrmByDept(String deptid) {
		getToken();
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=" + accessToken + "&department_id="
				+ deptid + "&fetch_child=1";// 递归获取子部门下成员
		JSONArray arr = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse rp = null;
		try {
			rp = httpClient.execute(httpget);
			if (rp.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				String conResult = EntityUtils.toString(rp.getEntity());
				JSONObject sobj = new JSONObject();
				sobj = sobj.fromObject(conResult);
				String name = sobj.getString("userlist");// 用户列表
				arr = JSONArray.fromObject(name);
			} else {
				String err = rp.getStatusLine().getStatusCode() + "";
			}
		} catch (Exception e) {
		}
		List<Map<String, Object>> listObjectFir = (List<Map<String, Object>>) arr;
		return listObjectFir;
	}
	
	public static List<Integer> gerWxDepId(String userId) {
		getToken();
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token="+accessToken+"&userid="+userId;
		JSONArray arr = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse rp = null;
		try {
			rp = httpClient.execute(httpget);
			if (rp.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				String conResult = EntityUtils.toString(rp.getEntity());
				JSONObject sobj = new JSONObject();
				sobj = sobj.fromObject(conResult);
				String name = sobj.getString("department");// 用户列表
				arr = JSONArray.fromObject(name);
			} else {
				String err = rp.getStatusLine().getStatusCode() + "";
			}
		} catch (Exception e) {
		}
		List<Integer> listObjectFir = (List<Integer>) arr;
		return listObjectFir;
	}
	
	public static void main(String[] args) {
		
		//System.out.println(gerWxDepId("730").toString());
		System.out.println(getDepartment("1").toString());
	}
}	
