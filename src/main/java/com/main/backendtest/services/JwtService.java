package com.main.backendtest.services;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import com.main.backendtest.exceptions.BadRequestException;

@Component
public class JwtService {
    @Value("${api.security.token.secret}")
    private String secret;

    private String issuer;

    private long expTime = 60 * 60 * 24 * 7; // 7d

    public String sign(UUID subject) {
        String token = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);

            token = JWT.create().withIssuer(this.issuer).withSubject(subject.toString())
                    .withExpiresAt(this.setTokenExp()).sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new BadRequestException("Error while signing token...");
        }

        return token;
    }

    protected Instant setTokenExp() {
        ZoneId zoneId = ZoneId.systemDefault();

        Instant exp = Instant.now().plusSeconds(expTime).atZone(zoneId).toInstant();

        return exp;
    }
}
