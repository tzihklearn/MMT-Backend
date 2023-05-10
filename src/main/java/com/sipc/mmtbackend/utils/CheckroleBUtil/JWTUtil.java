package com.sipc.mmtbackend.utils.CheckroleBUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sipc.mmtbackend.pojo.domain.UserB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * JWT 相关工具类
 * @author DoudiNCer
 */
@Component
@Slf4j
public class JWTUtil {
    // 盐
    private static final String tokenPara = "p.iT|OnYm]:IRr1rW{E/~<5o_r+_ h@Eel/k kK?1n{|heX[Q4Tj_I#!*K8P=Y+Ru@";

    private static final String BUserIdTokenKey = "buidtkk";
    public static final String BUserStuIdTokenKey = "busidtkk";

    /**
     * 生成 Token
     * @param user B端用户信息（只需ID和学号）
     * @return Token
     */
    public String createToken(UserB user) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, 7);
        return
                JWT.create()
                .withClaim(BUserIdTokenKey, user.getId())
                .withClaim(BUserStuIdTokenKey, user.getStudentId())
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC512(tokenPara));
    }

    /**
     * 解析 Token
     * @param token Token字符串
     * @return UserB，包含用户ID与学号，解析错误返回 null
     */
    private UserB unMarshellToken(String token){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(tokenPara)).build();
        DecodedJWT verify;
        try {
            verify = verifier.verify(token);
        } catch (com.auth0.jwt.exceptions.JWTVerificationException e) {
            log.info("Varify Token \"" + token + "\" Error: " + e.getMessage());
            return null;
        }
        UserB result = new UserB();
        result.setId(verify.getClaim(BUserIdTokenKey).asInt());
        result.setStudentId(verify.getClaim(BUserStuIdTokenKey).asString());
        return result;
    }
}
