package com.sicau.tiredsys.service;

import com.sicau.tiredsys.dao.HistoryDao;
import com.sicau.tiredsys.entity.History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhong  on 2019/5/19 20:50
 */
@Service
public class HistoryService {

    @Autowired
    HistoryDao historyDao;

    //获取历史纪录
    public ArrayList<History> getHistory(int offset,int limit,String openid){
        HashMap params = new HashMap();
        params.put("openid",openid);
        params.put("offset",offset);
        params.put("limit",limit);
        ArrayList<History> list = historyDao.getHistory(params);
        return list;
    }

    //获取最新记录
    public History getLastHistory(String openid){
        History history = historyDao.getLastHistory(openid);
        return history;
    }
}
