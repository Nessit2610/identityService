package com.TienLe.identityService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.TienLe.identityService.dto.request.RoleRequest;
import com.TienLe.identityService.dto.response.RoleResponse;
import com.TienLe.identityService.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	
	@Mapping(target = "permissions", ignore = true)
	public Role toRole(RoleRequest request);
	
	public RoleResponse toRoleResponse(Role role);
}
