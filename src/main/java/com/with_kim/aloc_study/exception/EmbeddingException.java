package com.with_kim.aloc_study.exception;

public class EmbeddingException extends RuntimeException{
    public EmbeddingException(String message){
        super(message);
    }

    public EmbeddingException(String message,Throwable cause){
        super(message,cause);
    }
}
