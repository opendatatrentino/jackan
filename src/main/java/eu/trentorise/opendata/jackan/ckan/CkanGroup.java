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

import java.util.ArrayList;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;



/**
 *
 * @author David Leoni
 */
public class CkanGroup {
    /**
     * can be "approved" or what? Bah 
     */
    private String approvalStatus;
    private DateTime created;
    private String description;
    /**
     * i.e. Gestione del Territorio
     */
    private String displayName;
    private ArrayList<CkanPair> extras;
    /**
     * Have no idea what this could mean inside a group!
     */
    private ArrayList<CkanGroup> groups;
    
    private String id;
    
    private String imageDisplayUrl;
    
    private String imageUrl;
    
    private boolean organization;
    
    /**
     * name in the url. i.e. gestione-del-territorio
     */
    private String name;
    
    private int numFollowers;
    
    private int packageCount;
    
    // better to comment it as it can also be an int according to which web api is called
    // private ArrayList<CkanDataset> packages;
    
    private String revisionId;
    
    private CkanState state;
    
    /**
     * i.e. "Gestione del territorio"
     */
    private String title;
    
    /**
     * Don't know possible ckan types
     */
    private String type;
    
    private ArrayList<CkanUser> users;

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
       internally date is stored with UTC timezone
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<CkanPair> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<CkanPair> extras) {
        this.extras = extras;
    }

    public ArrayList<CkanGroup> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<CkanGroup> groups) {
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

    @JsonProperty("is_organization")
    public boolean isOrganization() {
        return organization;
    }

    public void setOrganization(boolean organization) {
        this.organization = organization;
    }

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

    public ArrayList<CkanUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<CkanUser> users) {
        this.users = users;
    }
    
}
