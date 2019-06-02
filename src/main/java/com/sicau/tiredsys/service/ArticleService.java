package com.sicau.tiredsys.service;

import com.sicau.tiredsys.dao.ArticleDao;
import com.sicau.tiredsys.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhong  on 2019/5/14 15:02
 */
@Service
public class ArticleService {
    @Autowired
    ArticleDao articleDao;

    public boolean addArticle(Article article){
        return articleDao.addArticle(article) > 0 ? true : false;
    }

    public List<Article> selectLatestArticle(int offset, int limit){

        return articleDao.selectLatestArticle(offset, limit);
    }

    public List<Article> selectLatestArticleByOpenid(String openid, int offset, int limit){
        return articleDao.selectLatestArticleByOpenid(openid, offset, limit);
    }

    public boolean deleteByOpenid(int id,String openid){
        return articleDao.deleteByOpenid(id,openid) > 0 ? true : false;
    }
}
