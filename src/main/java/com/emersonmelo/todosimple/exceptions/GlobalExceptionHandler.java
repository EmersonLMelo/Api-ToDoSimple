package com.emersonmelo.todosimple.exceptions;


import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.emersonmelo.todosimple.services.exceptions.DataBindingViolationException;
import com.emersonmelo.todosimple.services.exceptions.ObjectNotFoundException;

import lombok.extern.slf4j.Slf4j;

//RestControllerAdvice = permite que você capture e manipule as exceção de forma centralizada, fornecendo uma resposta consistente ao cliente
//Slf4j = podem ser usados para escrever mensagens de log com diferentes níveis de severidade, como info, error, warn e debug
@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    //Value = para validar se a aplicação esta em produção ou não
    @Value("${server.error.include-exception}")
    private boolean printStackTrace;

    //HttpStatus.UNPROCESSABLE_ENTITY = o código de status HTTP 422 é usado para indicar que a requisição foi recebida corretamente, mas não pôde ser processada devido a uma semântica de erro, como validação de entrada falhada.
    //Essa função é para tratar exceções onde o usuario mandou algo invalido para efetuar um dos CRUD
    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation error. Check 'errors' field for details.");
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    //Essa função é para tratar exceções em geral
    //ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) = código de status HTTP 500 é usado para indicar que ocorreu um erro interno no servidor ao processar a requisição do cliente
    //ExceptionHandler(Exception.class) = indica que o método handleException será chamado para lidar com qualquer exceção do tipo Exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception exception,
            WebRequest request) {
        final String errorMessage = "Unknown error occurred";
        log.error(errorMessage, exception);
        return buildErrorResponse(
                exception,
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    //ExceptionHandler(DataIntegrityViolationException.class) = será chamado para lidar com qualquer exceção do tipo DataIntegrityViolationException
    //HttpStatus.CONFLICT = o código de status HTTP 409 é usado para indicar que houve um conflito durante o processamento da requisição.
    //Essa função é para tratar exceções em que o usuario mandou o usuario que ja existe
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException,
            WebRequest request) {
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Failed to save entity with integrity problems: " + errorMessage, dataIntegrityViolationException);
        return buildErrorResponse(
                dataIntegrityViolationException,
                errorMessage,
                HttpStatus.CONFLICT,
                request);
    }

    //Essa função é para tratar exceções onde o usuario deixou de mandar algo obrigatorio para efetuar um dos CRUD
    //ExceptionHandler(ConstraintViolationException.class) = será chamado para lidar com qualquer exceção do tipo ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request) {
        log.error("Failed to validate element", constraintViolationException);
        return buildErrorResponse(
                constraintViolationException,
                null, HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
            ObjectNotFoundException objectNotFoundException,
            WebRequest request) {
        log.error("Failed to find the requested element", objectNotFoundException);
        return buildErrorResponse(
                objectNotFoundException,
                null, HttpStatus.NOT_FOUND,
                request);
    }

     @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(
            DataBindingViolationException dataBindingViolationException,
            WebRequest request) {
        log.error("Failed to save entity with associated data", dataBindingViolationException);
        return buildErrorResponse(
                dataBindingViolationException,
                null, HttpStatus.CONFLICT,
                request);
    }


    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (this.printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
