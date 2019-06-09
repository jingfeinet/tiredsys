package com.sicau.tiredsys.dao;

import com.sicau.tiredsys.entity.Article;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhong  on 2019/5/14 15:00
 */
@Mapper
@Repository
public interface ArticleDao {
    String TABLE_NAME = "article";
    String INSERT_FIELDS = "title,brief, content, created_time, user_name, openid,avatar_url";
    String SELECT_FIELDS = " * ";

    @Insert({"insert into " + TABLE_NAME + " (" + INSERT_FIELDS + ") values(#{title}, " +
            "#{content}, #{createdTime}, #{userName}, #{openid})"})
    int addArticle(Article article);

    @Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " ORDER BY created_time DESC limit #{offset}, #{limit}"})
    List<Article> selectLatestArticle(@Param("offset") int offset, @Param("limit") int limit);

    @Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " where openid=#{openid} ORDER BY created_time DESC limit #{offset}, #{limit}"})
    List<Article> selectLatestArticleByOpenid(@Param("openid") String openid, @Param("offset") int offset, @Param("limit") int limit);

    @Delete({"delete from " + TABLE_NAME + " where id = #{id} and openid=#{openid}"})
    int deleteByOpenid(@Param("id") int id,@Param("openid")String openid);

}
