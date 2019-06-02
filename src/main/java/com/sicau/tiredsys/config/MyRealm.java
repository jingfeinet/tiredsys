package com.sicau.tiredsys.config;

import com.sicau.tiredsys.common.RoleWithPermission;
import com.sicau.tiredsys.dao.UserDao;
import com.sicau.tiredsys.entity.User;
import com.sicau.tiredsys.utils.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhong  on 2019/3/14 13:15
 */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }
    //执行授权

    /**
     * 此方法调用  hasRole,hasPermission的时候才会进行回调.
     * <p>
     * 权限信息.(授权):
     * 1、如果用户正常退出，缓存自动清空；
     * 2、如果用户非正常退出，缓存自动清空；
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
     * （需要手动编程进行实现；放在service进行调用）
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例，
     * 调用clearCached方法；
     * :Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // TODO Auto-generated method stub
        System.out.println("授权");
        //获取当前登录用户
        Subject subject = SecurityUtils.getSubject();

        String userId = (String) subject.getPrincipal();

        String role0 = userDao.getUserById(userId).getRole();
        Set<String> roles = new HashSet<>();
        roles.add(role0);
        //给资源授权
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        simpleAuthorizationInfo.setRoles(roles);
        roles.forEach(role -> {
            Set<String> permissions = RoleWithPermission.getPermissionsByRole(role);

            simpleAuthorizationInfo.addStringPermissions(permissions);
        });

        return simpleAuthorizationInfo;
    }

    //执行认证逻辑
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        // TODO Auto-generated method stub
        System.out.println("认证");

        String token = (String) auth.getCredentials();
        String openid = "";
        openid = JWTUtil.getOpenid(token);

        String redisToken = (String) redisTemplate.opsForHash().get(openid, "token"); //获取缓存登录状态，减少数据库压力
        User user = (User) redisTemplate.opsForHash().get(openid, "userInfo");
        if (user == null || redisToken == null || "".equals(redisToken.trim())) {
            //用户名错误
            //shiro会抛出UnknownAccountException异常
            throw new AuthenticationException("你还没登录");
        } else {

            String password = user.getPassword();

            if (!token.equals(redisToken)) { //判断和缓存的token是否一致，这样也为了保证单点登录
                throw new AuthenticationException("已经再别的地方登录，请查看是否本人操作");

            }
            if (!JWTUtil.verify(token, openid, password)) { //判断token是否过期
                throw new AuthenticationException("登录过期，请重新登录");
            }
        }

        System.out.println("认证成功");
        return new SimpleAuthenticationInfo(openid, token, "MyRealm");  //只有这样才成功了。。。。。

    }

}
