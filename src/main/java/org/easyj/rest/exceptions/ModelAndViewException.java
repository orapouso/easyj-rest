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

import org.springframework.web.servlet.ModelAndView;

/**
 * Base exception to carry a {@code ModelAndView} from an error
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 */
public class ModelAndViewException extends RuntimeException{
	
    private ModelAndView mav;

    public ModelAndViewException() {
        super();
    }
    public ModelAndViewException(String message, Throwable cause) {
        super(message, cause);
    }
    public ModelAndViewException(String message) {
        super(message);
    }
    public ModelAndViewException(Throwable cause) {
        super(cause);
    }

    /*Constructors with specific binding result from any controller with binding errors*/
    public ModelAndViewException(ModelAndView mav) {
        super();
        setMav(mav);
    }
    public ModelAndViewException(String message, Throwable cause, ModelAndView mav) {
        super(message, cause);
        setMav(mav);
    }
    public ModelAndViewException(String message, ModelAndView mav) {
        super(message);
        setMav(mav);
    }
    public ModelAndViewException(Throwable cause, ModelAndView mav) {
        super(cause);
        setMav(mav);
    }

    public final void setMav(ModelAndView mav) {
        this.mav = mav;
    }

    public final ModelAndView getMav() {
        return mav;
    }

}
