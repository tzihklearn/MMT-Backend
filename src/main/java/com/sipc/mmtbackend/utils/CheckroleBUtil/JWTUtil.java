package com.sipc.mmtbackend.utils.CheckroleBUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * JWT 相关工具类
 *
 * @author DoudiNCer
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class JWTUtil {
    // 盐
    private static final String tokenPara = "p.iT|OnYm]:IRr1rW{E/~<5o_r+_ h@Eel/k kK?1n{|heX[Q4Tj_I#!*K8P=Y+Ru@";
    private static final String BUserIdTokenKey = "buidtkk";
//    private static final String BUserStuIdTokenKey = "busidtkk";
//    private static final String BUserRoleIdTokenKey = "busroleidtkk";

    private static final String BUserOrganizationIdTokenKey = "busorganizationidtkk";

    private static final String BUserPermissionIdTokenKey = "buspermissionidtkk";

    private final RedisUtil redisUtil;

    /**
     * 生成 Token
     *
     * @param po B端 Token 交换专用类
     * @return Token
     */
    public String createToken(BTokenSwapPo po) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, 7);
        String token = JWT.create()
                .withClaim(BUserIdTokenKey, po.getUserId())
//                .withClaim(BUserStuIdTokenKey, po.getStudentId())
                .withClaim(BUserOrganizationIdTokenKey, po.getOrganizationId())
                .withClaim(BUserPermissionIdTokenKey, po.getPermissionId())
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC512(tokenPara));
        String tokenKey = getTokenKey(token);
        if (tokenKey == null)
            return null;
        boolean b = redisUtil.setString(tokenKey, po, 7, TimeUnit.DAYS);
        if (!b)
            return null;
        return token;
    }

    /**
     * 解析 Token
     *
     * @param token Token字符串
     * @return UserB，包含用户ID、学号、角色ID，解析错误返回 null
     * @author DoudiNCer
     */
    private BTokenSwapPo unMarshellToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(tokenPara)).build();
        DecodedJWT verify;
        try {
            verify = verifier.verify(token);
        } catch (com.auth0.jwt.exceptions.JWTVerificationException e) {
            log.info("Varify Token \"" + token + "\" Error: " + e.getMessage());
            return null;
        }
        BTokenSwapPo result = new BTokenSwapPo();
        result.setUserId(verify.getClaim(BUserIdTokenKey).asInt());
//        result.setStudentId(verify.getClaim(BUserStuIdTokenKey).asString());
//        result.setRoleId(verify.getClaim(BUserRoleIdTokenKey).asInt());
        result.setOrganizationId(verify.getClaim(BUserOrganizationIdTokenKey).asInt());
        result.setPermissionId(verify.getClaim(BUserPermissionIdTokenKey).asInt());
        return result;
    }

    /**
     * 验证 Token
     *
     * @param token JWT Token 字符串
     * @return 解析到的 Token 载荷
     * @author doudiNCer
     */
    public BTokenSwapPo verifyToken(String token) {
        BTokenSwapPo po = unMarshellToken(token);
        if (po == null)
            return null;
        String tokenKey = getTokenKey(token);
        if (tokenKey == null) {
            return null;
        }
        BTokenSwapPo redisPo = redisUtil.getString(tokenKey, BTokenSwapPo.class);
        if (redisPo == null) {
            log.warn("发现解析成功但未登录的Token：" + po);
            return null;
        }
        redisPo.setToken(token);
        return redisPo;
    }

    /**
     * 获取 Token 在 Redis 中的 key
     *
     * @param token JWT Token
     * @return key 或 null（系统错误）
     * @author DoudiNCer
     */
    private String getTokenKey(String token) {
        String tokenKey;
        try {
            MessageDigest md5Digest;
            md5Digest = MessageDigest.getInstance("MD5");
            tokenKey = Base64.encodeBase64String(md5Digest.digest(token.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            log.warn("Get Algorithm Error When Process Token Redis Key: " + e.getMessage());
            return null;
        }
        return tokenKey;
    }

    /**
     * 吊销 Token
     *
     * @param token Token字符串
     * @return 吊销结果，true表示吊销正常，null表示系统错误
     */
    public Boolean revokeToken(String token) {
        String tokenKey = getTokenKey(token);
        if (tokenKey == null) {
            return null;
        }
//        BTokenSwapPo po = verifyToken(token);
//        if (po == null){
//            log.info("尝试吊销非法 Token：" + token);
//            return false;
//        }
        boolean sel = redisUtil.delete(tokenKey);
        if (!sel) {
            log.warn("Redis 删除 Token 失败，Token 为：" + token + "key 为：" + tokenKey);
        }
        return true;
    }
}
