package com.sicau.tiredsys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

/**
 * Created by zhong  on 2019/5/14 15:01
 */
@Data
public class Article {
    private Integer id;
    private String title;
    private String brief;//简介
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createdTime;
    private String userName;
    private String avatarUrl;
    private String openid;

    public Article(String title, String content, Date createdTime, String userName, String openid) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.userName = userName;
        this.openid = openid;
    }

    public Article(){}
}
