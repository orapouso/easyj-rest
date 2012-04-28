package org.easyj.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public final class BadRequestException extends RuntimeException{
	
    BindingResult result;

    public BadRequestException(){
        super();
    }
    public BadRequestException(final String message, final Throwable cause){
        super(message, cause);
    }
    public BadRequestException(final String message){
        super(message);
    }
    public BadRequestException(final Throwable cause){
        super(cause);
    }

    /*Constructors with specific binding result from any controller with binding errors*/
    public BadRequestException(final BindingResult result){
        this();
        setResult(result);
    }
    public BadRequestException(final String message, final Throwable cause, BindingResult result){
        this(message, cause);
        setResult(result);
    }
    public BadRequestException(final String message, BindingResult result){
        this(message);
        setResult(result);
    }
    public BadRequestException(final Throwable cause, BindingResult result){
        this(cause);
        setResult(result);
    }
    private void setResult(BindingResult result) {
        this.result = result;
    }
    public BindingResult getResult() {
        return result;
    }

}
