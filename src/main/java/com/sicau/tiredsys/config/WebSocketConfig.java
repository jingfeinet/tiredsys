package com.sicau.tiredsys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Created by zhong  on 2019/5/14 16:28
 */
@Configuration
public class WebSocketConfig {
    @Bean    //不注释会报@ServerEndpoint注解无法注册
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
