package com.vendas.web;

import com.vendas.exception.PersistenciaException;
import com.vendas.exception.ValidacaoException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorErros {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, String>> validacao(ValidacaoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
    }

    @ExceptionHandler(PersistenciaException.class)
    public ResponseEntity<Map<String, String>> persistencia(PersistenciaException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Erro de acesso ao banco de dados: " + e.getMessage()));
    }
}
