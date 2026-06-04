package com.nhnacademy.minidooray.common.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_CLIENT_ERROR_MESSAGE = "요청을 처리할 수 없습니다.";
    private static final String DEFAULT_NOT_FOUND_MESSAGE = "요청한 대상을 찾을 수 없습니다.";
    private static final String DEFAULT_SERVER_ERROR_MESSAGE = "서비스 처리 중 문제가 발생했습니다.";
    private static final String API_UNAVAILABLE_MESSAGE = "하위 API와 연결할 수 없습니다. 잠시 후 다시 시도해주세요.";

    @ExceptionHandler(RestClientResponseException.class)
    public String handleApiResponseException(RestClientResponseException ex,
                                             Model model,
                                             HttpServletResponse response) {
        HttpStatusCode statusCode = ex.getStatusCode();
        int status = statusCode.value();
        response.setStatus(status);

        if (statusCode.is4xxClientError()) {
            log.warn("API client error. status={}, body={}", status, ex.getResponseBodyAsString());
            return handleClientError(ex, model);
        }

        log.error("API server error. status={}, body={}", status, ex.getResponseBodyAsString(), ex);
        model.addAttribute("status", status);
        model.addAttribute("title", "서버 오류");
        model.addAttribute("message", DEFAULT_SERVER_ERROR_MESSAGE);
        return "error/error";
    }

    @ExceptionHandler(ResourceAccessException.class)
    public String handleApiConnectionException(ResourceAccessException ex,
                                               Model model,
                                               HttpServletResponse response) {
        log.error("API connection error", ex);
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        model.addAttribute("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        model.addAttribute("title", "서비스 연결 실패");
        model.addAttribute("message", API_UNAVAILABLE_MESSAGE);
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception ex,
                                            Model model,
                                            HttpServletResponse response) {
        log.error("Unhandled gateway exception", ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("title", "서버 오류");
        model.addAttribute("message", DEFAULT_SERVER_ERROR_MESSAGE);
        return "error/error";
    }

    private String handleClientError(RestClientResponseException ex, Model model) {
        int status = ex.getStatusCode().value();
        String message = getResponseMessage(ex);

        model.addAttribute("status", status);
        if (status == HttpStatus.NOT_FOUND.value()) {
            model.addAttribute("title", "페이지를 찾을 수 없습니다");
            model.addAttribute("message", message.isBlank() ? DEFAULT_NOT_FOUND_MESSAGE : message);
            return "error/404";
        }

        model.addAttribute("title", "요청 오류");
        model.addAttribute("message", message.isBlank() ? DEFAULT_CLIENT_ERROR_MESSAGE : message);
        return "error/error";
    }

    private String getResponseMessage(RestClientResponseException ex) {
        return ex.getResponseBodyAsString() == null ? "" : ex.getResponseBodyAsString().trim();
    }
}
