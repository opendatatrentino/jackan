/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.trentorise.opendata.jackan.ckan;

import java.util.ArrayList;
import javax.annotation.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
     * i.e. "2013-03-08T09:31:20.833590"
     */
    private DateTime timestamp;
    /**
     * i.e. "Impostazioni modificate."
     */
    private String message;
    /**
     * i.e. "admin"
     */
    private String author;
    private @Nullable DateTime approvedTimestamp;
    private ArrayList<CkanDataset> packages;
    private ArrayList<CkanGroup> groups;
    private CkanState state;       

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
       internally date is stored with UTC timezone
    */    
    public void setTimestamp(DateTime timestamp) {
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

    public @Nullable DateTime getApprovedTimestamp() {
        return approvedTimestamp;
    }

    /**
       internally date is stored with UTC timezone
     * @param approvedTimestamp
    */    
    public void setApprovedTimestamp(@Nullable DateTime approvedTimestamp) {
        if (approvedTimestamp != null) {
            this.approvedTimestamp = approvedTimestamp.toDateTime(DateTimeZone.UTC);
        } else {
            this.approvedTimestamp = null;
        }
    }

    public ArrayList<CkanDataset> getPackages() {
        return packages;
    }

    public void setPackages(ArrayList<CkanDataset> packages) {
        this.packages = packages;
    }

    public ArrayList<CkanGroup> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<CkanGroup> groups) {
        this.groups = groups;
    }

    public CkanState getState() {
        return state;
    }

    public void setState(CkanState state) {
        this.state = state;
    }
}