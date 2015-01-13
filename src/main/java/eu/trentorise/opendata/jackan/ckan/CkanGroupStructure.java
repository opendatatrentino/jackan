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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * This class models the same data structure that Ckan uses for both groups and
 * organizations. Since they are different things and work with different apis
 * we made two different implementations. For groups use {@link CkanGroup} and
 * organizations use {@link CkanOrganization} (Ckan way to tell the difference
 * is the {@link #isOrganization() } field).
 *
 * @author David Leoni
 */
public abstract class CkanGroupStructure {

    private String approvalStatus;
    private DateTime created;
    private String description;
    private String displayName;
    private List<CkanPair> extras;

    private List<CkanGroup> groups;

    private String id;

    private String imageDisplayUrl;

    private String imageUrl;

    private boolean organization;

    private String name;

    private int numFollowers;

    private int packageCount;

    // better to comment it as it can also be an int according to which web api is called
    // private List<CkanDataset> packages;
    private String revisionId;

    private CkanState state;

    private String title;

    private String type;

    private List<CkanUser> users;

    public CkanGroupStructure() {
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

    public DateTime getCreated() {
        return created;
    }

    /**
     * internally date is stored with UTC timezone
     */
    public void setCreated(DateTime created) {
        this.created = created.toDateTime(DateTimeZone.UTC);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * i.e. Gestione del Territorio
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getImageDisplayUrl() {
        return imageDisplayUrl;
    }

    public void setImageDisplayUrl(String imageDisplayUrl) {
        this.imageDisplayUrl = imageDisplayUrl;
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
     * name in the url. i.e. gestione-del-territorio
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
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
     * Human readable name, i.e. "Gestione del territorio"
     *
     * @see #getName()
     */
    public String getTitle() {
        return title;
    }

    /**
     * Human readable name, i.e. "Gestione del territorio"
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
