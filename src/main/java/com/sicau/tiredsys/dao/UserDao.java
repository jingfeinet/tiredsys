package com.sicau.tiredsys.dao;

import com.sicau.tiredsys.entity.User;
import com.sicau.tiredsys.entity.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by zhong  on 2019/5/9 0:25
 */
@Mapper
@Repository
public interface UserDao {

    @Insert(
            "insert into api_user (user_name,history_status,history_eyeopen,history_eyeclose,history_time) " +
                    "values (#{userName},#{historyStatus},#{historyEyeopen},#{historyEyeclose},#{historyTime}"
    )
    int addUserApi(History history);

    @Select("select * from user where user_name = #{userName} and password=#{password}")
    User selectOne(User user);

    ArrayList<User> selectList(String  user);

    User getUserById(String userId);

    @Select("select * from user where openid = #{openid}")
    User getUserByOpenId(String openId);

    @Insert("insert into user (role,openid,fingerprint) values" +
            " (#{role},#{openid},#{fingerprint})")
    int addUser(User user);

    @Update("update user set avatar_url=#{avatarUrl},user_name=#{userName} where openid = #{openid}")
    int updateUser(User user);


    @Select("select * from user where password=#{password} and openid=#{openid}")
    User getUserByPassword(User user);

    @Update("update user set password = #{password},fingerprint=0 where openid = #{openid}")
    int updatePassword(User user);

    //设置指纹开关,有指纹就一定有密码锁
    @Update("update user set fingerprint = #{fingerprint} where openid = #{openid}")
    int updateFingerprint(User user);

}
