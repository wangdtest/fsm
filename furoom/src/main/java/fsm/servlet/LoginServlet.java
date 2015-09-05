package fsm.servlet;

import fsm.service.IUserService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class LoginServlet {
	@Autowired
	IUserService userService;
	
	Logger log = Logger.getLogger(LoginServlet.class);
	
	@RequestMapping(value={"/notify"})
	public void notify(HttpServletRequest req, HttpServletResponse resp)
	{
		log.info("出现一次回调！");
		writeSuccess(resp);
	}
	
	@RequestMapping(value={"/login/graphValidateCode"})
	public void thirdPartyRegist(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("image/jpeg");
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		try {
			userService.writeGraphValidateCode(resp.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value={"/messageValidateCode"})
	public void getMessageValidateCode(HttpServletRequest req, HttpServletResponse resp)
	{
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		PrintWriter writer=null;
		try {
			writer=resp.getWriter();
			StringBuilder text=new StringBuilder();
			text.append("您好，您的***网校验码为:");
			text.append(String.valueOf(userService.getCurrentSession().getAttribute(IUserService.SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE)));
			text.append(",").append("请您在").append(IUserService.MESSAGEVALIDATECODEEXPIRETIME/60/1000).append("分钟内使用，过期请重新获取。");
			writer.write("<font size=36>"+text.toString()+"</font>");
		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			if(writer!=null)
			{
				writer.flush();
				writer.close();
			}
		}
	}
	
	
	private void writeSuccess(HttpServletResponse resp)
	{
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(200);
		PrintWriter writer = null;
		try {
			writer = resp.getWriter();
			writer.write("SUCCESS");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}
}
