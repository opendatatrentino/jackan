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
import java.sql.Timestamp;
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
    private Timestamp metadataCreated;
    private Timestamp metadataModified;
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

    private List<CkanDatasetRelationShip> relationshipsAsObject;
    private List<CkanDatasetRelationShip> relationshipsAsSubject;

    private List<CkanResource> resources;

    private String revisionId;
    private Timestamp revisionTimestamp;
    private String state;
    private List<CkanTag> tags;
    private String title;
    private String type;
    private String url;
    @Nullable
    private String version;

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras. They will end up here.
     */
    private Map<String, Object> others;

    public CkanDataset() {
        this.others = new HashMap();
    }

    /**     
     * Constructor with the minimal set of attributes required to successfully
     * create a dataset on the server.
     *
     * @param name the dataset name (contains no spaces and has dashes as
     * separators, i.e. "limestone-pavement-orders")
     */
    public CkanDataset(String name) {
        this();
        this.name = name;
    }
     
    /**
     * CKAN instances might have
     * <a href="http://docs.ckan.org/en/latest/extensions/adding-custom-fields.html">
     * custom data schemas</a> that force presence of custom properties among
     * 'regular' ones given by {@link #getExtras()}. In this case, they go to
     * 'others' field.
     */
    @JsonAnyGetter
    public Map<String, Object> getOthers() {
        return others;
    }

    /**
     * See {@link #getOthers()}
     */
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
     * Regular place where to put custom metadata. See also
     * {@link #getOthers()}.
     */
    public List<CkanPair> getExtras() {
        return extras;
    }

    /**
     * See {@link #getExtras()}
     */
    public void setExtras(List<CkanPair> extras) {
        this.extras = extras;
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

    /**
     * <a href="http://docs.ckan.org/en/latest/api/legacy-api.html?highlight=isopen" target="_blank">
     * Legacy api 1/2 docs</a> says: boolean indication of whether dataset is
     * open according to Open Knowledge Definition, based on other fields
     */
    @JsonProperty("isopen")
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * @see #isOpen()
     */
    @JsonProperty("isopen")
    public void setOpen(boolean isOpen) {
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

    /**
     * Returns the dataset name (contains no spaces and has dashes as
     * separators, i.e. "limestone-pavement-orders")
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the dataset name. Name must not contain spaces and have dashes as
     * separators, i.e. "limestone-pavement-orders"
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
     * The organization that owns the dataset.
     *
     * Notice that if the dataset was obtained with a
     * {@link CkanClient#getDataset(java.lang.String)} call, the returned
     * organization won't have all the params you would get with a
     * {@link CkanClient#getOrganization(java.lang.String)} call.
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
     * Sets the owner organization alphanunmerical id, like
     * "b112ed55-01b7-4ca4-8385-f66d6168efcc",
     */
    public void setOwnerOrg(String ownerOrg) {
        this.ownerOrg = ownerOrg;
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

    public List<CkanDatasetRelationShip> getRelationshipsAsObject() {
        return relationshipsAsObject;
    }

    public void setRelationshipsAsObject(List<CkanDatasetRelationShip> relationshipsAsObject) {
        this.relationshipsAsObject = relationshipsAsObject;
    }

    public List<CkanDatasetRelationShip> getRelationshipsAsSubject() {
        return relationshipsAsSubject;
    }

    public void setRelationshipsAsSubject(List<CkanDatasetRelationShip> relationshipsAsSubject) {
        this.relationshipsAsSubject = relationshipsAsSubject;
    }

    public List<CkanResource> getResources() {
        return resources;
    }

    public void setResources(List<CkanResource> resources) {
        this.resources = resources;
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

    /**
     * The type of the dataset (optional), IDatasetForm plugins associate
     * themselves with different dataset types and provide custom dataset
     * handling behaviour for these types
     */
    public String getType() {
        return type;
    }

    /**
     * The type of the dataset (optional), IDatasetForm plugins associate
     * themselves with different dataset types and provide custom dataset
     * handling behaviour for these types
     */
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

}
