package com.TienLe.identityService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.TienLe.identityService.dto.request.UserCreationRequest;
import com.TienLe.identityService.dto.request.UserUpdateRequest;
import com.TienLe.identityService.dto.response.UserResponse;
import com.TienLe.identityService.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper{
	public User toUser(UserCreationRequest request);
	
	@Mapping(target = "roles", ignore = true)
	public void updateUser(@MappingTarget User user, UserUpdateRequest request);
	
	public UserResponse toUserResponse(User user);
}
