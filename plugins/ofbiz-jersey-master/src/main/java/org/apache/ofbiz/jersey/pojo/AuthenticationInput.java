package org.apache.ofbiz.jersey.pojo;

import lombok.Data;

@Data
public class AuthenticationInput {

    private String username;
    private String password;

    public AuthenticationInput() {
    }
}
