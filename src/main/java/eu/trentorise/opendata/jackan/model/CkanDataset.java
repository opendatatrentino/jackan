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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;
import javax.annotation.Nullable;

/**
 * Extends {@link CkanDatasetBase} with fields found in search operations.
 */
public class CkanDataset extends CkanDatasetBase {

    private String creatorUserId;
    private String licenseUrl;
    private String licenseTitle;
    private Timestamp metadataCreated;
    private Timestamp metadataModified;
    private int numResources;
    private CkanTrackingSummary trackingSummary;
    private int numTags;
    private String notesRendered;
    private Boolean open;

    private CkanOrganization organization;

    /**
     * Actually it is named 'private' in api. Appears in searches.
     */
    private Boolean priv;
    private String revisionId;
    private Timestamp revisionTimestamp;
    
    public CkanDataset() {
    }

    /**
     * @see CkanDatasetBase#CkanDatasetBase(String)     
     */
    public CkanDataset(String name) {        
        super(name);
    }

    
    
    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(@Nullable String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    /**
     * <a href="http://docs.ckan.org/en/latest/api/legacy-api.html?highlight=isopen" target="_blank">
     * Legacy api 1/2 docs</a> says: boolean indication of whether dataset is
     * open according to Open Knowledge Definition, based on other fields
     */
    @JsonProperty("isopen")
    public Boolean isOpen() {
        return open;
    }

    /**
     * @see #isOpen()
     */
    @JsonProperty("isopen")
    public void setOpen(Boolean isOpen) {
        this.open = isOpen;
    }

    public String getLicenseTitle() {
        return licenseTitle;
    }

    public void setLicenseTitle(String licenseTitle) {
        this.licenseTitle = licenseTitle;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    /**
     * CKAN always refer to UTC timezone
     */
    public Timestamp getMetadataCreated() {
        return metadataCreated;
    }

    /**
     * CKAN always refer to UTC timezone
     */
    public void setMetadataCreated(Timestamp metadataCreated) {
        this.metadataCreated = metadataCreated;
    }

    /**
     * CKAN always refers to UTC timezone
     */
    public Timestamp getMetadataModified() {
        return metadataModified;
    }

    /**
     * CKAN always refers to UTC timezone
     */
    public void setMetadataModified(Timestamp metadataModified) {
        this.metadataModified = metadataModified;
    }

    public String getNotesRendered() {
        return notesRendered;
    }

    public void setNotesRendered(String notesRendered) {
        this.notesRendered = notesRendered;
    }

    public int getNumTags() {
        return numTags;
    }

    public void setNumTags(int numTags) {
        this.numTags = numTags;
    }
    
    

    /**
     * The organization that owns the dataset.
     *
     * Notice that if the dataset was obtained with a
     * {@link eu.trentorise.opendata.jackan.CkanClient#getDataset(java.lang.String)} call, the returned
     * organization won't have all the params you would get with a
     * {@link eu.trentorise.opendata.jackan.CkanClient#getOrganization(java.lang.String)} call.
     */
    public CkanOrganization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization that owns the dataset.
     */
    public void setOrganization(CkanOrganization organization) {
        this.organization = organization;
    }

    /**
     * Actually it is named "private" in the CKAN API. Appears in dataset
     * searches.
     */
    @JsonProperty("private")
    public Boolean isPriv() {
        return priv;
    }

    /**
     * Actually it is named "private" in the CKAN API. Appears in dataset
     * searches.
     */
    public void setPriv(Boolean priv) {
        this.priv = priv;
    }

    public int getNumResources() {
        return numResources;
    }

    public void setNumResources(int numResources) {
        this.numResources = numResources;
    }

    
    
    
    /**
     * Returns the alphanumerical id, like
     * "39d94b20-ea72-4c5e-bd8f-967a77e03946"
     */
    public String getRevisionId() {
        return revisionId;
    }

    /**
     * Sets the alphanumerical id, like "39d94b20-ea72-4c5e-bd8f-967a77e03946"
     */
    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    /**
     * Returns date in UTC timezone. Probably it is automatically calculated by
     * CKAN.
     */
    public Timestamp getRevisionTimestamp() {
        return revisionTimestamp;
    }

    /**
     * CKAN always refer to UTC timezone. Probably it is automatically
     * calculated by CKAN.
     *
     * @param revisionTimestamp
     */
    public void setRevisionTimestamp(Timestamp revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    public CkanTrackingSummary getTrackingSummary() {
        return trackingSummary;
    }

    public void setTrackingSummary(CkanTrackingSummary trackingSummary) {
        this.trackingSummary = trackingSummary;
    }

    
    
}
