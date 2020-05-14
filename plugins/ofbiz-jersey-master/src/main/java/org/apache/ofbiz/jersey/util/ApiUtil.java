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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.common.authentication.api.AuthenticatorException;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.jersey.pojo.AuthenticationInput;
import org.apache.ofbiz.jersey.pojo.AuthenticationOutput;
import org.apache.ofbiz.jersey.response.Error;
import org.apache.ofbiz.jersey.response.Success;
import org.jetbrains.annotations.NotNull;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.security.Key;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.ofbiz.jersey.util.JsonUtils.parseJson;

public class ApiUtil {

	private static final Key key = new AesKey("OfbizOfbizOfbizO".getBytes());
	private static final String OFBIZ_SIGNATURE = "Apache Ofbiz";
	private static final String TARGET_AUDIENCE = "To whom it may concern";
	private static final String OFBIZ_GROUPS = "groups";
	private static RsaJsonWebKey rsaJsonWebKey;
	private static Delegator delegator;

	static {
		try {
			rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
			rsaJsonWebKey.setKeyId("k1");
		} catch (JoseException e) {
			e.printStackTrace();
		}
	}


	private static final ObjectMapper mapper = new ObjectMapper();
	public static final String SECRET_FIELD_NAME = "secret";

	/**
	 * @param message
	 * @return
	 */
	public static ResponseBuilder successResponse(int statusCode, String reasonPhrase, String message) {
		Success success = new Success(statusCode, reasonPhrase, message);
		return Response.status(statusCode).type(MediaType.APPLICATION_JSON)
				.entity(success);
	}

	public static void invokeDelegator(Delegator del) {
		if (delegator == null) {
			delegator = del;
		}
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
	public static AuthenticationOutput getAuthenticationOutput(AuthenticationInput user) throws AuthenticatorException, JoseException {
		return new AuthenticationOutput(generateJwsFromJwe(generateJweToAuthenticateUser(generateBodyForJWE(user)), user), user.getUserLoginId());
	}

	public static String generateJwsFromJwe(String secret, AuthenticationInput user) throws JoseException {

		JwtClaims claims = new JwtClaims();
		claims.setIssuer(OFBIZ_SIGNATURE);
		claims.setAudience(TARGET_AUDIENCE);
		claims.setExpirationTimeMinutesInTheFuture(10);
		claims.setGeneratedJwtId();
		claims.setIssuedAtToNow();
		claims.setNotBeforeMinutesInThePast(2);
		claims.setClaim(SECRET_FIELD_NAME, secret);
		List<String> groups = new ArrayList<>();

		try {
			GenericValue userLogin = EntityQuery.use(delegator)
					.from("UserLogin")
					.where("userLoginId", user.getUserLoginId())
					.queryOne();

			userLogin.getRelated("UserLoginSecurityGroup", null, null, false)
					.stream()
					.filter(x -> x.get("fromDate") == null || ((Timestamp) x.get("fromDate")).getTime() <= System.currentTimeMillis())
					.forEach(x -> groups.add(x.getString("groupId")));

		} catch (Exception e) {
			e.printStackTrace();
		}

		claims.setStringListClaim(OFBIZ_GROUPS, groups);

		JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setKey(rsaJsonWebKey.getPrivateKey());
		jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
		return jws.getCompactSerialization();

	}

	private static String generateJweToAuthenticateUser(String body) throws AuthenticatorException {
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


	public static String getBodyFromJwe(String serializedJwe) throws AuthenticatorException {
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

	private static String generateBodyForJWE(AuthenticationInput user) throws AuthenticatorException {
		try {
			Map<String, String> claims = new HashMap<>();
			claims.put("userLoginId", user.getUserLoginId());
			claims.put("currentPassword", user.getCurrentPassword());
			return mapper.writeValueAsString(claims);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthenticatorException(e.getMessage());
		}
	}


	public static String generateAdminToken() throws AuthenticatorException, JoseException {
		AuthenticationInput user = AuthenticationInput.builder().userLoginId("admin").currentPassword("ofbiz").build();
		return generateJwsFromJwe(generateJweToAuthenticateUser(generateBodyForJWE(user)), user);

	}

	/**
	 * Returns claims from token if token is valid
	 **/
	public static Map<String, Object> getClaimsFromJwtToken(String jwtToken) throws InvalidJwtException {

		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
				.setRequireExpirationTime()
				.setAllowedClockSkewInSeconds(30)
				.setExpectedIssuer(OFBIZ_SIGNATURE)
				.setExpectedAudience(TARGET_AUDIENCE)
				.setVerificationKey(rsaJsonWebKey.getKey())
				.setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256)
				.build();

		JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtToken);

		return jwtClaims.getClaimsMap();

	}

	/**
	 * Throws AuthenticatorException when login is invalid
	 */
	public static void authenticateUserLogin(AuthenticationInput user) throws
			AuthenticatorException {

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

	/**
	 * Returns user data from jwt
	 **/
	public static Map<String, Object> getInnerClaimsFromJwt(String jwtToken) throws Exception {
		return parseJson(getBodyFromJwe((String) getClaimsFromJwtToken(jwtToken).get(SECRET_FIELD_NAME)));
	}
}
