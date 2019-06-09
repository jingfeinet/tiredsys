package com.sicau.tiredsys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Created by zhong  on 2019/5/9 0:27
 */
@Data
public class History {
    private Integer id;
    private String openid;
    private String status;
    private Integer eyeopen;
    private Integer eyeclose;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date time;
}
