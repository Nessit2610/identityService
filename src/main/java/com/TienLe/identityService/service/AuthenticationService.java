package com.TienLe.identityService.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.TienLe.identityService.dto.request.AuthenticationRequest;
import com.TienLe.identityService.dto.request.IntrospectRequest;
import com.TienLe.identityService.dto.response.AuthenticationResponse;
import com.TienLe.identityService.dto.response.IntrospectResponse;
import com.TienLe.identityService.entity.User;
import com.TienLe.identityService.exception.AppException;
import com.TienLe.identityService.exception.ErrorCode;
import com.TienLe.identityService.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	@Autowired
	private final UserRepository userRepository;
	

	@NonFinal
	@Value("${jwt.signerkey}")
	protected String SIGNER_KEY;
	
	
	
	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();
		
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		
		SignedJWT signedJWT = SignedJWT.parse(token);
		
		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		
		var valid = signedJWT.verify(verifier);
		
		return IntrospectResponse.builder()
				.valid(valid && expiryTime.after(new Date()))
				.build();
	}
	
	private String generateToken(User user) {
		
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
		
		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(user.getUsername())
				.issuer("TienLe")
				.issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
				.claim("scope", buildScope(user))
				.build();
		
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		
		JWSObject jwsObject = new JWSObject(header,payload);
		
		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
		} catch (JOSEException e) {
			e.printStackTrace();
		}
		return jwsObject.serialize();
		
	}
   
	public AuthenticationResponse Authenticate(AuthenticationRequest request){
		
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		var user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
		
		boolean auth =  passwordEncoder.matches(request.getPassword(), user.getPassword());
		if(!auth) {
			throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
		}
		var token = generateToken(user);
		
		return AuthenticationResponse.builder()
				.token(token)
				.authenticated(true)
				.build();
	}
	
	
	private String buildScope(User user) {
		StringJoiner stringJoiner = new StringJoiner(" ");
		if(!CollectionUtils.isEmpty(user.getRoles())) {
			user.getRoles().forEach(s -> stringJoiner.add(s));
		}
		
		return stringJoiner.toString();
	}
	
}
