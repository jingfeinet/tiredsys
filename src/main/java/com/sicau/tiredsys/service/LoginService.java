package com.sicau.tiredsys.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.sicau.tiredsys.common.Const;
import com.sicau.tiredsys.common.ResponseCode;
import com.sicau.tiredsys.common.ResponseResult;
import com.sicau.tiredsys.dao.UserDao;
import com.sicau.tiredsys.entity.User;
import com.sicau.tiredsys.utils.JWTUtil;
import com.sicau.tiredsys.utils.RequestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by zhong  on 2019/5/16 21:45
 */
@Service
public class LoginService {
    @Autowired
    UserDao userDao;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    //获取openid后默认注册
    public String getOpenid(String code){
        HashMap params = new HashMap();
        params.put("appid", Const.AppID);
        params.put("secret", Const.AppSecret);
        params.put("js_code", code);
        String str = RequestClient.httpRequestToString(Const.GET_OPENID,
                "get", params);
        HashMap<String, String> result = (HashMap) JSONUtils.parse(str);
        String openid = result.get("openid");
        if (openid!=null){
           User user =  userDao.getUserByOpenId(openid);
           if (user==null){
               user = new User();
               user.setOpenid(openid);
               user.setRole(Const.INIT_ROLE);
               user.setFingerprint(0);//默认关闭指纹解锁
               userDao.addUser(user);
           }
        }
        return openid;
    }


    public HashMap login(User user1){

        String openid = user1.getOpenid();
        String password = "";
        HashMap result = new HashMap();
        boolean success = true;
        User user = userDao.getUserByOpenId(openid);
        if (user == null) {
            success = false;
            result.put("success",success);
            result.put("msg","openid错误,请退出重新进入");
            return result;
        }
        password = user.getPassword();
        if (password == null||"".equals(password)) {  //密码不存在则生成随机密码
            password = UUID.randomUUID().toString().replaceAll("-","");
         } else {
          success = false;
          result.put("success",success);
          result.put("msg","请通过密码验证");
          return result;
        }
        //密码不存在，直接返回token
        userDao.updateUser(user1);
        user.setPassword(password);
        user.setAvatarUrl(user1.getAvatarUrl());
        user.setUserName(user1.getUserName());
        redisTemplate.opsForHash().put(openid, "user", user);
        String token = JWTUtil.sign(openid, password);
        redisTemplate.opsForHash().put(openid,"token",token);
        result.put("success",success);
        result.put("token",token);
        return result;
    }
}
