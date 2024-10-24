package com.pipa.back.common;

public interface ResponseMessage {
    String SUCCESS = "Success.";

    String VALIDATION_FAIL = "Validation failed";
    String DUPLICATE_ID = "Duplication id.";

    String SIGN_IN_FAIL = "Login information mismatched.";
    String CERTIFICATION_FAIL = "Certification failed.";
    String MAIL_FAIL = "Mail send failed.";
    
    String DATABASE_ERROR = "Database error.";
}
