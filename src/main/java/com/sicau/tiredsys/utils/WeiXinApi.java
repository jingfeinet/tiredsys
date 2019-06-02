package com.sicau.tiredsys.utils;

import com.alibaba.druid.support.json.JSONUtils;
import com.sicau.tiredsys.common.Const;
import sun.plugin2.util.SystemUtil;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhong  on 2019/5/10 20:12
 */
public class WeiXinApi {
    private static String ACCESS_TOKEN;
    private static Timer timer = new Timer(true);

    @PostConstruct
    public static void setToken() {
        if (null == timer) {
            timer = new Timer(true);
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    HashMap params = new HashMap();
                    params.put("grant_type","client_credential");
                    params.put("appid", Const.AppID);
                    params.put("secret",Const.AppSecret);
                    String result = RequestClient.httpRequestToString(Const.GET_ACCESS_TOKEN,"get",params);
                    HashMap resultMap = (HashMap) JSONUtils.parse(result);
                    String access_token = (String)resultMap.get("access_token");
                    ACCESS_TOKEN = access_token;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2*3600 * 1000);
    }

    public static String getAccessToken(){
        return ACCESS_TOKEN;
    }
}
