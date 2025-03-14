package com.TienLe.identityService.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TienLe.identityService.dto.request.APIResponse;
import com.TienLe.identityService.dto.request.RoleRequest;
import com.TienLe.identityService.dto.response.RoleResponse;
import com.TienLe.identityService.exception.AppException;
import com.TienLe.identityService.exception.ErrorCode;
import com.TienLe.identityService.mapper.RoleMapper;
import com.TienLe.identityService.repository.PermissionRepository;
import com.TienLe.identityService.repository.RoleRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	
	public RoleResponse create(RoleRequest request) {
		var role = roleMapper.toRole(request); 
		var permissions = permissionRepository.findAllById(request.getPermissions());
		role.setPermissions(new HashSet<>(permissions));
		role = roleRepository.save(role);
		System.out.println(role.toString());
		return roleMapper.toRoleResponse(role); 
	}
	
	public List<RoleResponse> getAll(){
		var roles = roleRepository.findAll();
		return roles.stream().map(roleMapper::toRoleResponse).toList(); 
	}
	
	public void detele(String roleName) {
		roleRepository.deleteById(roleName);
	}
	
	public RoleResponse updatePermissionsForRole(RoleRequest request) {
	    var role = roleRepository.findById( request.getName())
	            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
	    var permissions = permissionRepository.findAllById(request.getPermissions());
	    role.setPermissions(new HashSet<>(permissions));
	    role = roleRepository.save(role);
	    return roleMapper.toRoleResponse(role);
	}	

}
