package com.pipa.back.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pipa.back.dto.request.auth.IdCheckRuestDto;
import com.pipa.back.dto.response.auth.IdCheckResponseDto;
import com.pipa.back.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/id-check")
    public ResponseEntity<? super IdCheckResponseDto> idCheck( 
        @RequestBody @Valid IdCheckRuestDto dto
    ){
        ResponseEntity<? super IdCheckResponseDto> responseBody = authService.idCheck( dto );
        return responseBody;
    }    
    
}
