package com.pipa.back.provider;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender JavaMailSender;

    private String SUBJECT = "[테스트-서비스] 인증메일입니다.";

    public boolean sendCertificationMail( String userEmail, String certificationNumber  ){
        try {

            MimeMessage mimeMessage = JavaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            String htmlContext = this.getCertificationMessage(certificationNumber);
            mimeMessageHelper.setTo( userEmail );
            mimeMessageHelper.setSubject(SUBJECT);
            mimeMessageHelper.setText(htmlContext, true);

            JavaMailSender.send( mimeMessage );
                
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        return true;

    }


    private String getCertificationMessage(String certificationNumber ){
        String certificationMessage ="";
        certificationMessage += "<h1 style='text-align: center;'>[테스트-서비스] 인증 메일</h1>";
        certificationMessage += "<h3 style='text-align: center;'>인증 코드 : <strong style='font-size: 32px; letter-spacing: 8px;'>" ;
        certificationMessage += certificationNumber;
        certificationMessage += "</strong></h3>";
        return certificationMessage;  
    }

}
