/**
 * *****************************************************************************
 * Copyright 2013-2014 Trento Rise (www.trentorise.eu/)
 * 
* All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License (LGPL)
 * version 2.1 which accompanies this distribution, and is available at
 * 
* http://www.gnu.org/licenses/lgpl-2.1.html
 * 
* This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
*******************************************************************************
 */
package eu.trentorise.opendata.jackan.ckan;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import eu.trentorise.opendata.traceprov.impl.dcat.DcatDataset;
import eu.trentorise.opendata.traceprov.impl.dcat.DcatDistribution;
import eu.trentorise.opendata.traceprov.impl.dcat.FoafAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author David Leoni
 */
public class CkanDataset {

    private String author;
    private String authorEmail;
    @Nullable
    private String creatorUserId;
    private String downloadUrl;
    private List<CkanPair> extras;
    private List<CkanGroup> groups;
    private String id;
    private boolean isOpen;
    private String licenseId;
    private String licenseTitle;
    private String licenseUrl;
    private String maintainer;
    private String maintainerEmail;
    private DateTime metadataCreated;
    private DateTime metadataModified;
    private String name;
    private String notes;
    private String notesRendered;
    private String ownerOrg;
    /**
     * Actually it is named 'private' in api. Appears in searches.
     */
    @Nullable
    private Boolean priv;

    private List<CkanResource> resources;

    private String revisionId;
    private DateTime revisionTimestamp;
    private String state; 
    private List<CkanTag> tags;
    private String title;
    private String type;
    private String url;
    @Nullable
    private String version;

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras as they should. They will end up here.
     */
    private Map<String, Object> others = new HashMap<String, Object>();

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras as they should. In this case, they end up in 'others'
     * field
     */
    @JsonAnyGetter
    public Map<String, Object> getOthers() {
        return others;
    }

    @JsonAnySetter
    public void setOthers(String name, Object value) {
        others.put(name, value);
    }

    @JsonIgnore
    public Map<String, String> getExtrasAsHashMap() {
        HashMap<String, String> hm = new HashMap();
        for (CkanPair cp : extras) {
            hm.put(cp.getKey(), cp.getValue());
        }
        return hm;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    @Nullable
    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(@Nullable String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public List<CkanGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CkanGroup> groups) {
        this.groups = groups;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
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

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getMaintainerEmail() {
        return maintainerEmail;
    }

    public void setMaintainerEmail(String maintainerEmail) {
        this.maintainerEmail = maintainerEmail;
    }

    /**
     * Returns date in UTC timezone
     */    
    public DateTime getMetadataCreated() {
        return metadataCreated;
    }

    /**
     * Internally date is stored with UTC timezone
     */
    public void setMetadataCreated(DateTime metadataCreated) {
        this.metadataCreated = metadataCreated.toDateTime(DateTimeZone.UTC);
    }

    /**
     * Returns date in UTC timezone
     */        
    public DateTime getMetadataModified() {
        return metadataModified;
    }

    /**
     * Internally date is stored with UTC timezone
     */
    public void setMetadataModified(DateTime metadataModified) {
        this.metadataModified = metadataModified.toDateTime(DateTimeZone.UTC);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotesRendered() {
        return notesRendered;
    }

    public void setNotesRendered(String notesRendered) {
        this.notesRendered = notesRendered;
    }

    /**
     * Returns the owner organization alphanunmerical id, like "b112ed55-01b7-4ca4-8385-f66d6168efcc",
     */    
    public String getOwnerOrg() {
        return ownerOrg;
    }

    /**
     * Sets the owner organization alphanunmerical id, like "b112ed55-01b7-4ca4-8385-f66d6168efcc",
     */
    public void setOwnerOrg(String ownerOrg) {
        this.ownerOrg = ownerOrg;
    }

    /** 
     * Returns the alphanumerical id, like "39d94b20-ea72-4c5e-bd8f-967a77e03946"
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
    public DateTime getRevisionTimestamp() {
        return revisionTimestamp;
    }

    /**
     * Internally date is stored with UTC timezone. Probably it is automatically
     * calculated by CKAN.
     *
     * @param revisionTimestamp
     */
    public void setRevisionTimestamp(DateTime revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp.toDateTime(DateTimeZone.UTC);
    }

    /**
     * todo don't know meaning, found "active" as one value
     */
    public String getState() {
        return state;
    }

    /**
     * todo don't know meaning, found "active" as one value
     */
    public void setState(String state) {
        this.state = state;
    }

    public List<CkanTag> getTags() {
        return tags;
    }

    public void setTags(List<CkanTag> tags) {
        this.tags = tags;
    }

    /**
     * Returns the title, like "Hospitals of Trento"
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title, like "Hospitals of Trento"
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Should be the landing page on original data provider website describing
     * the dataset.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Should be the landing page on original data provider website describing
     * the dataset.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    public void setVersion(@Nullable String version) {
        this.version = version;
    }

    public List<CkanPair> getExtras() {
        return extras;
    }

    public void setExtras(List<CkanPair> extras) {
        this.extras = extras;
    }

    public DcatDataset ToDcatDataset(String catalogURL, String locale){
        
        String nCatalogUrl = TraceProvUtils.removeTrailingSlash(catalogURL);
        
        CkanClient.logger.warning("TODO - CONVERSION FROM CKAN DATASET TO DCAT DATASET IS STILL EXPERIMENTAL, IT MIGHT BE INCOMPLETE!!!");
        
        DcatDataset dd = new DcatDataset();
        
        
        
        CkanClient.logger.warning("TODO - SKIPPED ACCRUAL PERIODICITY WHILE CONVERTING FROM CKAN TO DCAT");  
        // dd.setAccrualPeriodicity(null);
        CkanClient.logger.warning("TODO - SKIPPED CONTACT POINT WHILE CONVERTING FROM CKAN TO DCAT");
        // dd.setContactPoint(null);
        
        dd.setDescription(this.getNotes());
        
        
        List<DcatDistribution> distribs = new ArrayList();
        for (CkanResource cr : this.getResources()){
            distribs.add(cr.toDcatDistribution(nCatalogUrl, this.getId(), this.getLicenseId()));
        }
        
        dd.setDistributions(distribs);        
        dd.setIdentifier(this.getName());
        
        dd.setIssued(this.getMetadataCreated().toString());
        
        List<String> keywords = new ArrayList();
        for (CkanTag tag : this.getTags()){
            keywords.add(tag.getName());
        }
        dd.setKeywords(keywords);
        
        dd.setLandingPage(this.getUrl());
        dd.setLanguage(locale);
        dd.setModified(this.getMetadataModified().toString());
        FoafAgent publisher = new FoafAgent();
        publisher.setURI(locale);
        publisher.setName(this.getMaintainer());
        publisher.setMbox(this.getMaintainerEmail());
        dd.setPublisher(publisher);
        // dd.setSpatial(catalogURL);
        CkanClient.logger.warning("TODO - SKIPPED 'SPATIAL' WHILE CONVERTING FROM CKAN TO DCAT");        
        
        //dd.setTemporal(catalogURL);
        CkanClient.logger.warning("TODO - SKIPPED 'TEMPORAL' WHILE CONVERTING FROM CKAN TO DCAT");        
                
        CkanClient.logger.warning("TODO - SKIPPED 'THEME' WHILE CONVERTING FROM CKAN TO DCAT");        
        // dd.setTheme(null);
        
        dd.setTitle(this.getTitle());
        
        // let's set URI to ckan page
        dd.setURI(CkanClient.makeDatasetURL(nCatalogUrl, this.getId()));
        return dd;
    };

    

    /**
     * Actually it is named "private" in the CKAN API. Appears in dataset searches.
     */
    @JsonProperty("private")
    public Boolean getPriv() {
        return priv;
    }

    /**
     * Actually it is named "private" in the CKAN API. Appears in dataset searches.
     */    
    public void setPriv(Boolean priv) {
        this.priv = priv;
    }

    public List<CkanResource> getResources() {
        return resources;
    }

    public void setResources(List<CkanResource> resources) {
        this.resources = resources;
    }
}
