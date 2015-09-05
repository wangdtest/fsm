package com.furoom.exception;

import com.furoom.support.ServiceResponse.ExceptionType;

public class ProtocolParseException extends SuperException{
		public ProtocolParseException()
		{
			
		}
		public ProtocolParseException(Throwable e,ExceptionType type)
		{
			super(e, type);
		}
}
