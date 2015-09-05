package com.furoom.support;

import static com.furoom.support.FuRoomConstant.FR_PATH_PATTERN;
import static com.furoom.support.FuRoomConstant.FR_PROTOCOL_FULLNAME;
import static com.furoom.support.FuRoomConstant.PROTOCOL_BINGDING_JSON;
import static com.furoom.support.FuRoomConstant.PROTOCOL_BINGDING_REST;
import static com.furoom.support.FuRoomConstant.PROTOCOL_FR_WS;
import static com.furoom.support.FuRoomConstant.PROTOCOL_TRANSPORT_HTTP;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpParse {

	protected HttpURLConnection realConnection;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String serviceProtocol;// = PROTOCOL_ES_WS;
	protected String bindingProtocol;// = PROTOCOL_BINGDING_HESSIAN;
	protected String transportProtocol;// = PROTOCOL_ES_WS;
	protected String interfaceName;
	protected Map<String, Object> attributeMap = new HashMap<String, Object>();
	protected ServiceURL requestURL;

	protected Locale locale;

	// sdl Service Description Language
	protected boolean isFetchSdl = false;

	public ServiceURL getRequestURL() {
		return requestURL;
	}

	
	public HttpParse(HttpServletRequest request, HttpServletResponse response, String interfaceName, boolean isFetchSdl) throws MalformedURLException{
		this.request = request;
		this.response = response;
		this.interfaceName = interfaceName;
		this.isFetchSdl = true;
		serviceProtocol = PROTOCOL_FR_WS;
		bindingProtocol = PROTOCOL_BINGDING_JSON;
		transportProtocol = PROTOCOL_TRANSPORT_HTTP;
		StringBuilder transportUrl = new StringBuilder(transportProtocol).append("://").append(request.getHeader("Host")).append("/furoom/").append(interfaceName).append("/invoke");
		requestURL = new ServiceURL(serviceProtocol, bindingProtocol,transportUrl.toString());
	}
	
	
	public HttpParse(HttpServletRequest request, HttpServletResponse response, String interfaceName)
			throws MalformedURLException {
			this.request = request;
			this.response = response;
			this.interfaceName = interfaceName;
		
			String ctxType = getRequestAttribute("Content-Type");
			
			if(ctxType==null || !ctxType.contains("x-application/fr:")){
				throw new MalformedURLException("wrong protocol format");
			}
			
			String ptstr = ctxType.substring("x-application/fr:".length());
				int mpos = ptstr.indexOf(';');
				if (mpos > 0) {
					ptstr = ptstr.substring(0, mpos).trim();
				}
			if (ptstr != null && ptstr.length() > 0) {
				String[] pts = ptstr.split("\\-");
				if (pts.length == 1) {
					bindingProtocol = pts[0];
					serviceProtocol = PROTOCOL_FR_WS;
					transportProtocol = PROTOCOL_TRANSPORT_HTTP;
				} else if (pts.length < 3) {
					throw new MalformedURLException("wrong protocol format");
				} else {
					serviceProtocol = pts[0];
					bindingProtocol = pts[1];
					transportProtocol = pts[2];
				}
			} else {
				throw new MalformedURLException("wrong protocol format");
			}
			
			
			StringBuilder transportUrl = new StringBuilder(transportProtocol).append("://").append(request.getHeader("Host")).append("/furoom/").append(interfaceName).append("/invoke");
			requestURL = new ServiceURL(serviceProtocol, bindingProtocol,transportUrl.toString());
	}
	public <T> void setRequestAttribute(String key, T value) {
		if (request != null){
			request.setAttribute(key, value);
		}else{
			realConnection.setRequestProperty(key, value.toString());
		}
	}
	public <T> T getRequestAttribute(String key) {
		if (request != null) {
			T rt = (T) request.getHeader(key);
			if (rt == null) {
				rt = (T) request.getAttribute(key);
			}
			return rt;
		} else {
			return (T) realConnection.getRequestProperty(key);
		}
	}

	public boolean isFetchSdl() {
		return isFetchSdl;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public String getInterfaceName() {
		return interfaceName;
	}
	public <T> T getAttribute(String key) {
		return (T)attributeMap.get(key);
	}

	public <T> void setAttribute(String key, T value) {
		attributeMap.put(key, value);
	}

	public String getServiceProtocol() {
		return serviceProtocol;
	}

	public String getBindingProtocol() {
		return bindingProtocol;
	}

	public String getTransportProtocol() {
		return transportProtocol;
	}
	
	
}
