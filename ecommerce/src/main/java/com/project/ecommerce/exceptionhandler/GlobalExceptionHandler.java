package com.project.ecommerce.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myConstraintValidationResponse(MethodArgumentNotValidException e)
    {
        Map<String, String> response = new HashMap<>();

        e.getBindingResult().getAllErrors()
                .forEach(err -> {
                    String fieldName = ((FieldError)err).getField();
                    String responseMsg = err.getDefaultMessage();

                    response.put(fieldName, responseMsg);
                });

        return new ResponseEntity<Map<String, String>>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundExceptionResponse(ResourceNotFoundException e)
    {
        return new ResponseEntity<>(
                new APIResponse<>(e.getMessage(), false, null),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myApiExceptionResponse(APIException e)
    {
        return new ResponseEntity<>(
                new APIResponse<>(e.getMessage(), false, null),
                HttpStatus.BAD_REQUEST
        );
    }
}
