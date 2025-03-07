package com.TienLe.identityService.dto.response;

import com.TienLe.identityService.dto.request.AuthenticationRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
	String token;
	private Boolean authenticated;
}
