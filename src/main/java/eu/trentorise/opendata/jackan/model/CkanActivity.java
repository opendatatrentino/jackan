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
package eu.trentorise.opendata.jackan.model;

import java.sql.Timestamp;
import java.util.List;
import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class CkanActivity {

    /**
     * i.e. "ddb21e57-da76-4dc1-a815-4edd0e9e332e"
     */
    private String id;
    /**
     * Ckan always refer to UTC timezone, in JSON looks like i.e. "2013-03-08T09:31:20.833590"
     */
    private Timestamp timestamp;
    /**
     * i.e. "Impostazioni modificate."
     */
    private String message;
    /**
     * i.e. "admin"
     */
    private String author;
    @Nullable
    private Timestamp approvedTimestamp;
    private List<CkanDataset> packages;
    private List<String> groups;
    private CkanState state;

    public CkanActivity() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * internally date is stored with UTC timezone
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Nullable
    public Timestamp getApprovedTimestamp() {
        return approvedTimestamp;
    }

    /**
     * internally date is stored with UTC timezone
     *
     * @param approvedTimestamp
     */
    public void setApprovedTimestamp(@Nullable Timestamp approvedTimestamp) {        
        this.approvedTimestamp = approvedTimestamp;        
    }

    public List<CkanDataset> getPackages() {
        return packages;
    }

    public void setPackages(List<CkanDataset> packages) {
        this.packages = packages;
    }

    /** 
     * Returns list of group names (i.e. region-trentino)
     */
    public List<String> getGroups() {
        return groups;
    }

    /** 
     * Returns list of group names (i.e. region-trentino)
     */    
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public CkanState getState() {
        return state;
    }

    public void setState(CkanState state) {
        this.state = state;
    }
}
