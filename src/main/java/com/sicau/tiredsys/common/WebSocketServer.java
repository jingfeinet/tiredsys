package com.sicau.tiredsys.common;

/**
 * Created by zhong  on 2019/5/14 17:09
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.druid.support.json.JSONUtils;
import com.sicau.tiredsys.utils.JWTUtil;
import com.sicau.tiredsys.utils.RequestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;



@ServerEndpoint(value = "/websocket/{sid}",encoders = { ServerEncoder.class })
@Component
public class WebSocketServer {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    private static ConcurrentHashMap<String,String> sessionMap = new ConcurrentHashMap();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid = "";

    //定时任务
    private static Timer timer = new Timer(true);

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws EncodeException {
        this.session = session;
        String sessionId =  session.getId();
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新窗口开始监听:" + sid + ",当前在线人数为" + getOnlineCount());
    //        redisTemplate.opsForHash().put(Const.WEB_SOCKET,sessionId,sid);
        //sessionMap.put(sessionId,sid);
        this.sid = sid;
        try {
            sendMessage(ResponseResult.createBySuccessMessage("连接成功"));
            sendMessage(ResponseResult.createBySuccess(sessionId));
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws EncodeException {
        System.out.println("收到来自窗口" + sid + "的信息:" + message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                if (item.session == session){
                    item.sendMessage(ResponseResult.createBySuccess(message));
                    if (message.indexOf("token=")>=0){
                        //说明发送的是token信息
                        //将sessionId与openid匹配
                        String sessionId = session.getId();
                        String token = message.split("token=")[1];
                        String openid;
                        try{
                           openid =  JWTUtil.getOpenid(token);
                           sessionMap.put(openid,sessionId);
                        }catch (Exception e){
                            item.sendMessage(ResponseResult.createByErrorMessage("token有错"));
                        }

                    }
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public  void sendMessage(Object message) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(message);
    }

    public void sendMessageByOpenid(String openid,Object object) throws IOException, EncodeException {
        String sessionId = sessionMap.get(openid);
        if (sessionId==null){
            //异常则什么都不做
        }else {
            for(WebSocketServer item:webSocketSet){
                if (item.session.getId()==sessionId){
                    item.sendMessage(ResponseResult.createBySuccess("msg",object));
                }
            }
        }
    }


    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message, @PathParam("sid") String sid) throws IOException, EncodeException {
        System.out.println("推送消息到窗口" + sid + "，推送内容:" + message);
        for (WebSocketServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                System.out.println(item);
                if (sid == null) {
                    item.sendMessage(ResponseResult.createBySuccess(message));
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(ResponseResult.createBySuccess(message));
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    @PostConstruct
    public static void sendAllMessage(){
        if (null == timer) {
            timer = new Timer(true);
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                   sendInfo("hello",null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 20*60* 1000);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public String getSessionId(String openid){
        return sessionMap.get(openid);
    }
}

