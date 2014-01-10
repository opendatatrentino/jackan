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

import eu.trentorise.opendata.jackan.dcat.DcatDataset;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


/**
 *
 * @author David Leoni
 */
public class CkanDataset {
    private String author;
    private String authorEmail;  
    @Nullable private String creatorUserId;
    private String downloadUrl;
    private List<CkanPair> extras;
    private List<CkanGroup> groups;    
    private String id;
    private boolean isOpen;
    private String license;
    private String licenseTitle;
    private String licenseUrl;
    private String maintainer;
    private String maintainerEmail;
    /**
     * In Ckan it is stored in ISO-8601 defaulted to UTC timezone
     * i.e. "2013-11-11T04:12:11.110868"
     */
    private DateTime metadataCreated;
    /**
     * In Ckan it is stored in ISO-8601 defaulted to UTC timezone
     * i.e. "2013-11-11T04:12:11.110868"
     */

    private DateTime metadataModified;
    private String name;
    private String notes;
    private String notesRendered;
    private String ownerOrg;
    /**
     * Actually it is named 'private' in api. Appears in searches.
    */
    @Nullable private Boolean priv;    
    private String revisionId;
    /**
     * In Ckan it is stored in ISO-8601 defaulted to UTC timezone
     * i.e. "2013-11-11T04:12:11.110868"
     */
    private DateTime revisionTimestamp;
    private String state; // todo what should it be?
    private List<CkanTag> tags;
    private String title;
    private String type;
    private String url;
    @Nullable private String version;

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't end up in extras as they should. They will end up here.
     */
    private Map<String,Object> others = new HashMap<String,Object>();
    
    /**
     * Custom CKAN instances might sometimes gift us with properties that don't end up in extras as they should. In this case, they end up in 'others' field
    */ 
    @JsonAnyGetter
    public Map<String,Object> getOthers() {
        return others;
    }

    @JsonAnySetter
    public void setOthers(String name, Object value) {
        others.put(name, value);
    }

    @JsonIgnore
    public Map<String,String> getExtrasAsHashMap(){
        HashMap<String,String> hm = new HashMap();
        for (CkanPair cp : extras){
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

    @Nullable public String getCreatorUserId() {
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

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
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

    public DateTime getMetadataCreated() {
        return metadataCreated;
    }

    /**
       internally date is stored with UTC timezone
    */
    public void setMetadataCreated(DateTime metadataCreated) {
        this.metadataCreated = metadataCreated.toDateTime(DateTimeZone.UTC);
    }

    public DateTime getMetadataModified() {
        return metadataModified;
    }

    /**
       internally date is stored with UTC timezone
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

    public String getOwnerOrg() {
        return ownerOrg;
    }

    public void setOwnerOrg(String ownerOrg) {
        this.ownerOrg = ownerOrg;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public DateTime getRevisionTimestamp() {
        return revisionTimestamp;
    }

    /**
       internally date is stored with UTC timezone
     * @param revisionTimestamp
    */   
    public void setRevisionTimestamp(DateTime revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp.toDateTime(DateTimeZone.UTC);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<CkanTag> getTags() {
        return tags;
    }

    public void setTags(List<CkanTag> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Nullable public String getVersion() {
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
    
    public DcatDataset toDcatDataset(){
         DcatDataset ret = new DcatDataset();
         
         return ret;
    }    

    /**
     * Actually it is named "private" in the API. Appears in dataset searches.
     */
    @JsonProperty("private")
    public Boolean getPriv() {
        return priv;
    }

    public void setPriv(Boolean priv) {
        this.priv = priv;
    }
}
