package com.emersonmelo.todosimple.services.exceptions;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//ResponseStatus(value = HttpStatus.NOT_FOUND) = status HTTP 404 é usado para indicar que o recurso solicitado não foi encontrado no servidor. Isso é comumente usado para lidar com situações em que um endpoint de API não corresponde a nenhum recurso existente.
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends EntityNotFoundException {
    
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
