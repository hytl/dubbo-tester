package com.hytl.dubbo.tester.bs.exception;

import com.hytl.dubbo.tester.bs.enums.ErrorCode;
import com.hytl.dubbo.tester.bs.model.RestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * WebGlobalExceptionHandler
 *
 * @author hytl
 */
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class WebGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<RestResult> handleDubboTesterException(DubboTesterExcpetion ex) {
        log.error("ImServerException error: {} userId: {} sessionId: {}", ex.getMessage());
        int code = ex.getCode();
        int returnHttpStatusCode = code > 1000 ? 200 : code;
        return new ResponseEntity<>(new RestResult(code, ex.getMessage(), null)
                , HttpStatusCode.valueOf(returnHttpStatusCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResult> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException error: {}", ex.getMessage());
        return new ResponseEntity<>(new RestResult<>(ErrorCode.BAD_REQUEST
                , ex.getBindingResult() != null ? ex.getBindingResult().getAllErrors().get(0).toString() : null)
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<RestResult> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        return new ResponseEntity<>(new RestResult<>(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage())
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}