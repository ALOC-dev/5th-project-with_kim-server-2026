package com.with_kim.aloc_study.exception;

import org.springframework.http.HttpStatus;

public class KakaoApiException extends RuntimeException {
        private final HttpStatus status;

        public KakaoApiException(HttpStatus status, String message) {
            super(message);
            this.status = status;
        }

        public HttpStatus getStatus() {
            return status;
        }
}
