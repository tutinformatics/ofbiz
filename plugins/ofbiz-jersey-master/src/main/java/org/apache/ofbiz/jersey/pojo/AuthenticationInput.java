package org.apache.ofbiz.jersey.pojo;

import lombok.Data;

@Data
public class AuthenticationInput {

    private String userLoginId;
    private String currentPassword;
    private String currentPasswordVerify;

    public AuthenticationInput() {
    }
}
