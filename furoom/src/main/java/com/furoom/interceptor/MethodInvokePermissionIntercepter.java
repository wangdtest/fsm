package com.furoom.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.furoom.exception.PermissionException;
import com.furoom.security.IAuthorityValidateService;
import com.furoom.service.IProtocolBinding;
import com.furoom.service.IRemoteServiceRegister;
import com.furoom.support.FuRoomConstant;
import com.furoom.support.HttpParse;
import com.furoom.support.ServiceRequest;
import com.furoom.support.ServiceResponse;
import com.furoom.support.ServiceResponse.ExceptionType;

public class MethodInvokePermissionIntercepter implements HandlerInterceptor {
	@Autowired
	IAuthorityValidateService authorityValidateService;
	@Autowired
	IRemoteServiceRegister register;
	public static String getIp(HttpServletRequest req){
		String ip = req.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
	}
	
	public String[] allowUrls;//还没发现可以直接配置不拦截的资源，所以在代码里面来排除  
    
    public void setAllowUrls(String[] allowUrls) {  
        this.allowUrls = allowUrls;  
    } 
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
//		response.sendRedirect("/busy.html");
//		String ip = getIp(request);
//		System.out.println(ip);
//		String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
//		if(null != allowUrls && allowUrls.length>=1)  
//            for(String url : allowUrls) {    
//                if(requestUrl.contains(url)) {    
//                    return true;    
//                }    
//            }  
		
		
		request.setAttribute("starttime", System.currentTimeMillis());
		
		
		ServiceRequest serviceRequest = null;
		ServiceResponse serviceResponse = new ServiceResponse();
		
		String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
		
		String interfaceName = requestUrl.replaceAll("/furoom/", "").replaceAll("/invoke", "");
		
		request.setAttribute("interfaceName", interfaceName);
		
		try{
		HttpParse parse = new HttpParse(request, response, interfaceName);
		
		//根据绑定协议取得注册在服务中心的绑定协议处理服务
		IProtocolBinding protocolBinding = register.lookupBinding(parse.getBindingProtocol());
		if (protocolBinding == null) {
			return false;
		}
		
		
		HttpSession session=request.getSession();
		Object	 user=session.getAttribute(FuRoomConstant.SESSION_ATTRIBUTENAME_USER);
		
		 
		//请求参数反序列化
		serviceRequest = protocolBinding.getRequest(parse);
		
		
		//权限校验
		if(authorityValidateService!=null)
		{
			if(!authorityValidateService.checkPermission(user, false, parse.getInterfaceName(), serviceRequest.getMethod().getName()))
			{
//				serviceResponse.setException(new PermissionException());
//				serviceResponse.setExceptionType(ExceptionType.ET_SE_NO_PERMISSION);
//				protocolBinding.replyResponse(parse, serviceResponse);
				return false;
			}
		}
		
		request.setAttribute("parse", parse);
		request.setAttribute("serviceRequest", serviceRequest);
		request.setAttribute("serviceResponse", serviceResponse);
		request.setAttribute("protocolBinding", protocolBinding);
		
			return true;
		}catch(Throwable e){
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long after = System.currentTimeMillis();
		System.out.println(request.getAttribute("interfaceName").toString()+" total costs:"+(after-(Long)request.getAttribute("starttime")));
	}

}
