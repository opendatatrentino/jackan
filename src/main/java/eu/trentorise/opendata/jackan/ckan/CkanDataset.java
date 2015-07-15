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
package eu.trentorise.opendata.jackan.ckan;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static eu.trentorise.opendata.commons.OdtUtils.checkNotEmpty;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Class initializes almost nothing so to fully preserve all we get from ckan.
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
    private Date metadataCreated;
    private Date metadataModified;
    private String name;
    private String notes;
    private String notesRendered;
    private String ownerOrg;
    private CkanOrganization organization;

    /**
     * Actually it is named 'private' in api. Appears in searches.
     */
    @Nullable
    private Boolean priv;

    private List<CkanResource> resources;

    private String revisionId;
    private Date revisionTimestamp;
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
    private Map<String, Object> others;

    public CkanDataset() {
        this.others = new HashMap();
    }

    /**
     * @param id The alphanumerical id of the dataaset,
     * i.e."c4577b8f-5603-4098-917e-da03e8ddf461"
     */
    public CkanDataset(String id) {
        this();
        this.id = id;
    }

    /**
     *
     * Constructor with the minimal set of attributes required to successfully
     * create a dataset on the server.
     *
     * @param name the dataset name with no spaces and dashes as separators,
     * i.e. "comune-di-trento-raccolta-differenziata-2013"
     * @param url A page URL containg a description of the semantified dataset
     * columns and the trasformations done on the original dataset. This URL
     * will be also displayed as metadata in the catalog under dcat:landingPage
     * @param extras
     */
    public CkanDataset(String name, String url, List<CkanPair> extras) {
        this();
        checkNotEmpty(name, "invalid ckan dataset name");
        checkNotNull(url, "invalid ckan dataset url to description page");
        checkNotNull(extras, "invalid ckan dataset extras");
        this.name = name;
        this.url = url;
        this.extras = extras;
    }

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras as they should. In this case, they go to in 'others'
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

    /**
     * Always returns a map (which might be empty)
     */
    @JsonIgnore
    public Map<String, String> getExtrasAsHashMap() {
        HashMap<String, String> hm = new HashMap();
        if (extras != null) {
            for (CkanPair cp : extras) {
                hm.put(cp.getKey(), cp.getValue());
            }
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

    /**
     * Notice that if the dataset was obtained with a
     * {@link CkanClient#getDataset(java.lang.String)} call, the returned group
     * won't have all the params you would get with a
     * {@link CkanClient#getGroup(java.lang.String)} call.
     */
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

    /**
     * Returns the alphanumerical id, i.e.
     * "c4577b8f-5603-4098-917e-da03e8ddf461"
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the alphanumerical id, i.e. "c4577b8f-5603-4098-917e-da03e8ddf461"
     */
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
     * CKAN always refer to UTC timezone
     */
    public Date getMetadataCreated() {
        return metadataCreated;
    }

    /**
     * CKAN always refer to UTC timezone
     */
    public void setMetadataCreated(Date metadataCreated) {
        this.metadataCreated = metadataCreated;
    }

    /**
     * CKAN always refers to UTC timezone
     */
    public Date getMetadataModified() {
        return metadataModified;
    }

    /**
     * CKAN always refers to UTC timezone
     */
    public void setMetadataModified(Date metadataModified) {
        this.metadataModified = metadataModified;
    }

    /**
     * returns the dataset name (contains no spaces and has dashes as
     * separators, i.e. "comune-di-trento-raccolta-differenziata-2013")
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the dataset name. Name must not contain spaces and have dashes as
     * separators, i.e. "comune-di-trento-raccolta-differenziata-2013"
     *
     * @param name
     */
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
     * Returns the owner organization alphanunmerical id, like
     * "b112ed55-01b7-4ca4-8385-f66d6168efcc".
     */
    public String getOwnerOrg() {
        return ownerOrg;
    }

    /**
     * Notice that if the dataset was obtained with a
     * {@link CkanClient#getDataset(java.lang.String)} call, the returned
     * organization won't have all the params you would get with a
     * {@link CkanClient#getOrganization(java.lang.String)} call.
     */
    public CkanOrganization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization the owns the dataset.
     */
    public void setOrganization(CkanOrganization organziation) {
        this.organization = organziation;
    }

    /**
     * Sets the owner organization alphanunmerical id, like
     * "b112ed55-01b7-4ca4-8385-f66d6168efcc",
     */
    public void setOwnerOrg(String ownerOrg) {
        this.ownerOrg = ownerOrg;
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
    public Date getRevisionTimestamp() {
        return revisionTimestamp;
    }

    /**
     * CKAN always refer to UTC timezone. Probably it is automatically
     * calculated by CKAN.
     *
     * @param revisionTimestamp
     */
    public void setRevisionTimestamp(Date revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
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

    /**
     * Actually it is named "private" in the CKAN API. Appears in dataset
     * searches.
     */
    @JsonProperty("private")
    @Nullable
    public Boolean isPriv() {
        return priv;
    }

    /**
     * Actually it is named "private" in the CKAN API. Appears in dataset
     * searches.
     */
    public void setPriv(@Nullable Boolean priv) {
        this.priv = priv;
    }

    public List<CkanResource> getResources() {
        return resources;
    }

    public void setResources(List<CkanResource> resources) {
        this.resources = resources;
    }
}
