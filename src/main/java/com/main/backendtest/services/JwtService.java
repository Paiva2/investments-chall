package com.main.backendtest.services;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

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
            System.err.println(exception);
            throw new BadRequestException("Error while signing token...");
        }

        return token;
    }

    public String verify(String token) {
        String decodedJWT = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);

            JWTVerifier verifier = JWT.require(algorithm).withIssuer(this.issuer).build();

            decodedJWT = verifier.verify(token).getSubject();
        } catch (JWTVerificationException exception) {
            System.err.println(exception);
            throw new BadRequestException("Error while verifying token...");
        }

        return decodedJWT;
    }

    protected Instant setTokenExp() {
        ZoneId zoneId = ZoneId.systemDefault();

        return Instant.now().plusSeconds(expTime).atZone(zoneId).toInstant();
    }
}
