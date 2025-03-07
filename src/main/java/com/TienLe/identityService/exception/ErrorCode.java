package com.TienLe.identityService.exception;

public enum ErrorCode {
	
	USER_EXISTED(1001,"User Existed"),
	UNCATEGORIZED_EXCEPTION(1002,"Uncategorized error"),
	NOTFOUND_API(404,"API not exist"),
	USERNAME_INVALID(1003, "Username must be at least 5 characters"),
	PASSWORD_INVALID(1004,"Password must be at least 8 characters"),
	INVALID_KEY(1005,"Invalid message key"),
	USER_NOTFOUND(1006,"User not found"),
	USER_UNAUTHENTICATED(1007, "Unauthenticated");
	
	private ErrorCode(int code, String message) {
		Code = code;
		Message = message;
	}

	private int Code;
	private String Message;
	
	public int getCode() {
		return Code;
	}
	
	public String getMessage() {
		return Message;
	}
	
}
