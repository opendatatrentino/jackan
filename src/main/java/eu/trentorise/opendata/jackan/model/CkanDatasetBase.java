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
package eu.trentorise.opendata.jackan.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Nullable;
import java.util.*;

import static eu.trentorise.opendata.commons.TodUtils.isNotEmpty;

/**
 * A Ckan Dataset, which in turn holds Ckan Resources.
 *
 * In Ckan terminology it is also known as 'package'.
 *
 * {@link CkanDatasetBase} holds fields that can be sent when
 * <a href="http://docs.ckan.org/en/latest/api/index.html?#ckan.logic.action.create.package_create" target="_blank">creating
 * a dataset,</a>, while {@link CkanDataset} holds more fields that can be
 * returned with searches.
 *
 * This class initializes nothing to fully preserve all we get from ckan. In
 * practice, all fields of retrieved resources can be null except maybe
 * {@code name}.
 *
 * @author David Leoni
 * @since 0.4.1
 */
public class CkanDatasetBase {

    private String author;
    private String authorEmail;
    private List<CkanPair> extras;
    private List<CkanGroup> groups;
    private String id;
    private String licenseId;
    private String maintainer;
    private String maintainerEmail;
    private String name;
    private String notes;
    private String ownerOrg;
    private List<CkanDatasetRelationship> relationshipsAsObject;
    private List<CkanDatasetRelationship> relationshipsAsSubject;
    private List<CkanResource> resources;
    private CkanState state;
    private List<CkanTag> tags;
    private String title;
    private String type;
    private String url;
    private String version;

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras. They will end up here.
     */
    @Nullable
    private Map<String, Object> others;

    public CkanDatasetBase() {
    }

    /**
     * Constructor with the minimal set of attributes required to successfully
     * create a dataset on the server.
     *
     * @param name the dataset name (contains no spaces and has dashes as
     * separators, i.e. "limestone-pavement-orders")
     */
    public CkanDatasetBase(String name) {
        this();
        this.name = name;
    }

    /**
     * CKAN instances might have
     * <a href="http://docs.ckan.org/en/latest/extensions/adding-custom-fields.html">
     * custom data schemas</a> that force presence of custom properties among
     * 'regular' ones. In this case, they go to 'others' field. Note that to
     * further complicate things there is also an {@link #getExtras() extras}
     * field.
     *
     * @see #putOthers(java.lang.String, java.lang.Object)
     */
    @JsonAnyGetter
    @Nullable
    public Map<String, Object> getOthers() {
        return others;
    }

    /**
     * @see #getOthers()
     * @see #putOthers(java.lang.String, java.lang.Object)
     */
    public void setOthers(@Nullable Map<String, Object> others) {
        this.others = others;
    }

    /**
     * See {@link #getOthers()}
     *
     * @see #setOthers(java.util.Map)
     */
    @JsonAnySetter
    public void putOthers(String name, Object value) {
        if (others == null) {
            others = new HashMap<>();
        }
        others.put(name, value);
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

    /**
     * Notice that if the dataset was obtained with a
     * {@link eu.trentorise.opendata.jackan.CkanClient#getDataset(java.lang.String)} call, the returned group
     * won't have all the params you would get with a
     * {@link eu.trentorise.opendata.jackan.CkanClient#getGroup(java.lang.String)} call.
     */
    public List<CkanGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CkanGroup> groups) {
        this.groups = groups;
    }

    /**
     * Adds CkanGroups
     * @param ckanGroups The CkanGroups elements
     */
    public void addGroups(CkanGroup... ckanGroups) {
        if (this.groups == null) {
            this.groups = new ArrayList<>(ckanGroups.length);
        }
        Collections.addAll(this.groups, ckanGroups);
    }

    /**
     * Regular place where to put custom metadata. See also
     * {@link #getOthers()}. Note also extras can be in CkanDataset but not in
     * CkanResource.
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
     * Always returns a non-null map (which might be empty)
     */
    @JsonIgnore
    public Map<String, String> getExtrasAsHashMap() {
        HashMap<String, String> hm = new HashMap<>();
        if (extras != null) {
            for (CkanPair cp : extras) {
                hm.put(cp.getKey(), cp.getValue());
            }
        }
        return hm;
    }

    /**
     * Adds CkanExtras
     * @param extras The CkanExtra elements
     */
    public void addExtras(CkanPair... extras) {
        if (this.extras == null) {
            this.extras = new ArrayList<>(extras.length);
        }
        Collections.addAll(this.extras, extras);
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
     * The license id (i.e. 'cc-zero')
     */
    public String getLicenseId() {
        return licenseId;
    }

    /**
     * The license id (i.e. 'cc-zero')
     */
    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
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
     * The dataset name (contains no spaces and has dashes as separators, i.e.
     * "limestone-pavement-orders")
     */
    public String getName() {
        return name;
    }

    /**
     * The dataset name. Name must not contain spaces and have dashes as
     * separators, i.e. "limestone-pavement-orders"
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * A description of the dataset. See also
     * {@link CkanDataset#getNotesRendered()} Note CkanResource has instead a
     * field called {@link CkanResourceBase#getDescription() description}.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * A description of the dataset. See also
     * {@link CkanDataset#getNotesRendered()} Note CkanResource has instead a
     * field called {@link CkanResourceBase#getDescription() description}.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * The owner organization alphanunmerical id, like
     * "b112ed55-01b7-4ca4-8385-f66d6168efcc".
     */
    public String getOwnerOrg() {
        return ownerOrg;
    }

    /**
     * The owner organization alphanunmerical id, like
     * "b112ed55-01b7-4ca4-8385-f66d6168efcc".
     */
    public void setOwnerOrg(String ownerOrg) {
        this.ownerOrg = ownerOrg;
    }

    public List<CkanDatasetRelationship> getRelationshipsAsObject() {
        return relationshipsAsObject;
    }

    public void setRelationshipsAsObject(List<CkanDatasetRelationship> relationshipsAsObject) {
        this.relationshipsAsObject = relationshipsAsObject;
    }

    public List<CkanDatasetRelationship> getRelationshipsAsSubject() {
        return relationshipsAsSubject;
    }

    public void setRelationshipsAsSubject(List<CkanDatasetRelationship> relationshipsAsSubject) {
        this.relationshipsAsSubject = relationshipsAsSubject;
    }

    public List<CkanResource> getResources() {
        return this.resources;
    }

    public void setResources(List<CkanResource> resources) {
        this.resources = resources;
    }

    /**
     * Adds CkanResources
     * @param resources The CkanResources elements
     */
    public void addCkanResources(CkanResource... resources) {
        if (this.resources == null) {
            this.resources = new ArrayList<>(resources.length);
        }
        Collections.addAll(this.resources, resources);
    }

    /**
     * The current state of the dataset, e.g. 'active' or 'deleted', only active
     * datasets show up in search results and other lists of datasets, this
     * parameter will be ignored if you are not authorized to change the state
     * of the dataset (optional, default: 'active')
     */
    public CkanState getState() {
        return state;
    }

    /**
     * The current state of the dataset, e.g. 'active' or 'deleted', only active
     * datasets show up in search results and other lists of datasets, this
     * parameter will be ignored if you are not authorized to change the state
     * of the dataset (optional, default: 'active')
     */
    public void setState(CkanState state) {
        this.state = state;
    }

    public List<CkanTag> getTags() {
        return tags;
    }

    public void setTags(List<CkanTag> tags) {
        this.tags = tags;
    }

    /**
     * Adds CkanTag
     *
     * @param tags The CkanTags elements
     */
    public void addTags(CkanTag... tags) {
        if (this.tags == null) {
            this.tags = new ArrayList<>(tags.length);
        }
        Collections.addAll(this.tags, tags);
    }

    /**
     * The title, like "Hospitals of Trento"
     */
    public String getTitle() {
        return title;
    }

    /**
     * The title, like "Hospitals of Trento"
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

     /**
     * Returns the id if non-empty, the name otherwise
     */
    @Nullable
    public String idOrName() {        
        return isNotEmpty(getId()) ? getId() : getName();
    }
    
    /**
     * Returns the name if non-empty, the id otherwise
     */
    @Nullable
    public String nameOrId() {       
        return isNotEmpty(getName()) ? getName() : getId();
    }

}
