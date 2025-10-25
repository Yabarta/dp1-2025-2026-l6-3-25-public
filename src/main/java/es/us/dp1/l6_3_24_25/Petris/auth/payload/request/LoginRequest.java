package es.us.dp1.l6_3_24_25.Petris.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

	@NotBlank
	private String username;

	@NotBlank
	private String password;

}
