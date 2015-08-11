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
package eu.trentorise.opendata.jackan.model;

/**
 *
 * @author David Leoni
 */
public class CkanResponse {

    private String help;
    private boolean success;
    private CkanError error;

    public CkanResponse() {        
    }   
    
    public CkanResponse(String help, boolean success, CkanError error) {
        this.help = help;
        this.success = success;
        this.error = error;
    }   
    
    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CkanError getError() {
        return error;
    }

    public void setError(CkanError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "CkanResponse{error=" + error+ ", success=" + success + ", help=" + help +  '}';
    }
    
    
        
}