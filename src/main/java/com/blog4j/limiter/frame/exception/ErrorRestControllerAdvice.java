package com.blog4j.limiter.frame.exception;


import com.blog4j.limiter.frame.respose.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorRestControllerAdvice {
//    @ResponseStatus(HttpStatus.OK)
//    @ExceptionHandler(value = {
//            LimiterException.class
//    })
//    public void RuntimeException(HttpServletRequest request, HttpServletResponse response, Exception throwable) throws IOException {
//        log.info("{}", throwable.getMessage());
//        throwable.printStackTrace();
//        response.sendError(HttpServletResponse.SC_OK, throwable.getMessage());
//    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LimiterException.class)
    public CommonResponse<Object> handleException(LimiterException e) {
        return CommonResponse.fail(e.getMessage());
    }
}
