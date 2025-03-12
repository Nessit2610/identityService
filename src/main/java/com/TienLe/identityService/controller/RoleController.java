package com.TienLe.identityService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TienLe.identityService.dto.request.APIResponse;
import com.TienLe.identityService.dto.request.RoleRequest;
import com.TienLe.identityService.dto.response.RoleResponse;
import com.TienLe.identityService.service.RoleService;



@RestController
@RequestMapping("/roles")
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	
	@PostMapping
	public APIResponse<RoleResponse> create(@RequestBody RoleRequest request){
		return APIResponse.<RoleResponse>builder()
				.result(roleService.create(request))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<RoleResponse>> getAll(){
		return APIResponse.<List<RoleResponse>>builder()
				.result(roleService.getAll())
				.build();
	}
	
	@DeleteMapping("{roleName}")
	public APIResponse<Void> Delete(@PathVariable("roleName") String roleName){
		roleService.detele(roleName);
		return APIResponse.<Void>builder().build();
	}
	
	
	
	
	
	
	
	
}
