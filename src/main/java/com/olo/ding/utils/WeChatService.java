package com.olo.ding.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class WeChatService {
    private static Logger log = Logger.getLogger(WeChatService.class);

    public String getWeChatUserInfo(String code) {
        String accessToken = AccessTokenTool.getToken();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token="
                + accessToken + "&code=" + code;
        String userId = "-1";
        try {
            JSONObject output = JSON.parseObject(HttpRequester.requestGET(url));
            if (0 == output.getInteger("errcode")) {
                userId = output.getString("UserId");
            } else {
                log.error(output.toJSONString());
            }
        } catch (Exception e) {
            log.error(e);
        }
        return userId;
    }
    
    public static String newSendGet(String userId ,String text) {
   	 String lines = "";
           try {	        	   
          	   //String url = "http://model.olo-home.com:8090/olosupprot/sendweixinmess.do";
          	   String url = "http://192.168.2.244:8080/oloxservices/sendweixinmess.do";
          	   text = URLEncoder.encode(text, "utf-8");
             	   String param = "user="+userId+"&text="+text;
   	           String urlNameString = url + "?" + param;
   	           URL realUrl = new URL(urlNameString);
   	           // 打开和URL之间的连接
   	           URLConnection connection = realUrl.openConnection();
   	           // 建立实际的连接
   	           connection.connect();
   	           // 获取所有响应头字段
   	           connection.getHeaderFields();
   	           
   	           BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));//设置编码,否则中文乱码
   		         
   	           lines = reader.readLine();
   	           reader.close();
   	           
            } catch (Exception e) {
   	           System.out.println("发送GET请求出现异常！" + e);
   	           e.printStackTrace();
            } 
           
           return lines;
           
   }
}
