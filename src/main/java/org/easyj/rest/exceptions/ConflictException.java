/*
 *  Copyright 2009-2012 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.easyj.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Exception to be binded to the response with
 * {@code HttpServletResponse} Conflict status code (409)
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public final class ConflictException extends ModelAndViewException {
	
    public ConflictException() {
        super();
    }
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConflictException(String message) {
        super(message);
    }
    public ConflictException(Throwable cause) {
        super(cause);
    }

    public ConflictException(ModelAndView mav) {
        super();
        setMav(mav);
    }
    public ConflictException(String message, Throwable cause, ModelAndView mav) {
        super(message, cause);
        setMav(mav);
    }
    public ConflictException(String message, ModelAndView mav) {
        super(message);
        setMav(mav);
    }
    public ConflictException(Throwable cause, ModelAndView mav) {
        super(cause);
        setMav(mav);
    }

}
