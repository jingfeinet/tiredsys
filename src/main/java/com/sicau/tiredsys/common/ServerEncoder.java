package com.sicau.tiredsys.common;

import net.sf.json.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by zhong  on 2019/5/15 16:41
 */
public class ServerEncoder implements Encoder.Text<ResponseResult>{
    @Override
    public String encode(ResponseResult responseResult) throws EncodeException {
        return JSONObject.fromObject(responseResult).toString();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
