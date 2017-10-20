package eionet.gdem.api.conversions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 */
@ControllerAdvice(basePackages = {"eionet.gdem.api.conversions"})
public class ConversionExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handleErrors(Exception exception, WebRequest request) {
        String body = "Error: " + exception.getMessage();
        return handleExceptionInternal(exception, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
