/*
 * Copyright 2015 Trento Rise.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.jackan.ckan;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author David Leoni
 */
public class CkanError {

    private String message;
    /**
     * actually the original is __type
     */
    private String type;
    
    /**
     * Holds fields we can't foresee
     */
    private Map<String, Object> others = new HashMap();    

    @Override
    public String toString() {
        return "Ckan error of type: " + getType() + "  message:" + getMessage() + 
                "  Other fields:" + others.toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * todo what are possible types?
     */
    @JsonProperty("__type")
    public String getType() {
        return type;
    }

    /**
     * todo what are possible types?
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Holds fields we can't foresee
     */
    @JsonAnyGetter
    public Map<String, Object> getOthers() {
        return others;
    }
    
    /**
     * Holds fields we can't foresee
     */
    @JsonAnySetter
    public void setOthers(String name, Object value) {
        others.put(name, value);
    }        
        
}