package com.TienLe.identityService.dto.request;

import java.util.Set;

import com.TienLe.identityService.entity.Permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleRequest {
	
	private String name;
	private String description;
	private Set<String> permissions;
}