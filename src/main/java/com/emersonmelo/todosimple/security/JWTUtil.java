package com.emersonmelo.todosimple.security;

import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

//@Component = marca esta classe como um componente gerenciado pelo Spring
@Component
public class JWTUtil {
    
    //recebe valores do application.properties que é uma chave secreta
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    //recebe um nome de usuário e gera um token JWT para esse usuário
    public String generateToken(String username){
        SecretKey key = getKeyBySecret();
        return Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + this.expiration))
            .signWith(key)
            .compact();
    }

    //retorna a chave secreta utilizada para assinar e verificar os tokens JWT
    private SecretKey getKeyBySecret() {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());
        return key;
    }

    //Ele verifica se o token contém reivindicações (claims), se o nome de usuário está presente e se o tempo de expiração ainda não foi ultrapassado
    //Recebe um token JWT e verifica se é válido
    public boolean isValidToken(String token){
        Claims claims = getClaims(token);
        if(Objects.nonNull(claims)){
            String username = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            if (Objects.nonNull(username) && Objects.nonNull(expirationDate) && now.before(expirationDate)) {
                return true;
            }
        }
        return false;
    }

    //este método é usado para recuperar o nome de usuário de um token JWT válido
    public String getUsername(String token){
        Claims claims = getClaims(token);
        if (Objects.nonNull(claims)) 
            return claims.getSubject();
        return null;
    }

    //Ele verifica a assinatura do token usando a chave secreta e, se a assinatura for válida, retorna as reivindicações contidas no token
    private Claims getClaims(String token){
        SecretKey key = getKeyBySecret();
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
