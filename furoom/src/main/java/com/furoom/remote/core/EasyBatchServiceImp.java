package com.furoom.remote.core;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.el.MethodNotFoundException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.furoom.exception.PermissionException;
import com.furoom.remote.batch.IEasyBatchService;
import com.furoom.remote.batch.SingleRequest;
import com.furoom.security.IAuthorityValidateService;
import com.furoom.service.IProtocolBinding;
import com.furoom.service.IRemoteServiceRegister;
import com.furoom.support.FuRoomConstant;
import com.furoom.support.MangleClassInfo;
import com.furoom.support.MangleClassInfoPool;
import com.furoom.utils.ApplicationContextUtil;


@Service
public class EasyBatchServiceImp implements IEasyBatchService {
	@Autowired
	IAuthorityValidateService authorityValidateService;
	@Autowired
	IRemoteServiceRegister register;
	
	Logger log = Logger.getLogger(EasyBatchServiceImp.class);
	
	public Object[] batchCall(SingleRequest[] requests) throws PermissionException, MethodNotFoundException {

		
		 HttpSession session=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		 
		 Object user = null;
		 
		 if(session!=null)
			user=session.getAttribute(FuRoomConstant.SESSION_ATTRIBUTENAME_USER);
		 
//		 String userid = user==null ? "anonymous" : ((Lender)user).getId().toString();
//		 String param = (requests==null || requests.length==0)?"none":requests[2].method;
//		 log.info(userid+"调用batchcall,调用方法为"+param);
		 
		 
		if(authorityValidateService!=null)
		{
		 for(SingleRequest req: requests){
			 
					if(!authorityValidateService.checkPermission(user, false, req.service, req.method))
					{
						throw new PermissionException("没有足够的权限：【"+req.method+"】");
					}
				
		 		}
		 
		}
		 
		 
		
		Object[] rt = new Object[requests.length];
		int i = 0;
		for (SingleRequest req : requests) {
			try {
				MangleClassInfo ci = MangleClassInfoPool.getDefault().getMangleClassInfo(req.service);
				
				if (ci == null) {
					throw new MethodNotFoundException("can not find service: " + req.service + "." + req.method);
				}
				
				Method m = ci.getMethod(req.method + "_" + (req.args == null ? 0 : req.args.length));
				if (m == null) {
					throw new NoSuchMethodException(req.service + "." + req.method);
				}
				
				IProtocolBinding protocolBinding = register.lookupBinding("json");
				Class clz;
				try {
					clz = protocolBinding.getInterfaceClass(req.service);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				
				Object service = ApplicationContextUtil.getBean(req.target, clz);
				
				if (service == null) {
					throw new RuntimeException("can not find service: " + req.service + " with target=" + req.target);
				} else {
					long starTime = System.currentTimeMillis();
					rt[i] = m.invoke(service, toRealParam(m, req.args));
				}
				
			} catch (Throwable e) {
				if (e instanceof InvocationTargetException) {
					e = ((InvocationTargetException) e).getTargetException();
				}
				rt[i] = e;
			} finally {
			}
			i++;
		}
		return rt;
	}

	public Object[] toRealParam(Method m, Object[] args) {
		if (args == null) {
			return args;
		}
		if (args.length == 0) {
			return args;
		}
		Class<?>[] pts = m.getParameterTypes();
		if (pts.length != args.length) {
			throw new RuntimeException("Wrong numbers of parameter");
		}
		Object[] newArgs = new Object[args.length];
		int index = 0;
		for (Object a : args) {
			if (a == null) {
				index++;
				continue;
			}
			// 只处理数组对象
			if (a instanceof Object[]) {
				Object[] aArray = (Object[]) a;
				Object realArray = Array.newInstance(pts[index].getComponentType(), aArray.length);
				System.arraycopy(aArray, 0, realArray, 0, aArray.length);
				newArgs[index] = realArray;
			} else {
				newArgs[index] = a;
			}
			index++;
		}
		return newArgs;
	}
}
