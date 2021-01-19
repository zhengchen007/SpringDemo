package com.olo.ding.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import com.olo.ding.utils.WeChatCorpInfo;

public class AccessTokenTool {
    private static Logger log = Logger.getLogger(AccessTokenTool.class);
    private static String accessToken = "";
    private static long tokenTime = -1L;

    public static String getToken() {
        if (-1L == tokenTime || System.currentTimeMillis() - tokenTime >= 7188000L) {
            refreshToken();
        }
        return accessToken;
    }

    public static String getToken(boolean force) {
        refreshToken();
        return accessToken;
    }

    private static void refreshToken() {
        String getTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + WeChatCorpInfo.CORPID + "&corpsecret=" + WeChatCorpInfo.SECRET;
        try {
            JSONObject received = JSON.parseObject(HttpRequester.requestGET(getTokenUrl));
            if (0 == received.getInteger("errcode")) {
                accessToken = received.getString("access_token");
                tokenTime = System.currentTimeMillis();
                log.info("access_token刷新：" + accessToken);
            }
        } catch (Exception ignored) {
        }
    }
}
