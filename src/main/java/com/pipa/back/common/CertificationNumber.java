package com.pipa.back.common;

public class CertificationNumber {
    public static String getCertificationNumber(){
        String certificationNumer="";
        for(int i=0;i<4; i++){
            certificationNumer+= (int)(Math.random()*10);
        }
        return certificationNumer;
    }
    
    
}
