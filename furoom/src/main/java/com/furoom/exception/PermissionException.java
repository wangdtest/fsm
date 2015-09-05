package com.furoom.exception;

import com.furoom.support.ServiceResponse.ExceptionType;

public class PermissionException extends SuperException{
	public PermissionException()
	{
		super();
	}
	public PermissionException(String message)
	{
		super(message);
	}
	public PermissionException(Throwable e,ExceptionType type)
	{
		super(e, type);
	}
}
