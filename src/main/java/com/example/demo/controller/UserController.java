package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")  // 회원가입
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){


        try{

            if(userDTO == null || userDTO.getPassword() == null){
                throw new RuntimeException("Invalid Password value.");
            }

            UserEntity userEntity = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .password(userDTO.getPassword())
                    .build();

            UserEntity registerUser = userService.create(userEntity);

            UserDTO responseUserDTO = userDTO.builder()
                    .id(registerUser.getId())
                    .username(registerUser.getUsername())
                    .password(registerUser.getPassword())
                    .build();

            return ResponseEntity.ok(responseUserDTO);

        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO){

        UserEntity user = userService.getByCredentials(
                userDTO.getUsername(), userDTO.getPassword(), passwordEncoder);

        if(user != null){

            String token = tokenProvider.create(user);

            log.info("token: {}", token);

            UserDTO responseUserDTO = UserDTO.builder()
                    .username(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();

            return ResponseEntity.ok(responseUserDTO);

        }else{
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login Error")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
