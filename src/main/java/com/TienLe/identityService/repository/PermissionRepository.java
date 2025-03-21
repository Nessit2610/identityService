package com.TienLe.identityService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TienLe.identityService.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>{

}
