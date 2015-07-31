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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * This abstract class models the same data structure that Ckan uses for both
 * groups and organizations. Since they are different things and work with
 * different APIs we made two different implementations. For creating/updating
 * groups use {@link CkanGroupBase} and for reading them use {@link CkanGroup}.
 * For creating/updating organizations use {@link CkanOrganizationBase} and for
 * reading them use {@link CkanOrganization}. The Ckan way to tell the
 * difference is the {@link #isOrganization() } field).
 *
 * @author David Leoni
 */
abstract class CkanGroupOrgBase {

    private String approvalStatus;
    private String description;
    private List<CkanPair> extras;
    private List<CkanGroup> groups;
    private String id;

    private String imageUrl;
    private boolean organization;
    private String name;

    // better to comment it as it can also be an int according to which web api is called
    // private List<CkanDataset> packages;
    private String revisionId;
    private CkanState state;
    private String title;
    private String type;
    private List<CkanUser> users;

    protected CkanGroupOrgBase() {
    }

    /**
     * Constructor with minimal amount of parameters needed to successfully
     * create an instance on the server.
     *
     * @param name  Name in the url, lowercased and without spaces. i.e.
     * management-of-territory
     */
    protected CkanGroupOrgBase(String name) {        
        this.name = name;
    }

    /**
     * can be "approved" or what? Bah
     */
    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CkanPair> getExtras() {
        return extras;
    }

    public void setExtras(List<CkanPair> extras) {
        this.extras = extras;
    }

    /**
     * Have no idea what this could mean inside a group!
     */
    public List<CkanGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CkanGroup> groups) {
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * A ckan group can also be an organization.
     */
    @JsonProperty("is_organization")
    public boolean isOrganization() {
        return organization;
    }

    /**
     * Protected, we use it only when deserializing
     */
    @JsonProperty("is_organization")
    protected void setOrganization(boolean organization) {
        this.organization = organization;
    }

    /**
     * Name in the url, lowercased and without spaces. i.e.
     * management-of-territory
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name in the url, lowercased and without spaces. i.e.
     * management-of-territory
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public CkanState getState() {
        return state;
    }

    public void setState(CkanState state) {
        this.state = state;
    }

    /**
     * Human readable name, i.e. "Department of Justice"
     *
     * @see #getName()
     */
    public String getTitle() {
        return title;
    }

    /**
     * Human readable name, i.e. "Department of Justice"
     *
     * @see #setName(java.lang.String)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    /**
     * Don't know possible ckan types
     */
    public void setType(String type) {
        this.type = type;
    }

    public List<CkanUser> getUsers() {
        return users;
    }

    public void setUsers(List<CkanUser> users) {
        this.users = users;
    }

}