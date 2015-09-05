package com.furoom.service;

import java.io.IOException;

import com.furoom.exception.ProtocolParseException;
import com.furoom.support.HttpParse;
import com.furoom.support.ServiceRequest;
import com.furoom.support.ServiceResponse;

public interface IProtocolBinding {
	public ServiceRequest getRequest(HttpParse parse) throws ProtocolParseException;
	public void replyResponse(HttpParse parse, ServiceResponse resp) throws IOException;
	public String getProtocol();
	public Class getInterfaceClass(String name) throws ClassNotFoundException;
}
