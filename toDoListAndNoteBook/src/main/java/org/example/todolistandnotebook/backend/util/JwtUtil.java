package org.example.todolistandnotebook.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.sql.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成令牌
     */
    public String GenJwt(Map<String, Object> claims){

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,"JwtDemo")//签名算法,后面一部分为秘钥
                .setClaims(claims)//载荷
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))//有效时间，这里的1000单位为ms
                .compact();
    }

    /**
     * 解析令牌
     */
    public Claims ParseJwt(String s){
        Claims claims = Jwts.parser()
                .setSigningKey("JwtDemo")
                .parseClaimsJws(s)
                .getBody();
        return claims;
    }
}
