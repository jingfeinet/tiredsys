package com.sicau.tiredsys.controller;

import com.sicau.tiredsys.common.ResponseResult;
import com.sicau.tiredsys.entity.Article;
import com.sicau.tiredsys.entity.User;
import com.sicau.tiredsys.service.ArticleService;
import com.sicau.tiredsys.service.WordFilterService;
import com.sicau.tiredsys.utils.JWTUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhong  on 2019/5/14 14:59
 */
@RestController
@Api(value = "ArticleController",description = "文章接口")
public class ArticleController {
    @Autowired
    ArticleService articleService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    WordFilterService wordFilterService;
    ThreadLocal<SimpleDateFormat> simpleDateFormatHolder = new ThreadLocal<>();


    @PostMapping("/addArticle")
    @ApiOperation(value = "文章添加接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "文章标题", name = "title", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "文章内容", name = "content", dataType = "String"),
    })
    public ResponseResult addArticle( String title, String content,HttpServletRequest request){
/*        simpleDateFormatHolder.set(new SimpleDateFormat("yyyy-MM-dd"));
        SimpleDateFormat simpleDateFormat = simpleDateFormatHolder.get();
        Date date = new Date();

        String formatDate = simpleDateFormat.format(date);*/
        String openid = JWTUtil.getTokenOpenid(request);
        User user = (User) redisTemplate.opsForHash().get(openid,"userInfo");
        Date now = new Date();
        title = wordFilterService.filter(title);
        content = wordFilterService.filter(content);
        String  userName = user.getUserName();
        Article article = new Article(title, content, now, userName, openid);
        if (articleService.addArticle(article)){
            return ResponseResult.createBySuccessMessage("添加成功");
        }else {
            return ResponseResult.createByErrorMessage("添加失败");
        }
    }

    @GetMapping("/articles")
    @ApiOperation(value = "最新文章获取接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "文章偏移量，用于分页", name = "offset", dataType = "int"),
            @ApiImplicitParam(paramType = "query", value = "一页取多少文章", name = "limit", dataType = "int")
    })
    public ResponseResult getArticle(int offset, int limit){
        List list = articleService.selectLatestArticle(offset, limit);
        return ResponseResult.createBySuccess(list);
    }

    @DeleteMapping("/article/{id}")
    @ApiOperation(value = "文章删除接口")
    // 文章需要在对应位置添加隐藏域，内容为文章id
    public ResponseResult deleteArticle(HttpServletRequest request,@ApiParam("文章id")@PathVariable int id){
        String  openid = JWTUtil.getTokenOpenid(request);
        /**
         *     获取openid比较用户是否有权限删除
         */
        // if had the authority
          boolean success =  articleService.deleteByOpenid(id,openid) == true ? true : false;
          if (success) return ResponseResult.createBySuccessMessage("删除成功");
          else return ResponseResult.createByErrorMessage("删除失败");
    }
}
