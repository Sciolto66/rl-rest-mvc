package nl.rowendu.rlrestmvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomErrorController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<List<Map<String,String>>> handleBindErrors(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getFieldErrors().stream()
                .map(fieldError -> {
                            Map<String, String> errorMap = new HashMap<>();
                            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                            return errorMap;
                        }).toList();

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler
    ResponseEntity<List<Map<String,String>>> handleJpaViolations(TransactionSystemException ex) {
        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.badRequest();

        if (ex.getCause().getCause() instanceof ConstraintViolationException violationException) {
            List<Map<String, String>> errors = violationException.getConstraintViolations().stream()
                    .map(violation -> {
                        Map<String, String> errorMap = new HashMap<>();
                        errorMap.put(violation.getPropertyPath().toString(), violation.getMessage());
                        return errorMap;
                    }).toList();
            return responseEntity.body(errors);
        }
        return responseEntity.build();
    }

}
