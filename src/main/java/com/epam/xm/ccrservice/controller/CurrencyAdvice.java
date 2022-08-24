package com.epam.xm.ccrservice.controller;

import com.epam.xm.ccrservice.dto.ErrorResponse;
import com.epam.xm.ccrservice.exception.ControllerNoContentException;
import com.epam.xm.ccrservice.exception.ControllerValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.Set;
import java.util.stream.Collectors;


@ControllerAdvice(assignableTypes = CurrencyController.class)
public class CurrencyAdvice extends ResponseEntityExceptionHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ControllerValidationException.class})
    @ResponseBody
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected ErrorResponse handleConstraintViolation(ControllerValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseBody
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected ErrorResponse handleConstraintViolation(ConstraintViolationException e) {
        return new ErrorResponse(toConstraintMessage(e));
    }

    @Override
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(value = {ControllerNoContentException.class})
    @ResponseBody
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected ErrorResponse handleNoContent(ControllerNoContentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseBody
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected ErrorResponse handleRuntime(RuntimeException e) { //override general spring response
        return new ErrorResponse(e.getMessage());
    }

    /* beatify to avoid exposure of some internals through the api */
    private static String toConstraintMessage(ConstraintViolationException exception) {

        Set<ConstraintViolation<?>> violation = exception.getConstraintViolations();

        if (violation != null) { //may be null, see source code deeper
            return "Violation(s) triggerred: " + exception.getConstraintViolations().stream()
                    .map(cv -> parameter(cv.getPropertyPath()) + " " + cv.getMessage())
                    .collect(Collectors.joining(",", "[", "]"));
        } else {
            return "Violation(s) triggered but set is null, message: " + exception.getMessage();
        }
    }

    private static String parameter(Path path) {
        String parameter = null;
        for (Path.Node node : path) {
            if (node.getKind() == ElementKind.PARAMETER) {
                parameter = node.getName();
            }
        }
        return parameter != null ? parameter : "Parameter not found";
    }

}
