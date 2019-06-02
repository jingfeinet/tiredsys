package com.sicau.tiredsys.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.shiro.authc.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by zhong  on 2019/3/14 13:20
 */
public class JWTUtil {
    //    过期时间 秒
    private static final long EXPIRE_TIME = 5 *3600* 1000;

    private static final String USER_ID = "userId";
    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String userId, String secret){
        try {
            //根据密码生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim(USER_ID, userId)
                    .build();
            //效验TOKEN
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            // throw new MyException(-1,"token错误","账号或密码错误");
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getOpenid(String token)  throws AuthenticationException {
        if (token==null||token=="")
            throw new AuthenticationException("token错误");
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USER_ID).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名,5min后过期
     *
     * @param openid 用户名
     * @param secret   用户的密码
     * @return 加密的token
     */
    public static String sign(String openid, String secret) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带username信息
        return JWT.create()
                .withClaim(USER_ID, openid)
                .withExpiresAt(date)
                .sign(algorithm);

    }

    public static String getTokenOpenid(HttpServletRequest request){
        String token = request.getHeader("token");
        String openid =  getOpenid(token);
        return openid;
    }
}
