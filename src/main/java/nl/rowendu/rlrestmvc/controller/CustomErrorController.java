package nl.rowendu.rlrestmvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
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

}
