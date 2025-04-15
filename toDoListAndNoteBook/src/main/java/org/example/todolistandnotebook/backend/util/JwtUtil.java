package org.example.todolistandnotebook.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {

    private String key = "JwtDemo";

    /**
     * 生成令牌
     */
    public String GenJwt(Map<String, Object> claims){
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,key)//签名算法,后面一部分为秘钥
                .setClaims(claims)//载荷
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000 * 12))//有效时间，这里的1000单位为ms
                .compact();
    }

    /**
     * 解析令牌
     */
    public Claims ParseJwt(String s){
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(s)
                .getBody();
        return claims;
    }

    /**
     * 解析令牌并返回用户id
     */
    public Integer getIdFromJwt(String s){
        s = s.substring(7);
        Claims claims = ParseJwt(s);
//        log.info(String.valueOf((Integer) claims.get("id")));
        return (Integer) claims.get("id");
    }
}
