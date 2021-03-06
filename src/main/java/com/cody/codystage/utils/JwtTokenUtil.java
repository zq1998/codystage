package com.cody.codystage.utils;

import com.cody.codystage.common.constants.AuthConstants;

import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;


/**
 * @Classname JwtTokenUtil
 * @Description TODO
 * @Date 2019/4/17 23:25
 * @Created by ZQ
 */
public class JwtTokenUtil {

    public static String createToken(Long userId, String role,boolean isRemember) {
        long expiration = isRemember ? AuthConstants.EXPIRATION_REMEMBER : AuthConstants.EXPIRATION;
        Map<String,Object> map= Maps.newHashMap();
        map.put(AuthConstants.ROLE_CLAIMS,role);
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, AuthConstants.SECRET)
                .setClaims(map)
                .setIssuer(AuthConstants.ISS)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * expiration))
                .compact();
    }

    /**
     * 从token获取用户ID
     * @param token
     * @return
     */
    public static Long getUserId(String token) {
        return Long.valueOf(getTokenBody(token).getSubject());
    }

    /**
     * 获取用户角色
     * @param token
     * @return
     */
    public static String getUserRole(String token){
        return (String) getTokenBody(token).get(AuthConstants.ROLE_CLAIMS);
    }

    /**
     * 是否过期
     *
     * @param token
     * @return
     */
    public static boolean isExpiration(String token) {
        return getTokenBody(token).getExpiration().before(new Date());
    }

    private static Claims getTokenBody(String token) {
        return Jwts.parser()
                .setSigningKey(AuthConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public static Long getUserId(HttpServletRequest request){
        String tokenHeader= request.getHeader("Authorization");
        String token = tokenHeader.replace(AuthConstants.TOKEN_PREFIX, "");
        return getUserId(token);
    }
}
