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
import java.util.ArrayList;
import javax.annotation.Nullable;


/**
 *
 * @author David Leoni
 */
public class CkanDataset {
    private String author;
    private String authorEmail;  
    private @Nullable String creatorUserId;
    private ArrayList<CkanGroup> groups;
    private String downloadUrl;
    private ArrayList<CkanPair> extras;
    private String id;
    private boolean isOpen;
    private String license;
    private String licenseTitle;
    private String licenseUrl;
    private String maintainer;
    private String maintainerEmail;
    /**
     * This should be a Date - couldn't find format documentation in Ckan api 2.2a
     */
    private String metadata;
    /**
     * This should be a Date - couldn't find format documentation in Ckan api 2.2a
     */
    private String metadata_modified;
    private String name;
    private String notes;
    private String notesRendered;
    private String ownerOrg;
    private String revisionId;
    /**
     * This should be a Date - couldn't find format documentation in Ckan api 2.2a
    */
    private String revisionTimestamp;
    private String state; // todo what should it be?
    private ArrayList<CkanTag> tags;
    private String title;
    private String type;
    private String url;
    private @Nullable String version;
    


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

    public @Nullable String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(@Nullable String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public ArrayList<CkanGroup> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<CkanGroup> groups) {
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

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getMetadata_modified() {
        return metadata_modified;
    }

    public void setMetadata_modified(String metadata_modified) {
        this.metadata_modified = metadata_modified;
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

    public String getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(String revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<CkanTag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<CkanTag> tags) {
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


    public @Nullable String getVersion() {
        return version;
    }

    public void setVersion(@Nullable String version) {
        this.version = version;
    }

    public ArrayList<CkanPair> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<CkanPair> extras) {
        this.extras = extras;
    }
    
    public DcatDataset toDcatDataset(){
         DcatDataset ret = new DcatDataset();
         
         return ret;
    }    
}
