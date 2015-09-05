package com.furoom;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieTool {
	public static String getLoginId(HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		if(cookies!=null){
		for(Cookie cookie : cookies){
			if(cookie.equals("loginId")){
				return cookie.getValue();
			}
		}
		}
		return null;
	}
	
	public static void writeLoginId(HttpServletResponse resp, String loginId){
		Cookie cookie = new Cookie("loginId", loginId);
		cookie.setMaxAge(30*60);
		resp.addCookie(cookie);
	}
}
