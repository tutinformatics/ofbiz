package org.apache.ofbiz.jersey.pojo;

import lombok.Data;

@Data
public class AuthenticationOutput {

    private String token;
    private String userLoginId;

    public AuthenticationOutput(String token, String userLoginId) {
        this.token = token;
        this.userLoginId = userLoginId;
    }
}
