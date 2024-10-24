package com.pipa.back.service.implement;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pipa.back.common.CertificationNumber;
import com.pipa.back.dto.request.auth.EmailCertificationRequestDto;
import com.pipa.back.dto.request.auth.IdCheckRequestDto;
import com.pipa.back.dto.response.ResponseDto;
import com.pipa.back.dto.response.auth.EmailCertificationResponseDto;
import com.pipa.back.dto.response.auth.IdCheckResponseDto;
import com.pipa.back.entity.CertificationEntity;
import com.pipa.back.provider.EmailProvider;
import com.pipa.back.repository.CertificationRepository;
import com.pipa.back.repository.UserRepository;
import com.pipa.back.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;
    private final EmailProvider emailProvider;

    @Override
    public ResponseEntity<? super IdCheckResponseDto> idCheck(IdCheckRequestDto dto) {
        try {

            String userId = dto.getId();
            boolean isExistId = userRepository.existsByUserId( userId );

            if( isExistId ) return IdCheckResponseDto.duplicateId();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        } 
        return IdCheckResponseDto.success();
        
    }

    @Override
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(EmailCertificationRequestDto dto) {
        try {

            /// id 중복 체크
            String userId = dto.getId();
            boolean isExistId = userRepository.existsByUserId( userId );
            if( isExistId ) return EmailCertificationResponseDto.duplicateId();

            // email 전송 부분
            String certificationNumer = CertificationNumber.getCertificationNumber();
            String email = dto.getEmail();
            boolean isSuccessed = emailProvider.sendCertificationMail(email, certificationNumer);
            if( !isSuccessed ) return EmailCertificationResponseDto.mailSendFail();

            CertificationEntity certificationEntity = new CertificationEntity(userId, email, certificationNumer);
            certificationRepository.save( certificationEntity );

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        } 
        return EmailCertificationResponseDto.success();

    }

    
}
