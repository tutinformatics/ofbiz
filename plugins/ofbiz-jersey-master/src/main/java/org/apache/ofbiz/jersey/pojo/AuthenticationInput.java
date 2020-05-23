package org.apache.ofbiz.jersey.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationInput {

	private String userLoginId;
	private String currentPassword;
	private String currentPasswordVerify;

}
