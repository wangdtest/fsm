package com.furoom.support;

public interface FuRoomConstant {
	String SESSION_ATTRIBUTENAME_USER="user";
	int USER_ANONYMOUS=-1;
	
	
	String PROTOCOL =  "protocol";
	String PROTOCOL_FR_WS = "ws"; //
	String PROTOCOL_BINGDING_HESSIAN = "hessian";
	String PROTOCOL_BINGDING_REST = "rest";
	String PROTOCOL_BINGDING_JSON = "json"; //
	String PROTOCOL_TRANSPORT_XHTTP = "xhttp";
	String PROTOCOL_TRANSPORT_HTTP = "http"; //
	String PROTOCOL_TRANSPORT_HTTPS = "https";
	String FR_PATH_PATTERN =  "/furoom";
	String FR_CAPTCHA_PATH_PATTERN =  "/frcaptcha";
	String FR_PROTOCOL_FULLNAME = ".pf"; //
	
	String I18n_httpTransport_listenAt = "httpTransport.listenAt";
	String FR_CAPTCHA_listenAt = "frcaptcha.listenAt";
	
	String CONN_READ_TIMEOUT = "$conn_read_timeout";
	String CONN_CONNECT_TIMEOUT = "$conn_connect_timeout";
}
