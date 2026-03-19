package com.group05.TC_LLM_Generator.application.exception;

/**
 * Exception thrown when LLM service (RunPod) is unavailable or returns invalid response.
 */
public class LlmServiceException extends RuntimeException {

    public LlmServiceException(String message) {
        super(message);
    }

    public LlmServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
