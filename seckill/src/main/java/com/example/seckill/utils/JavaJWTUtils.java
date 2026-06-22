package com.example.seckill.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.HashMap;
import java.util.Map;

public class JavaJWTUtils {
    /**
     * 生成密钥
     */
    public static final String JWT_SECRET = "iQ1xE9zH5hM8eW1eV9mB";

    /**
     * 生成JWT token
     * @param user_id 用户ID
     * @param account 账号
     * @param password 密码
     */
    public static String createToken(String user_id,String account,String password){
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");;

        return JWT.create()
                //设置头部信息
                .withHeader(header)
                //负载
                .withClaim("user_id", user_id)
                .withClaim("account", account)
                .withClaim("password", password)
                //签名
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(JWT_SECRET));
    }

    /**
     * 验证JWT token
     * @param token 要验证的token
     */
    public static boolean verifyToken(String token){
        try{
            JWTVerifier jwtVerifier=JWT.require(Algorithm.HMAC256(JWT_SECRET)).build();
            jwtVerifier.verify(token);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从JWT token中获取数据
     * @param token 要解析的token
     * @return 解析后的数据
     */
    public static String parseToken(String token) {
        try{
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(JWT_SECRET)).build();
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
            Claim user_id=decodedJWT.getClaim("user_id");
            return user_id.asString();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
