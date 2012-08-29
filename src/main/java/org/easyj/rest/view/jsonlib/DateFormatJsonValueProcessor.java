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

package org.easyj.rest.view.jsonlib;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * Custom Date processor
 * 
 * @author Rafael Raposo
 * @since 1.0.0
 */
public class DateFormatJsonValueProcessor implements JsonValueProcessor {
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    String dateFormat = "";
    
    public DateFormatJsonValueProcessor(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Object processArrayValue( Object value, JsonConfig jsonConfig ) {
        return process( value, jsonConfig );
    }

    @Override
    public Object processObjectValue( String key, Object value, JsonConfig jsonConfig ) {
        return process( value, jsonConfig );
    }

    private Object process( Object value, JsonConfig jsonConfig ) {
        return new SimpleDateFormat(dateFormat).format((Date) value);
    }
}
