package com.furoom;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.furoom.security.IAuthorityValidateService;
import com.furoom.service.IProtocolBinding;
import com.furoom.service.IRemoteServiceRegister;
import com.furoom.support.FuRoomConstant;
import com.furoom.support.HttpParse;
import com.furoom.support.MangleClassInfo;
import com.furoom.support.MangleClassInfoPool;
import com.furoom.support.MethodDescriptor;
import com.furoom.support.ParamDescriptor;
import com.furoom.support.ParamList;
import com.furoom.support.ServiceResponse;
import com.furoom.support.ServiceResponse.ExceptionType;
import com.furoom.utils.ApplicationContextUtil;

@Controller
public class InterfaceDescripter {
	protected static final String HEADER_IFMODSINCE = "If-Modified-Since";
	protected static final String HEADER_IFNONEMATCH = "If-None-Match";
	protected static final String HEADER_LASTMOD = "Last-Modified";
	Map<String, MethodDescriptor[]> caches = new ConcurrentHashMap<String, MethodDescriptor[]>();
	MangleClassInfoPool mangleClassInfoPool = MangleClassInfoPool.getDefault();
	private static IEJsonIOManager eJsonIOManager=EJsonManager.getDefaultManager(); 
	Logger logger = Logger.getLogger(InterfaceDescripter.class);
	@Autowired
	IRemoteServiceRegister register;
	@RequestMapping(value={"/furoom/{interface}/json"})
	public void service(HttpServletRequest req, HttpServletResponse resp, @PathVariable("interface")String inter){
		ServiceResponse serviceResponse = new ServiceResponse();
		 
		 try{
			 IProtocolBinding protocolBinding = register.lookupBinding(FuRoomConstant.PROTOCOL_BINGDING_JSON);
			 HttpParse parse = new HttpParse(req, resp, inter, true);
		 
			long ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
			Class clz;
			try {
				clz = protocolBinding.getInterfaceClass(parse.getInterfaceName());
			} catch (ClassNotFoundException e) {
				logger.debug(e.getMessage(),e);
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			long lastModified = clz.getResource(clz.getSimpleName() + ".class").openConnection().getLastModified();
			// for purposes of comparison we add 999 to ifModifiedSince
			// since the fidelity
			// of the IMS header generally doesn't include milli-seconds
			if (ifModifiedSince > -1 && lastModified > 0 && lastModified <= (ifModifiedSince + 999)) {
				resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
			if (lastModified > 0) {
				resp.setDateHeader(HEADER_LASTMOD, lastModified);
			}
			if (req.getQueryString()!=null && req.getQueryString().indexOf("&v") > 0) {
				resp.setDateHeader("Expires", System.currentTimeMillis() + 3650L * 24 * 60 * 60 * 1000);
			}
			parse.setAttribute("requestId", parse.getRequestURL().getTransportUrl());
			parse.setAttribute("callback", "FuRoomClient._remoteProxyRegisterFromServer");
			MethodDescriptor[] mds = queryMethodDescriptors(null, clz);
			serviceResponse.setResult(mds);
			protocolBinding.replyResponse(parse, serviceResponse);
			return;
		 }catch(Throwable e) {
				logger.error(e.getMessage(), e);
			}
	}
	
	private MethodDescriptor[] queryMethodDescriptors(String name, Class requiredType) {

		MethodDescriptor[] rt = caches.get(requiredType.getName());
		if (rt == null) {
			Object s = ApplicationContextUtil.getBean(name, requiredType);
			if (s == null) {
				return null;
			}
			try {
				MangleClassInfo classInfo = mangleClassInfoPool.getMangleClassInfo(requiredType.getName());
				ArrayList<MethodDescriptor> tmplist = new ArrayList<MethodDescriptor>();
				for (Method m : classInfo.getMethods()) {
					MethodDescriptor md = new MethodDescriptor();
					md.setName(m.getName());
					md.setReturnType(m.getReturnType().getName());
					ParamList pl = m.getAnnotation(ParamList.class);
					ParamDescriptor[] pdl = new ParamDescriptor[m.getParameterTypes().length];
					String[] pn = null;
					if (pl != null) {
						pn = pl.value();
					} else {
						pn = new String[m.getParameterTypes().length];
						for (int i = 0; i < pn.length; i++) {
							pn[i] = "" + i;
						}
					}
					for (int i = 0; i < pn.length; i++) {
						ParamDescriptor pd = new ParamDescriptor();
						pd.setName(pn[i]);
						pd.setType(m.getParameterTypes()[i].getName());
						pdl[i] = pd;
					}
					md.setParams(pdl);
					tmplist.add(md);
				}
				rt = new MethodDescriptor[tmplist.size()];
				tmplist.toArray(rt);
				caches.put(requiredType.getName(), rt);
			} catch (ClassNotFoundException e) {
				logger.debug(e.getMessage(),e);
				return null;
			}
		}
		return rt;
	}
}
