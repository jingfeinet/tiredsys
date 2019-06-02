package com.sicau.tiredsys.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.sicau.tiredsys.common.Const;
import com.sicau.tiredsys.common.ResponseCode;
import com.sicau.tiredsys.common.ResponseResult;
import com.sicau.tiredsys.dao.UserDao;
import com.sicau.tiredsys.entity.User;
import com.sicau.tiredsys.service.LoginService;
import com.sicau.tiredsys.utils.JWTUtil;
import com.sicau.tiredsys.utils.RequestClient;
import com.sicau.tiredsys.utils.WeiXinApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.UUID;


@RestController
@Api(value = "LoginAPIController", description = "登录接口")
public class LoginAPIController {

    @Autowired
    UserDao userDao;
    @Autowired
    LoginService loginService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    @ApiOperation(value = "获取openid")
    @ApiImplicitParam(paramType = "query", value = "微信小程序登录接口", name = "code", dataType = "string")
    @GetMapping("/openid")
    public ResponseResult getOpenid(String code) {
        if (code == null) {
            return ResponseResult.createByErrorMessage("code不能为空");
        }
        String openid = loginService.getOpenid(code);
        if (openid == null) {
            return ResponseResult.createByErrorMessage("code不能重复验证");
        }
        return ResponseResult.createBySuccess(openid);
    }


    //第一次登录时就保存最初的头像和用户名
    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "用户id", name = "openid", dataType = "string"),
            @ApiImplicitParam(paramType = "query", value = "用户名", name = "userName", dataType = "string"),
            @ApiImplicitParam(paramType = "query", value = "用户头像", name = "avatarUrl", dataType = "string")
    })
    @PostMapping("/login")
    public ResponseResult login(@NotNull String openid,@NotNull String userName,@NotNull String avatarUrl){
        if (openid==null||userName==null||avatarUrl==null){
            return ResponseResult.createByErrorMessage("参数不能为空");
        }
        User user = new User();
        user.setOpenid(openid);
        user.setUserName(userName);
        user.setAvatarUrl(avatarUrl);
        HashMap result = loginService.login(user);
        if ((boolean)result.get("success"))
            return ResponseResult.createBySuccess(result.get("token"));
        else
            return ResponseResult.createByErrorMessage((String)result.get("msg"));
    }

    @ApiOperation(value = "密码验证接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "微信小程序密码验证接口", name = "openid", dataType = "string", required = true),
            @ApiImplicitParam(paramType = "query", value = "MD5加密密码", name = "password", dataType = "string", required = true)
    })

    @PostMapping("/loginBypassword")
    public ResponseResult loginByPassword(String openid, String password) {
        User user0 = new User();
        user0.setPassword(password);
        user0.setOpenid(openid);
        HashMap map = new HashMap();
        User user = userDao.getUserByPassword(user0);
        if (user == null) {
            return ResponseResult.createByErrorMessage("openid或密码有错");
        } else {
            redisTemplate.opsForHash().put(openid, "userInfo", user);
            String token = JWTUtil.sign(openid, password);
            map.put("token", token);
            redisTemplate.opsForHash().put(openid,"token",token); //存入token，以致后面每次验证时直接比对缓存里的token，也能实现单点登录
            return ResponseResult.createBySuccess(token);
        }
    }


    @ApiOperation(value = "指纹登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "微信应用唯一标识用户", name = "openid", dataType = "string", required = true),
            @ApiImplicitParam(paramType = "query", value = "指纹验证结果", name = "resultJSON", dataType = "string", required = true),
            @ApiImplicitParam(paramType = "query", value = "加密的结果", name = "resultJSONSignature", dataType = "string", required = true)
    })

    @PostMapping("/loginByFingerprint")
    public ResponseResult loginByFingerprint(String openid, String resultJSON, String resultJSONSignature) {
        try {

            System.out.println(resultJSON);
            System.out.println(openid);
            HashMap verifyParams = new HashMap();
            verifyParams.put("openid", openid);
            verifyParams.put("json_string", resultJSON);
            verifyParams.put("json_signature", resultJSONSignature);
            System.out.println(WeiXinApi.getAccessToken());
            String result = RequestClient.httpRequestToString(Const.POST_Verify_Signature + WeiXinApi.getAccessToken(),
                    "post", verifyParams);
            HashMap resultMap = (HashMap) JSONUtils.parse(result);
            Boolean is_ok = (Boolean) resultMap.get("is_ok");
            if (is_ok == null) {
                String errmsg = (String) resultMap.get("errmsg");
                if (errmsg == null)
                    return ResponseResult.createByErrorMessage("服务器异常");
                else return ResponseResult.createByErrorMessage(errmsg);
            } else {
                if (is_ok == true) {
                    return ResponseResult.createBySuccessMessage("验证成功");
                } else return ResponseResult.createByErrorCodeMessage(ResponseCode.Unauthorized.getCode(), "验证失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}




