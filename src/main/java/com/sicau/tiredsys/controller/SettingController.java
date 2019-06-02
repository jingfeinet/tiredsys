package com.sicau.tiredsys.controller;

import com.sicau.tiredsys.common.ResponseCode;
import com.sicau.tiredsys.common.ResponseResult;
import com.sicau.tiredsys.dao.UserDao;
import com.sicau.tiredsys.entity.User;
import com.sicau.tiredsys.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * Created by zhong  on 2019/5/11 23:21
 */
@RestController
@Api(value = "SettingController", description = "小程序设置接口")
public class SettingController {

    @Autowired
    UserDao userDao;

    @ApiOperation(value = "判断是否开启密码锁和指纹锁")
    @ApiImplicitParam(value = "openid", name = "openid", required = true, dataType = "string", paramType = "query")
    @GetMapping("/password")
    public ResponseResult getPassword(String openid) {
        if (openid == null) {
            return ResponseResult.createByErrorMessage("openid不存在");
        }
        User user = userDao.getUserByOpenId(openid);
        boolean openPassword = false;
        boolean openFinger = false;
        if (user != null) {
            if (user.getPassword() != null && !user.getPassword().equals("")) {
                openPassword = true;
            }
            openFinger = user.getFingerprint() == 0 ? false : true;
        }
        HashMap map = new HashMap();
        map.put("openPassword", openPassword);
        map.put("openFinger", openFinger);
        return ResponseResult.createBySuccess(map);
    }


    //开启密码锁后不会立即生效，应当在退出或token过期后才生效
    @ApiOperation(value = "密码设置")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "type", value = "设置密码类型（纯密码或者开启指纹）", dataType = "string", required = true),
            @ApiImplicitParam(paramType = "query", name = "password", value = "哈希密码", dataType = "string"),
            @ApiImplicitParam(paramType = "query", name = "isOpen", value = "是否开启密码或指纹", dataType = "int", required = true)
    })
    @PostMapping("/setPassword")
    public ResponseResult setPassword(@NotNull String type, String password, @NotNull Integer isOpen, HttpServletRequest request) {
        String openid = JWTUtil.getTokenOpenid(request);
        if (!"password".equals(type) && !"fingerprint".equals(type)) {
            return ResponseResult.createByErrorMessage("类型错误");
        }
        if (isOpen == null && isOpen != 0 && isOpen != 1) { //开关不是0 1
            return ResponseResult.createByErrorMessage("是否开启参数异常");
        }
        if (isOpen == 1) {
            //sha1密码不为40位获取空则报错

            User user = new User();
            user.setOpenid(openid);
            user.setPassword(password);
            int flag = userDao.updatePassword(user);
            if (type.equals("password")) { //只设置了密码
                if (password == null || "".equals(password.trim()) || password.length() != 40) {
                    return ResponseResult.createByErrorMessage("哈希密码不符合要求");
                }
                if (flag > 0)
                    return ResponseResult.createBySuccessMessage("密码锁设置成功");
                else
                    return ResponseResult.createByErrorMessage("密码锁开启失败");
            } else {  //开启了指纹
                user.setFingerprint(isOpen);
                if (flag > 0 && userDao.updateFingerprint(user) > 0)
                    return ResponseResult.createBySuccessMessage("指纹开启成功");
                else return ResponseResult.createByErrorMessage("指纹开启失败");
            }

        } else {
            User user = new User();
            user.setFingerprint(isOpen);
            user.setOpenid(openid);
            if (type.equals("password")) { //关闭密码，则同时取消指纹
                user.setPassword(null);
                if (userDao.updatePassword(user) > 0)
                    return ResponseResult.createBySuccessMessage("密码锁和指纹关闭成功");
                else return ResponseResult.createByErrorMessage("设置异常");
            } else { //只关闭指纹
                if (userDao.updateFingerprint(user) > 0)
                    return ResponseResult.createBySuccessMessage("指纹关闭成功");
                else return ResponseResult.createByErrorMessage("指纹关闭异常");
            }
        }
    }
}
