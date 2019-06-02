package com.sicau.tiredsys.controller;

import com.sicau.tiredsys.common.ResponseResult;
import com.sicau.tiredsys.dao.UserDao;
import com.sicau.tiredsys.entity.History;
import com.sicau.tiredsys.utils.RequestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UploadAPIcontrllor {

	@Autowired
	UserDao userDao;

	 @RequestMapping("/uploadImageAPI")

	    public ResponseResult uploadImage(@RequestParam(value = "image", required = false) MultipartFile files, HttpServletRequest requesrt,
										  HttpServletResponse response) throws IOException {
	        String uuid = UUID.randomUUID().toString();
	        uuid = uuid.replaceAll("-", "");
	        String realPath = requesrt.getSession().getServletContext().getRealPath("/");
	        System.out.println(realPath);
	        String path = "/static/upload/images";
	        String fileName = files.getOriginalFilename();
	        String suffix = fileName.substring(fileName.length() - 3);
	        File file = new File(realPath+path,uuid+"."+suffix);
	        if(!file.exists()){
	            file.createNewFile();
	        }
	        files.transferTo(file);
	        HashMap json = new HashMap();
	        json.put("fileName","upload/images/"+uuid+"."+suffix);
	        return ResponseResult.createBySuccess(json);
	    }
	 
	 @RequestMapping("/uploadVideoAPI")

	    public ResponseResult uploadVideo(@RequestParam(value = "mf", required = false) MultipartFile files,
									  HttpServletRequest requesrt,
	                                  HttpServletResponse response
	 									) throws IOException {
	      HashMap json = new HashMap();

	        response.setContentType("application/json;charset=utf-8;");
	        if(files==null){
	            json.put("messqge","message");
	            return ResponseResult.createBySuccess(json);
	        }
//         String	 	 current_user = "方圆几里";
	        String uuid = UUID.randomUUID().toString();
	        uuid = uuid.replaceAll("-", "");
	        String realPath = requesrt.getSession().getServletContext().getRealPath("/");
	        System.out.print(realPath);
	        long size = files.getSize();
	        String fileName = files.getOriginalFilename();
	        String suffix = fileName.substring(fileName.length() - 3);
	        String path = "/static/upload/videos";

	        File file = new File(realPath+path,uuid+"."+suffix);
	        if(!file.exists()){
	            file.createNewFile();
	        }
	        files.transferTo(file);
	        String imgPath = null;
	        System.out.print(imgPath);
	        json.put("fileName","/static/upload/videos/"+uuid+"."+suffix);
	       return ResponseResult.createBySuccess(json);
	    }


	@RequestMapping("/checkAPI")

	public ResponseResult check(HttpServletRequest request){
	 	HashMap json = new HashMap();
	 	String  video = request.getParameter("video");
	 	String[] filenames = video.split("videos/");
	 	String  filename = filenames[1];
		System.out.println(filename);
		Map<String, String> map = new HashMap<String,String>();
		map.put("message", filename);
		String result = RequestClient.httpRequestToString("http://127.0.0.1:5000/test", "get", map, null);
		System.out.println(result);
		result=result.substring(1,result.length()-1);
		String[] results = result.split(",");
		System.out.print(results);
		if(results[0].equals("\"正常\"")){
			results[0]="正常";
		}else if(results[0].equals("\"疲劳\"")) {
			results[0]="疲劳";
		}
		String current_user = request.getParameter("user");
		System.out.print(current_user);

		Date date=new Date();     //获取一个Date对象
		Timestamp timeStamp = new Timestamp(date.getTime());
		String statement = "com.bitongyun.userAPIMapper.addUserAPI";
		userDao.addUserApi(new History(current_user, results[0], results[1], results[2], timeStamp));
		json.put("message",results);
		return ResponseResult.createBySuccess(json);
	}

}
