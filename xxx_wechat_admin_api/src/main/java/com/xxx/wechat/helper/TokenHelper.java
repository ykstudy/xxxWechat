package com.xxx.wechat.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxx.wechat.admin.dto.LoginReq;
import com.xxx.wechat.config.AppConfig;
import com.xxx.wechat.core.exception.AppException;

@Component
public class TokenHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(TokenHelper.class);

	private static final String JWT_SECRET = "7786df7fc3a34e26a61c034d5ec8245d";

	@Autowired
	private AppConfig appConfig;

	/**
	 * 
	 * @param id
	 * @param subject
	 * 
	 * @param issuer
	 *            发行人
	 * @param audience
	 *            角色
	 * @param ttlMillis
	 *            有效时间
	 * @return
	 */
	public String createJWT(LoginReq admin, Date loginDate) {

		if (admin == null) {
			return null;
		}
		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		JwtBuilder builder = Jwts.builder().setId(appConfig.appTokenIssuer)
				.setIssuedAt(loginDate)// 发行时间
				.setSubject(generalSubject(admin))// 抽象主题
				.setIssuer(appConfig.appTokenIssuer)// 发行人
				.setAudience(admin.getRoleId())// 角色
				.signWith(signatureAlgorithm, generalKey());

		if (appConfig.appTokenActiveTime >= 0) {
			Date exp = new Date(System.currentTimeMillis()
					+ appConfig.appTokenActiveTime);
			builder.setExpiration(exp);
		}

		return builder.compact();
	}

	/**
	 * 
	 * @param admin
	 * @param loginDate
	 * @return
	 */
	public String createJWT(LoginReq admin) {

		if (admin == null) {
			return null;
		}
		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		JwtBuilder builder = Jwts.builder().setId(appConfig.appTokenIssuer)
				.setIssuedAt(new Date(admin.getLoginTime()))// 发行时间
				.setSubject(generalSubject(admin))// 抽象主题
				.setIssuer(appConfig.appTokenIssuer)// 发行人
				.setAudience(admin.getRoleId())// 角色
				.signWith(signatureAlgorithm, generalKey());

		if (appConfig.appTokenActiveTime >= 0) {
			Date exp = new Date(System.currentTimeMillis()
					+ appConfig.appTokenActiveTime);
			builder.setExpiration(exp);
		}

		return builder.compact();
	}

	public LoginReq parseJWT(String jwt) throws AppException {
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(generalKey())
					.parseClaimsJws(jwt).getBody();
		} catch (ExpiredJwtException e) {
			logger.error("登录超时");
			return null;
		} catch (SignatureException | MalformedJwtException
				| UnsupportedJwtException e) {
			logger.error(e.getMessage());
			logger.error("解析失败", e);
			throw new AppException(e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			logger.error("参数异常", e);
			throw new AppException(e);
		}
		return JSON.parseObject(claims.getSubject(), LoginReq.class);

	}

	/**
	 * 由字符串生成加密key
	 * 
	 * @return
	 */
	private SecretKey generalKey() {
		byte[] encodedKey = Base64.decodeBase64(appConfig.appTokenIssuer
				+ JWT_SECRET);
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length,
				"AES");
		return key;
	}

	/**
	 * 生成subject信息
	 * 
	 * @param user
	 * @return
	 */
	public static String generalSubject(LoginReq user) {
		JSONObject jo = new JSONObject();
		jo.put("name", user.getName());
		jo.put("roleId", user.getRoleId());
		jo.put("loginTime", user.getLoginTime());
		return jo.toJSONString();
	}
}
