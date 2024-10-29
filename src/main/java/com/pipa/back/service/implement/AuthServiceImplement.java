package com.pipa.back.service.implement;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pipa.back.common.CertificationNumber;
import com.pipa.back.dto.request.auth.CheckCertificationRequestDto;
import com.pipa.back.dto.request.auth.EmailCertificationRequestDto;
import com.pipa.back.dto.request.auth.IdCheckRequestDto;
import com.pipa.back.dto.request.auth.SignInRequestDto;
import com.pipa.back.dto.request.auth.SignUpRequestDto;
import com.pipa.back.dto.response.ResponseDto;
import com.pipa.back.dto.response.auth.CheckCertificationResponseDto;
import com.pipa.back.dto.response.auth.EmailCertificationResponseDto;
import com.pipa.back.dto.response.auth.IdCheckResponseDto;
import com.pipa.back.dto.response.auth.SignInResponseDto;
import com.pipa.back.dto.response.auth.SignUpResponseDto;
import com.pipa.back.entity.CertificationEntity;
import com.pipa.back.entity.UserEntity;
import com.pipa.back.provider.EmailProvider;
import com.pipa.back.provider.JwtProvider;
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
    private final JwtProvider jwtProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    @Override
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification(CheckCertificationRequestDto dto) {
        try {

            String userId = dto.getId();
            String email = dto.getEmail();
            String certificationNumber = dto.getCertificationNumber();

            CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);
            if( certificationEntity == null ) return CheckCertificationResponseDto.certificationFail();

            boolean isMatched = certificationEntity.getEmail().equals( email ) && 
                                certificationEntity.getCertificationNumber().equals( certificationNumber );
            if( !isMatched ) return CheckCertificationResponseDto.certificationFail();

            
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return CheckCertificationResponseDto.success();
    }

    @Override
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto) {

        try {

            String userId = dto.getId();
            boolean isExistId = userRepository.existsByUserId( userId );
            if( isExistId ) return SignUpResponseDto.duplicateId();




            String email = dto.getEmail();
            String certificationNumber = dto.getCertificationNumber();            
        
            /// certification 체크
            CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);
            if( certificationEntity == null ) return SignUpResponseDto.certificationFail();

            boolean isMatched = certificationEntity.getEmail().equals( email ) && 
                                certificationEntity.getCertificationNumber().equals( certificationNumber );
            if( !isMatched ) return SignUpResponseDto.certificationFail();


            String password = dto.getPassword();
            String encodedPassword = passwordEncoder.encode( password );



            // User테이블에 저장
            //UserEntity userEntity = new UserEntity( userId, encodedPassword, email, "app", "ROLE_USER" );
            dto.setPassword(encodedPassword);
            UserEntity userEntity = new UserEntity( dto );

            userRepository.save(userEntity);

            // Certification테이블에 삭제
            // certificationRepository.delete(certificationEntity);
            certificationRepository.deleteByUserId(userId);

        } catch (Exception exception) {
            exception.printStackTrace();
            ResponseDto.databaseError();
        }

        return SignUpResponseDto.success();
    }

    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {
        String token=null;
        try {

            String userId = dto.getId();
            UserEntity userEntity = userRepository.findByUserId(userId);
            if( userEntity == null ) return SignInResponseDto.signInFail();
            
            String password = dto.getPassword();
            String encodedPassword = userEntity.getPassword();
            boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            if( !isMatched ) return SignInResponseDto.signInFail();
            
            token = jwtProvider.create(userId);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return SignInResponseDto.success(token);
    }
}
