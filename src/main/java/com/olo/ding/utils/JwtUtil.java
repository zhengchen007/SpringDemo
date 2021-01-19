package com.olo.ding.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {
	private static final long	EXPIRE_TIEM		= 1 * 60 * 1000;
	public static String		TOKEN_SECRET	= "f2fbeb3c-07e0-41e2-8dd2-1a49e77d6d69";

	public static String sign(String userId) {
		try {
			Date iatDate = new Date();
			// 过期时间
			//Date date = new Date(System.currentTimeMillis() + EXPIRE_TIEM);
			// 私钥及加密算法
			//Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
			// 设置头部信息
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("alg", "HS256");
			header.put("typ", "JWT");
			header.put("uid", userId);

			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

			// 添加构成JWT的参数

			JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT").setPayload(new Gson().toJson(header))
					.signWith(signatureAlgorithm, TOKEN_SECRET.getBytes()); // 估计是第三段密钥
			// 生成JWT
			return builder.compact();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("加密失败！"+e);
			return null;
		}

	}

	public static boolean verify(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT jwt = verifier.verify(token);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return false;
		}

	}
}
