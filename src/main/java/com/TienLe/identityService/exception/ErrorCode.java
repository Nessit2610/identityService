package com.TienLe.identityService.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
	
	UNCATEGORIZED_EXCEPTION(1002,"Uncategorized error",HttpStatus.INTERNAL_SERVER_ERROR),
	USER_EXISTED(1001,"User Existed",HttpStatus.BAD_REQUEST),
	NOTFOUND_API(404,"API not exist",HttpStatus.BAD_REQUEST),
	USERNAME_INVALID(1003, "Username must be at least 5 characters",HttpStatus.BAD_REQUEST),
	PASSWORD_INVALID(1004,"Password must be at least 8 characters",HttpStatus.BAD_REQUEST),
	INVALID_KEY(1005,"Invalid message key",HttpStatus.BAD_REQUEST),
	USER_NOTFOUND(1006,"User not found",HttpStatus.BAD_REQUEST),
	USER_UNAUTHENTICATED(1007, "Unauthenticated",HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(1008, "You do not have permission",HttpStatus.FORBIDDEN);
	
	private ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.Code = code;
		this.Message = message;
		this.statusCode = statusCode;
	}

	private int Code;
	private String Message;
	private HttpStatusCode statusCode;
	
	
}
