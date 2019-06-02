package com.sicau.tiredsys.common.test;

import com.sicau.tiredsys.utils.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;

/**
 * Created by zhong  on 2019/5/12 16:02
 */
public class Jwttest {
    public static void printInfo(Object object){
        if (object instanceof String) System.out.println("i am string");
        if (object instanceof Integer) System.out.println("i am integer");
    }
    public static void main(String[] args) throws InterruptedException {
//        String token = JWTUtil.sign("123","123");
//        System.out.println(token);
//        Thread.sleep(5*1001);
//        try {
//            String openid = JWTUtil.getOpenid(token);
//            System.out.println(JWTUtil.verify(token,"123","123"));
//            System.out.println(openid);
//        }catch (AuthenticationException e){
//            e.printStackTrace();
//        }
        printInfo("test");
        printInfo(new Integer(1));
    }
}
