package com.main.backendtest.controllers;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.main.backendtest.dtos.request.AuthUserDto;
import com.main.backendtest.dtos.request.RegisterUserDto;
import com.main.backendtest.entities.User;
import com.main.backendtest.services.JwtService;
import com.main.backendtest.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(
            @RequestBody @Valid RegisterUserDto dto) {
        this.userService.register(dto);

        return ResponseEntity.status(201)
                .body(Collections.singletonMap("message", "Register success."));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authUser(@RequestBody @Valid AuthUserDto dto) {
        User userAuthenticated = this.userService.auth(dto);

        String tokenGenerate = this.jwtService.sign(userAuthenticated.getId());

        return ResponseEntity.ok().body(Collections.singletonMap("token", tokenGenerate));
    }
}
