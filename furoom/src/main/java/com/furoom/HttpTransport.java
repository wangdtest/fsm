package com.furoom;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.furoom.ejson.EJsonManager;
import com.furoom.ejson.IEJsonIOManager;
import com.furoom.exception.PermissionException;
import com.furoom.exception.ProtocolParseException;
import com.furoom.security.IAuthorityValidateService;
import com.furoom.service.IProtocolBinding;
import com.furoom.service.IRemoteServiceRegister;
import com.furoom.support.FuRoomConstant;
import com.furoom.support.HttpParse;
import com.furoom.support.MangleClassInfoPool;
import com.furoom.support.MethodDescriptor;
import com.furoom.support.ServiceRequest;
import com.furoom.support.ServiceResponse;
import com.furoom.support.ServiceResponse.ExceptionType;
import com.furoom.utils.ApplicationContextUtil;

@Controller
public class HttpTransport {
	private static Logger logger = Logger.getLogger(HttpTransport.class);
	@Autowired
	IAuthorityValidateService authorityValidateService;
	@Autowired
	IRemoteServiceRegister register;
	
	@RequestMapping(value={"/furoom/{interface}/invoke"})
	public void service(HttpServletRequest req, HttpServletResponse resp, @PathVariable("interface")String inter) {
		long start = System.currentTimeMillis();
		try {
			req.setCharacterEncoding("UTF-8");
			resp.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		 
		// 处理阶段
		// 1.请求解析生成请求描述，根据requestURI以及参数（包括header）解析出->a.请求协议;b.请求服务/请求Sdl;c.请求服务接口
		// 2.根据请求协议调取协议绑定服务，输入请求描述->请求服务描述
		// 3.根据服务描述调用服务方法执行，然后生成服务响应描述
		// 4.根据请求协议调用协议绑定服务，输入服务响应描述，写回结果
		ServiceRequest serviceRequest = (ServiceRequest)req.getAttribute("serviceRequest");
		ServiceResponse serviceResponse = (ServiceResponse)req.getAttribute("serviceResponse");
		HttpParse parse = (HttpParse)req.getAttribute("parse");
		IProtocolBinding protocolBinding = (IProtocolBinding)req.getAttribute("protocolBinding");
		try {
			
			try {
				
				long invokeStart = System.currentTimeMillis();
				//服务调用
				serviceResponse = innerInvoke(serviceRequest, serviceResponse);
				long invokeEnd = System.currentTimeMillis();
			} catch (Throwable e) {
				serviceResponse.setException(e);
				serviceResponse.setExceptionType(ExceptionType.UNKNOWN);
			}
			if(serviceResponse.getException()!=null)
				logger.error(serviceResponse.getException());
			
			long seriStart = System.currentTimeMillis();
			//服务调用结果序列化返回
			protocolBinding.replyResponse(parse, serviceResponse);
			long seriEnd = System.currentTimeMillis();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

	private <Q, R> ServiceResponse<R> innerInvoke(final ServiceRequest<Q> req, ServiceResponse<R> rt) {
		try {
			Q service = (Q) (ApplicationContextUtil.getBean(req.getTarget(), req.getInterfacClass()));
			if (service == null) {
				rt.setExceptionType(ExceptionType.ET_SE_SERVICE_NOT_FOUND);
				rt.setException(new RuntimeException("can not find service: " + req.getInterfacClass().getName() + " with target=" + req.getTarget()));
			} else {
//				logger.info("此处有调用");
				rt.setResult((R) req.getMethod().invoke(service, req.getArgs()));
			}
		} catch (Throwable e) {
			logger.debug(e.getMessage(),e);
			if (e instanceof InvocationTargetException) {
				e = ((InvocationTargetException) e).getTargetException();
				rt.setExceptionType(ExceptionType.ET_SE_METHOD_NORMAL_EXCEPTION);
			} else {
				rt.setExceptionType(ExceptionType.ET_SE_INNER_ERROR);
			}
			rt.setException(e);
		}
		return rt;
	}
}
