/* 
 * Copyright 2015 Trento Rise  (trentorise.eu) 
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
package eu.trentorise.opendata.jackan;

import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanError;
import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class JackanException extends RuntimeException {

    @Nullable
    private CkanError ckanError = null;
    @Nullable
    private CkanClient ckanClient = null;
    
    private static String makeMessage(String msg, @Nullable CkanError error, @Nullable CkanClient client){
        return msg + "  "  
                + (client != null ? client + "  " : "")
                + (error != null ? error  : "");
    }
    
    public JackanException(String msg) {
        super(msg);
    }

    public JackanException(String msg, Throwable ex) {
        super(msg, ex);
    }

   public JackanException(String msg, CkanClient client) {
        super(makeMessage(msg, null, client));        
        this.ckanClient = client;
    }    
    
    public JackanException(String msg, CkanError error, CkanClient client) {
        super(makeMessage(msg, error, client));
        this.ckanError = error;
        this.ckanClient = client;
    }

    public JackanException(String msg, CkanClient client, Throwable ex) {
        this(msg, null, client, ex);
    }
    
     public JackanException(String msg, CkanError error, CkanClient client, Throwable ex) {
        super(makeMessage(msg, error, client), 
                ex);
        this.ckanError = error;
        this.ckanClient = client;         
    }

    @Nullable
    public CkanError getCkanError() {
        return ckanError;
    }
     
    @Nullable
    public CkanClient getCkanClient() {
        return ckanClient;
    }

    
}
