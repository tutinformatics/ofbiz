package org.apache.ofbiz.jersey.pojo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthenticationInput extends Throwable {

    private String userLoginId;
    private String currentPassword;
    private String currentPasswordVerify;

    public AuthenticationInput() {
    }

    public AuthenticationInput(String userLoginId, String currentPassword, String currentPasswordVerify) {
        this.userLoginId = userLoginId;
        this.currentPassword = currentPassword;
        this.currentPasswordVerify = currentPasswordVerify;
    }
}
