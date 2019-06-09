package com.sicau.tiredsys.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.sicau.tiredsys.common.Const;
import com.sicau.tiredsys.common.WebSocketServer;
import com.sicau.tiredsys.dao.HistoryDao;
import com.sicau.tiredsys.entity.History;
import com.sicau.tiredsys.utils.RequestClient;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhong  on 2019/6/9 19:01
 */
@Service
public class BusinessService {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    WebSocketServer webSocketServer;
    @Autowired
    HistoryDao historyDao;

    @Async(value="ThreadPool")
    public void dealVideo(String openid,String fileName) throws IOException, EncodeException {
         // webSocketServer.sendMessageByOpenid(openid,msg);
        Map<String, String> map = new HashMap<String,String>();
        map.put("path", fileName);
        String results = RequestClient.httpRequestToString("http://127.0.0.1:5000/checkVideo", "get", map, null);
        HashMap<String,Object> jsonResults = (HashMap) JSONUtils.parse(results);
        String status =(String) jsonResults.get("status");
        Integer eyeopen = Integer.valueOf((String)jsonResults.get("eyeopen"));
        Integer eyeclose = Integer.valueOf((String)jsonResults.get("eyeclose"));
        History history = new History();
        history.setStatus(status);
        history.setEyeopen(eyeopen);
        history.setEyeclose(eyeclose);
        history.setOpenid(openid);
        if (historyDao.addHistory(history)>0){
            webSocketServer.sendMessageByOpenid(openid,status);
        }


    }

    @Async(value="ThreadPool")
    public void uploadVideo( MultipartFile files){

    }

}
