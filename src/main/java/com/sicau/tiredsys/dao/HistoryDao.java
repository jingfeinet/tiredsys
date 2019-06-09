package com.sicau.tiredsys.dao;

import com.sicau.tiredsys.entity.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhong  on 2019/5/19 20:51
 */
@Mapper
@Repository
public interface HistoryDao {
    @Select("select * from history where openid = #{openid}  limit #{offset},#{limit}")
    ArrayList<History> getHistory(HashMap params);

    @Select("select * from history  where openid=#{openid} order by id DESC limit 1")
    History getLastHistory(String openid);

    @Insert("insert into history (openid,status,eyeopen,eyeclose) values (#{openid},#{status},#{eyeopen},#{eyeclose} ")
    Integer addHistory(History history);
}
