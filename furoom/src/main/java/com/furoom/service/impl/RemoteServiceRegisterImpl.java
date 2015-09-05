package com.furoom.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.furoom.service.IProtocolBinding;
import com.furoom.service.IRemoteServiceRegister;

@Component
public class RemoteServiceRegisterImpl implements IRemoteServiceRegister{
	Map<String,IProtocolBinding> protocolBindings=new HashMap<String, IProtocolBinding>();
	@Override
	public void register(String protocol, IProtocolBinding binding) {
		protocolBindings.put(protocol, binding);
	}

	@Override
	public void unregister(String protocol, IProtocolBinding binding) {
		protocolBindings.put(protocol, binding);
	}

	@Override
	public IProtocolBinding lookupBinding(String protocol) {
		return protocolBindings.get(protocol);
	}
	
}
