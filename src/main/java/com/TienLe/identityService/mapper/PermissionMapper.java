package com.TienLe.identityService.mapper;

import org.mapstruct.Mapper;

import com.TienLe.identityService.dto.request.PermissionRequest;
import com.TienLe.identityService.dto.response.PermissionResponse;
import com.TienLe.identityService.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

	public Permission toPermission(PermissionRequest request);
	public PermissionResponse toPermissionResponse(Permission permission);
}
