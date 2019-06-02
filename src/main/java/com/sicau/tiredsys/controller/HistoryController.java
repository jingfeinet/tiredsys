package com.sicau.tiredsys.controller;


import com.sicau.tiredsys.common.ResponseResult;
import com.sicau.tiredsys.dao.UserDao;
import com.sicau.tiredsys.entity.History;
import com.sicau.tiredsys.entity.User;
import com.sicau.tiredsys.service.HistoryService;
import com.sicau.tiredsys.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(value = "HistoryController",description = "历史纪录接口")
public class HistoryController {
	@Autowired
	UserDao userDao;
	@Autowired
	HistoryService historyService;

	@RequestMapping("showList")
	@ResponseBody
	public ResponseResult showVideo(@RequestParam(value="user") String user_name) {
//		user_name = "123";
		HashMap json = new HashMap();

		String statement = "com.bitongyun.userAPIMapper.findUserAPI";
		List<User> video_list = userDao.selectList(user_name);
//        String jsonString = JSON.toJSONString(video_list);
		json.put("list",video_list);
	    return ResponseResult.createBySuccess(json);
	}

	@ApiOperation(value = "显示历史纪录")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "offset",value = "偏移量",dataType = "int",required = true),
			@ApiImplicitParam(paramType = "query",name = "limit",value = "多少个",dataType = "int",required = true)
	})
	@GetMapping("/history")
	public ResponseResult showHistory(HttpServletRequest request,Integer offset,Integer limit){
       String openid = JWTUtil.getTokenOpenid(request);
        if (offset==null||limit==null) //为空默认查最新的记录
       	return ResponseResult.createBySuccess(historyService.getLastHistory(openid));
		ArrayList<History> list = historyService.getHistory(offset,limit,openid);
		return ResponseResult.createBySuccess(list);
	}

}
