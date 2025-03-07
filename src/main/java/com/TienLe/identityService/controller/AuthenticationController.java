package com.TienLe.identityService.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TienLe.identityService.dto.request.APIResponse;
import com.TienLe.identityService.dto.request.AuthenticationRequest;
import com.TienLe.identityService.dto.request.IntrospectRequest;
import com.TienLe.identityService.dto.response.AuthenticationResponse;
import com.TienLe.identityService.dto.response.IntrospectResponse;
import com.TienLe.identityService.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping("/token")
	public APIResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
		var result = authenticationService.Authenticate(request);
		return APIResponse.<AuthenticationResponse>builder()
				.result(result)
				.build();
	}
	
	@PostMapping("/introspect")
	public APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws JOSEException, ParseException{
		var result = authenticationService.introspect(request);
		return APIResponse.<IntrospectResponse>builder()
				.result(result)
				.build();
	}	
	
}
