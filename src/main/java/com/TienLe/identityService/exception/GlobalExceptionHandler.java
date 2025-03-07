package com.TienLe.identityService.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.TienLe.identityService.dto.request.APIResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<APIResponse> handlingRuntimeException(Exception exception){
		APIResponse apiResponse = new APIResponse();
		apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
		apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	} 	
	
	
	
	@ExceptionHandler(value = AppException.class)
	public ResponseEntity<APIResponse> handlingAppException(AppException exception){
		
		ErrorCode errorCode = exception.getErrorCode();
		
		APIResponse apiResponse = new APIResponse();
		
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}
	
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<APIResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exeption){
		
		String enumkey = exeption.getFieldError().getDefaultMessage();
		
		ErrorCode errorCode = ErrorCode.INVALID_KEY;
		
		try {
			errorCode = ErrorCode.valueOf(enumkey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		APIResponse apiResponse = new APIResponse();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		
		return ResponseEntity.badRequest().body(apiResponse);
	}
	
	@ExceptionHandler(value = NoHandlerFoundException.class)
	public ResponseEntity<APIResponse> handlingNoHandlerFoundException(NoHandlerFoundException exception){
		ErrorCode errorCode = ErrorCode.NOTFOUND_API;
		APIResponse apiResponse = new APIResponse();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		
		return ResponseEntity.badRequest().body(apiResponse);
	}
}
