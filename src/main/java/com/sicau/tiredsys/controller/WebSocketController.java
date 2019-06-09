package com.sicau.tiredsys.controller;

import com.sicau.tiredsys.common.ResponseResult;
import com.sicau.tiredsys.common.WebSocketServer;
import com.sicau.tiredsys.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.EncodeException;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


/**
 * Created by zhong  on 2019/5/14 16:55
 */
@RestController
@Api(value = "websocket连接控制层")
public class WebSocketController {

    //页面请求
    @GetMapping("/socket/{cid}")
    public ModelAndView socket(@PathVariable String cid) {
        ModelAndView mav = new ModelAndView("/socket");
        mav.addObject("cid", cid);
        return mav;
    }

    @ApiOperation(value ="websocket认证" )
    @PostMapping("/socket/check")
    public ResponseResult checkSocket(HttpServletRequest request){
        String openid = JWTUtil.getTokenOpenid(request);
        return null;
    }

    //推送数据接口
    @ResponseBody
    @RequestMapping("/socket/push/{cid}")
    public ResponseResult pushToWeb(@PathVariable String cid, String message) throws EncodeException {
        try {
            WebSocketServer.sendInfo(message, cid);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.createByErrorMessage(cid + "#" + e.getMessage());
        }
        return ResponseResult.createBySuccessMessage(cid);
    }
}

