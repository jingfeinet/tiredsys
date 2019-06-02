package com.sicau.tiredsys.config;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Created by zhong  on 2019/3/14 13:40
 */
public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
