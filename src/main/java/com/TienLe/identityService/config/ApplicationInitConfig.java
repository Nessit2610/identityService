package com.TienLe.identityService.config;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.TienLe.identityService.entity.User;
import com.TienLe.identityService.enums.Roles;
import com.TienLe.identityService.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository) {
		return args -> {
			if(userRepository.findByUsername("adminrole").isEmpty()) {
				
				var roles = new HashSet<String>();
				roles.add(Roles.ADMIN.name());
				User user = User.builder()
						.username("adminrole")
						.password(passwordEncoder.encode("adminrole"))
						.roles(roles)
						.build();
				userRepository.save(user);
				log.warn("admin user has been created with default : adminrole ");
			}
		};
	}
}
