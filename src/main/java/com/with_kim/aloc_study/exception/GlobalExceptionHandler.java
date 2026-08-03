package com.with_kim.aloc_study.exception;

import com.with_kim.aloc_study.dto.ResultDto;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // @Valid 요청 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultDto> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.error("Validation failed : {}", message);

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message(message)
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.BAD_REQUEST);
    }

    // 서버 상태 오류
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResultDto> handleIllegalStateException(IllegalStateException e) {
        logger.error("IllegalStateException : {}", e.getMessage(), e);

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("Unexpected Error : " + e.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //카카오맵 API 오류
    @ExceptionHandler(KakaoApiException.class)
    public ResponseEntity<ResultDto> handleKakaoApiException(KakaoApiException e) {

        logger.error("Kakao API Error : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message(e.getMessage())
                .code(e.getStatus().value())
                .build();

        return new ResponseEntity<>(resultDto, e.getStatus());
    }

    // NotFound 예외
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResultDto> handleResourceNotFound(ResourceNotFoundException e) {
        logger.error("Resource Not Found : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("Resource not Found : " + e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.NOT_FOUND);
    }

    // 유효하지 않은 토큰 예외
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResultDto> handleInvalidToken(InvalidTokenException e) {
        logger.error("Invalid Token : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("Invalid Token : " + e.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.UNAUTHORIZED);
    }

    // AuthenticationFailed
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ResultDto> handleAuthenticationFailed(AuthenticationFailedException e) {
        logger.error("AuthenticationFailed : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("AuthenticationFailed : " + e.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.UNAUTHORIZED);
    }

    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultDto> handleIllegalArgument(IllegalArgumentException e) {
        logger.error("IllegalArgumentException : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("IllegalArgumentException : " + e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ResultDto> handleInvalidRequest(InvalidRequestException e) {
        logger.error("InvalidRequestException : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("InvalidRequestException : " + e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResultDto> handleAccessDenied(AccessDeniedException e) {
        logger.error("AccessDeniedException : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("접근 권한이 없습니다.")
                .code(HttpStatus.FORBIDDEN.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.FORBIDDEN);
    }

    // 임베딩 API 예외
    @ExceptionHandler(EmbeddingException.class)
    public ResponseEntity<ResultDto> handleEmbedding(EmbeddingException e){
        logger.error("Embedding Error : {}",e.getMessage());

        ResultDto resultDto=ResultDto.builder()
                .success(false)
                .message("Embedding Error : "+e.getMessage())
                .code(HttpStatus.BAD_GATEWAY.value())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.BAD_GATEWAY);
    }
}
