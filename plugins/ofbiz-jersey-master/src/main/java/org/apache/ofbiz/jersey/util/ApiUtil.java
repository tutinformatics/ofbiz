/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.jersey.util;

import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.common.authentication.api.AuthenticatorException;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.jersey.pojo.AuthenticationInput;
import org.apache.ofbiz.jersey.pojo.AuthenticationOutput;
import org.apache.ofbiz.jersey.response.Error;
import org.apache.ofbiz.jersey.response.Success;
import org.apache.ofbiz.webapp.control.JWTManager;
import org.jetbrains.annotations.NotNull;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.AesKey;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.security.Key;
import java.util.Map;

public class ApiUtil {

	private static final Key key = new AesKey("OfbizOfbizOfbizO".getBytes());

	/**
	 * @param message
	 * @return
	 */
	public static ResponseBuilder successResponse(int statusCode, String reasonPhrase, String message) {
		Success success = new Success(statusCode, reasonPhrase, message);
		return Response.status(statusCode).type(MediaType.APPLICATION_JSON)
				.entity(success);
	}

	/**
	 * @param message
	 * @return
	 */
	public static ResponseBuilder errorResponse(int statusCode, String reasonPhrase, String message) {
		Error error = new Error(statusCode, reasonPhrase, message);
		return Response.status(statusCode).type(MediaType.APPLICATION_JSON)
				.entity(error);
	}

	@NotNull
	public static AuthenticationOutput getAuthenticationOutput(Delegator delegator, AuthenticationInput user) throws AuthenticatorException {
		return new AuthenticationOutput(generateJweToAuthenticateUser(generateJwtToAuthenticateUser(delegator, user)), user.getUserLoginId());
	}

	public static String generateJweToAuthenticateUser(String body) throws AuthenticatorException {
		try {
			JsonWebEncryption jwe = new JsonWebEncryption();
			jwe.setPayload(body);
			jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
			jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
			jwe.setKey(key);
			return jwe.getCompactSerialization();
		} catch (Exception e) {
			throw new AuthenticatorException(e.getMessage());
		}
	}


	public static String getBodyFromJWE(String serializedJwe) throws AuthenticatorException {
		try {
			JsonWebEncryption jwe = new JsonWebEncryption();
			jwe.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, KeyManagementAlgorithmIdentifiers.A128KW));
			jwe.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256));
			jwe.setKey(key);
			jwe.setCompactSerialization(serializedJwe);
			return jwe.getPayload();
		} catch (Exception e) {
			throw new AuthenticatorException(e.getMessage());
		}
	}

	/**
	 * Generate JWT token from AuthenticationInput
	 **/
	public static String generateJwtToAuthenticateUser(Delegator delegator, AuthenticationInput user) {
		Map<String, String> claims = UtilMisc.toMap("userLoginId", user.getUserLoginId());
		claims.put("currentPassword", user.getCurrentPassword());
		return JWTManager.createJwt(delegator, claims, user.getUserLoginId() + user.getCurrentPassword(), 30 * 60);
	}

	public static String generateAdminToken(Delegator delegator) throws AuthenticatorException {

		return generateJweToAuthenticateUser(generateJwtToAuthenticateUser(delegator, AuthenticationInput.builder().userLoginId("admin").currentPassword("ofbiz").build()));

	}

	public static Map<String, Object> getClaimsFromToken(String jwtToken) throws InvalidJwtException {
		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
				.setSkipSignatureVerification()
				.build();

		JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtToken);
		return jwtClaims.getClaimsMap();
	}

	/**
	 * Throws AuthenticatorException when login is invalid
	 */
	public static void authenticateUserLogin(Delegator delegator, AuthenticationInput user) throws AuthenticatorException {

		if (user.getCurrentPasswordVerify() == null) {
			user.setCurrentPasswordVerify(user.getCurrentPassword());
		} else {
			if (!user.getCurrentPassword().equals(user.getCurrentPasswordVerify())) {
				throw new AuthenticatorException("passwords dont match");
			}
		}

		try {
			GenericValue userLogin = EntityQuery.use(delegator)
					.from("UserLogin")
					.where("userLoginId", user.getUserLoginId())
					.queryOne();
			if (!HashCrypt.comparePassword(userLogin.getString("currentPassword"), "", user.getCurrentPassword())) {
				throw new AuthenticatorException("passwords dont match");
			}
		} catch (Exception e) {
			throw new AuthenticatorException(e.getMessage());
		}
	}
}
