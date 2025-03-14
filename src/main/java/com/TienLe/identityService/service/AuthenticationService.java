package com.TienLe.identityService.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.TienLe.identityService.dto.request.AuthenticationRequest;
import com.TienLe.identityService.dto.request.IntrospectRequest;
import com.TienLe.identityService.dto.request.LogoutRequest;
import com.TienLe.identityService.dto.request.RefreshRequest;
import com.TienLe.identityService.dto.response.AuthenticationResponse;
import com.TienLe.identityService.dto.response.IntrospectResponse;
import com.TienLe.identityService.entity.InvalidatedToken;
import com.TienLe.identityService.entity.User;
import com.TienLe.identityService.exception.AppException;
import com.TienLe.identityService.exception.ErrorCode;
import com.TienLe.identityService.repository.InvalidatedTokenRepository;
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
	

	@Autowired
	private InvalidatedTokenRepository invalidatedTokenRepository;
	
	@NonFinal
	@Value("${jwt.signerkey}")
	protected String SIGNER_KEY;
	
	@NonFinal
	@Value("${jwt.valid-duration}")
	protected long VALID_DURATION;
	
	@NonFinal
	@Value("${jwt.refeshable-duration}")
	protected long REFRESHABLE_DURATION;
	
	
	
	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();
		boolean isvalid = true;
		
		try {
			verifyToken(token,false);			
		} catch (AppException e) {
			isvalid = false;
		}
		
		return IntrospectResponse.builder()
				.valid(isvalid)
				.build();
	}
	
	private String generateToken(User user) {
		
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
		
		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(user.getUsername())
				.issuer("TienLe")
				.issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
				.jwtID(UUID.randomUUID().toString())
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
			user.getRoles().forEach(role -> {
				stringJoiner.add("ROLE_" + role.getName());
				if(!CollectionUtils.isEmpty(role.getPermissions())) {					
					role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
				}
			});
		}
		
		return stringJoiner.toString();
	}
	
	public void logout(LogoutRequest request) throws JOSEException, ParseException {
		var signToken = verifyToken(request.getToken(), true);
		String jit = signToken.getJWTClaimsSet().getJWTID();
		Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
		
		InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
		invalidatedTokenRepository.save(invalidatedToken);
	}
	
	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		
		SignedJWT signedJWT = SignedJWT.parse(token);
		
		Date expiryTime = (isRefresh)
				? new Date(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant()
						.plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli()) 
				: signedJWT.getJWTClaimsSet().getExpirationTime();
		
		var valid = signedJWT.verify(verifier);
		
		if(!(valid && expiryTime.after(new Date()))) {
			throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
		}
		
		if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
			throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
		}
		
		
			
		return signedJWT;
	}
	
	public AuthenticationResponse refreshToken(RefreshRequest request) throws JOSEException, ParseException {
		var signJWT = verifyToken(request.getToken(), true);
		var jit = signJWT.getJWTClaimsSet().getJWTID();
		var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();
		InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
		invalidatedTokenRepository.save(invalidatedToken);
		var username = signJWT.getJWTClaimsSet().getSubject();
		var user = userRepository.findByUsername(username).orElseThrow(
				() -> new AppException(ErrorCode.USER_NOTFOUND)
				); 
		var token = generateToken(user);
		
		return AuthenticationResponse.builder().token(token).authenticated(true).build();
	
	}
	
	
	
}
