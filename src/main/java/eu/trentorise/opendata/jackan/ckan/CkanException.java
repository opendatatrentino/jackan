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

import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanResponse;
import javax.annotation.Nullable;

/**
 * Runtime exception for ckan-related problems.
 * @author David Leoni
 */
public class CkanException extends JackanException {

    @Nullable
    private CkanResponse ckanResponse = null;
    @Nullable
    private CkanClient ckanClient = null;

    private static String makeMessage(String msg, @Nullable CkanResponse ckanResponse, @Nullable CkanClient client) {
        return msg + "  "
                + (ckanResponse != null ? ckanResponse + "  " : "")
                + (client != null ? client : "");
    }
    
    
    public CkanException(String msg) {
        super(msg);
    }

    public CkanException(String msg, Throwable ex) {
        super(msg, ex);
    }

   public CkanException(String msg, CkanClient client) {
        super(makeMessage(msg, null, client));        
        this.ckanClient = client;
    }    
    
    public CkanException(String msg, CkanResponse ckanResponse, CkanClient client) {
        super(makeMessage(msg, ckanResponse, client));
        this.ckanResponse = ckanResponse;
        this.ckanClient = client;
    }

    public CkanException(String msg, CkanClient client, Throwable ex) {
        this(msg, null, client, ex);
    }
    
     public CkanException(String msg, CkanResponse ckanResponse, CkanClient client, Throwable ex) {
        super(makeMessage(msg, ckanResponse, client), 
                ex);
        this.ckanResponse = ckanResponse;
        this.ckanClient = client;         
    }

    @Nullable
    public CkanResponse getCkanResponse() {
        return ckanResponse;
    }
     
    @Nullable
    public CkanClient getCkanClient() {
        return ckanClient;
    }

}
