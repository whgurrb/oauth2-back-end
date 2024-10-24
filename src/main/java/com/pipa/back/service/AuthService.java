package com.pipa.back.service;

import org.springframework.http.ResponseEntity;

import com.pipa.back.dto.request.auth.IdCheckRuestDto;
import com.pipa.back.dto.response.auth.IdCheckResponseDto;

public interface AuthService {
    ResponseEntity<? super IdCheckResponseDto> idCheck(IdCheckRuestDto dto) ;
}
