package com.furoom.remote.batch;

import javax.el.MethodNotFoundException;

import com.furoom.exception.PermissionException;


public interface IEasyBatchService {

	public Object[] batchCall(SingleRequest[] requests) throws PermissionException, MethodNotFoundException;

}
