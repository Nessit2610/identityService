package com.TienLe.identityService.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {

	@Size(min = 5, message = "USERNAME_INVALID")
	private String username;
	
	@Size(min = 8, message = "PASSWORD_INVALID")
	private String password;
	
	private String firstName;
	private String lastName;
	private LocalDate dob;
	
	
	
}
