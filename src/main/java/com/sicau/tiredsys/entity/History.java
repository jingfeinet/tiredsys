package com.sicau.tiredsys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Created by zhong  on 2019/5/9 0:27
 */
@Data
public class History {

    private String openid;
    private String status;
    private String eyeopen;
    private String eyeclose;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;

    public History(){}

    public History(String openid, String historyStatus, String historyEyeopen, String historyEyeclose, Date historyTime) {
        this.openid = openid;
        this.status = historyStatus;
        this.eyeopen = historyEyeopen;
        this.eyeclose = historyEyeclose;
        this.date = historyTime;
    }
}
