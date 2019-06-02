package com.sicau.tiredsys.controller;

import com.sicau.tiredsys.common.ResponseResult;
import com.sicau.tiredsys.common.WebSocketServer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.websocket.EncodeException;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


/**
 * Created by zhong  on 2019/5/14 16:55
 */
@ServerEndpoint(value = "/client/{userId}")
@Component
public class WebSocketController {

    //页面请求
    @GetMapping("/socket/{cid}")
    public ModelAndView socket(@PathVariable String cid) {
        ModelAndView mav = new ModelAndView("/socket");
        mav.addObject("cid", cid);
        return mav;
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

