package com.sicau.tiredsys.common;

import com.sicau.tiredsys.utils.WeiXinApi;

/**
 * Created by zhong  on 2019/5/9 18:52
 */
public class Const {

    public static final String  AUTH_NAME = "token";
    public static final String INIT_ROLE = "guest"; //默认角色
    public static final String AppID="wx4b648685f90832f9";
    public static final String AppSecret="a0169dc7f38c9101271ed04366b43334";
    public static final String GET_OPENID ="https://api.weixin.qq.com/sns/jscode2session";
    public static final String GET_ACCESS_TOKEN  ="https://api.weixin.qq.com/cgi-bin/token";
    public static final String POST_Verify_Signature="https://api.weixin.qq.com/cgi-bin/soter/verify_signature?access_token=";
}
