/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.trentorise.opendata.jackan.ckan;

import java.util.ArrayList;
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
     * i.e. "2013-03-08T09:31:20.833590"
     */
    private String timestamp;
    /**
     * i.e. "Impostazioni modificate."
     */
    private String message;
    /**
     * i.e. "admin"
     */
    private String author;
    private @Nullable String approved_timestamp;
    private ArrayList<CkanDataset> packages;
    private ArrayList<CkanGroup> groups;
    private CkanState state;       

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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

    public @Nullable String getApproved_timestamp() {
        return approved_timestamp;
    }

    public void setApproved_timestamp(@Nullable String approved_timestamp) {
        this.approved_timestamp = approved_timestamp;
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