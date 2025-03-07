package com.TienLe.identityService.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TienLe.identityService.dto.request.UserCreationRequest;
import com.TienLe.identityService.dto.request.UserUpdateRequest;
import com.TienLe.identityService.dto.response.UserResponse;
import com.TienLe.identityService.entity.User;
import com.TienLe.identityService.entity.UserMongo;
import com.TienLe.identityService.enums.Roles;
import com.TienLe.identityService.exception.AppException;
import com.TienLe.identityService.exception.ErrorCode;
import com.TienLe.identityService.mapper.UserMapper;
import com.TienLe.identityService.repository.UserMongoRepository;
import com.TienLe.identityService.repository.UserRepository;



@Service
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

		HashSet<String> roles = new HashSet<String>();
		roles.add(Roles.USER.name());
		user.setRoles(roles);
		
		return userMapper.toUserResponse(userRepository.save(user));
		 
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	public List<User> GetAllUser() {
		return userRepository.findAll();	
	}
	
	
	
	public User GetUserById(String id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException());
	}
	
	
	public User UpdateUser(String userId, UserUpdateRequest updateRequest) {
		User user = GetUserById(userId);
		
		userMapper.updateUser(user, updateRequest);
		
//		user.setFirstName(updateRequest.getFirstName());
//		user.setLastName(updateRequest.getLastName());
//		user.setPassword(updateRequest.getPassword());
//		user.setDob(updateRequest.getDob());
		
		return userRepository.save(user);
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
