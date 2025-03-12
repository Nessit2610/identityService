package com.TienLe.identityService.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TienLe.identityService.constant.PredefinedRole;
import com.TienLe.identityService.dto.request.UserCreationRequest;
import com.TienLe.identityService.dto.request.UserUpdateRequest;
import com.TienLe.identityService.dto.response.UserResponse;
import com.TienLe.identityService.entity.Role;
import com.TienLe.identityService.entity.User;
import com.TienLe.identityService.entity.UserMongo;
import com.TienLe.identityService.enums.Roles;
import com.TienLe.identityService.exception.AppException;
import com.TienLe.identityService.exception.ErrorCode;
import com.TienLe.identityService.mapper.UserMapper;
import com.TienLe.identityService.repository.RoleRepository;
import com.TienLe.identityService.repository.UserMongoRepository;
import com.TienLe.identityService.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class UserService {
	
	@Autowired
	private final UserRepository userRepository;
	
	@Autowired
	private final UserMongoRepository userMongoRepository;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	public UserService(UserRepository userRepository, UserMongoRepository userMongoRepository, UserMapper userMapper) {
		super();
		this.userRepository = userRepository;
		this.userMongoRepository = userMongoRepository;
		this.userMapper = userMapper;
	}

	public UserResponse createUser(UserCreationRequest request) {
		
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		
		User user = userMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);
		
		return userMapper.toUserResponse(userRepository.save(user));
		 
	}
	
	public UserResponse getMyInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		User user = userRepository.findByUsername(name).orElseThrow( () -> new AppException(ErrorCode.USER_NOTFOUND));
		return userMapper.toUserResponse(user);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> GetAllUser() {
		return userRepository.findAll().stream().map(userMapper :: toUserResponse).toList();	
	}

	@PostAuthorize("returnObject.username == authentication.name")
	public UserResponse GetUserById(String id) {
		return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException()));
	}
	
	
	public UserResponse UpdateUser(String userId, UserUpdateRequest updateRequest) {
		User user = userRepository.findById(userId).orElseThrow( () -> new AppException(ErrorCode.USER_NOTFOUND));
		
		userMapper.updateUser(user, updateRequest);
		user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
		
		var roles = roleRepository.findAllById(updateRequest.getRoles());
		
		user.setRoles(new HashSet<>(roles));
		
		return userMapper.toUserResponse(userRepository.save(user));
	}
	
	public void DeleteUser(String id) {
		userRepository.deleteById(id);
	}
	// Lấy tất cả users từ MongoDB
    public List<UserMongo> getAllUsersMongo() {
        return userMongoRepository.findAll();
    }
    // Lưu user vào MongoDB
    public UserMongo saveUserToMongo(UserMongo user) {
        return userMongoRepository.save(user);
    }
}
