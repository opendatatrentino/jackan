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
import javax.annotation.Nullable;



/**
 * @author David Leoni
 */
public class CkanResource {
    /**
     * Should be a Date
     */
    @Nullable
    private String cacheLastUpdated;
    /**
     * God only knows what this is
     */
    @Nullable 
    private String cacheUrl;
    /*
    not in rest api 
     
    private String datasetName; // laghi-monitorati-trento
    private String datasetTitle; // Laghi monitorati Trento
    */
    /**
     * Should be a Date
     */    
    private String cacheUrlUpdated;
            
    private String created;
    
    private String description;
    
    private ArrayList<CkanPair> extras;
    
    /**
     * In Ckan 1.8 was lowercase, 2.2a seems capitalcase. 
     */
    private String format;
    
    /**
     * Sometimes for dati.trentino.it can be the empty string
     */
    private String hash;
    
    /**
     * alphanumerical id, i.e. "c4577b8f-5603-4098-917e-da03e8ddf461"
     */
    private String id;
    
    
    private String lastModified;
    
    private String mimetype;
    
    /**
     * Unknown meaning, as usual. Can be the empty string or null
     */
    private @Nullable String mimetypeInner;
    
    /**
     * Human readable name, i.e. "Apple Production 2013 in CSV format"
     */
    private String name;
    
    /**
     * username of the owner i.e. 
     */
    private @Nullable String owner;
    
    /**
     * Position inside the dataset?
     */
    private int position;
    /**
     * todo - What the hell is this?
     * alphanumerical id, i.e.  "fd6375cd-1d6a-41e8-8e10-460a11e2308e"
     */
    private String resourceGroupId;
    /**
     * so far, found: api,  file, file.upload
     */
    private String resourceType;
    /**
     * alphanumerical id, 0c949f17-d123-4379-8536-cfcf25b3b0e9
     */
    private String revisionId;
    /**
     * Should be a Date
     */
    private @Nullable String revisionTimestamp;
    /**
     * file size in bytes. Note ckan returns a string, we hope it's always an integer.
     */
    private int size;
    
    private CkanState state;
    
    private TrackingSummary trackingSummary;
    private String url;
    /**
     * Don't know what it is
     */
    private @Nullable String urlType;
    
    /**
     * Should be a Date
     */
    private @Nullable String webstoreLastUpdated;

    /**
     * found "active" as value. Maybe it is a CkanState
     */
    private @Nullable String webstoreUrl;
    
    

    
    

    public @Nullable String getCacheLastUpdated() {
        return cacheLastUpdated;
    }

    public void setCacheLastUpdated(@Nullable String cacheLastUpdated) {
        this.cacheLastUpdated = cacheLastUpdated;
    }

    public @Nullable String getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(@Nullable String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    public @Nullable String getCacheUrlUpdated() {
        return cacheUrlUpdated;
    }

    public void setCacheUrlUpdated(String cacheUrlUpdated) {
        this.cacheUrlUpdated = cacheUrlUpdated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<CkanPair> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<CkanPair> extras) {
        this.extras = extras;
    }    
    
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public @Nullable String getMimetypeInner() {
        return mimetypeInner;
    }

    public void setMimetypeInner(@Nullable String mimetypeInner) {
        this.mimetypeInner = mimetypeInner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @Nullable String getOwner() {
        return owner;
    }

    public void setOwner(@Nullable String owner) {
        this.owner = owner;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public @Nullable String getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(@Nullable String revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public CkanState getState() {
        return state;
    }

    public void setState(CkanState state) {
        this.state = state;
    }

    public TrackingSummary getTrackingSummary() {
        return trackingSummary;
    }

    public void setTrackingSummary(TrackingSummary trackingSummary) {
        this.trackingSummary = trackingSummary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public @Nullable String getUrlType() {
        return urlType;
    }

    public void setUrlType(@Nullable String urlType) {
        this.urlType = urlType;
    }

    public @Nullable String getWebstoreLastUpdated() {
        return webstoreLastUpdated;
    }

    public void setWebstoreLastUpdated(@Nullable String webstoreLastUpdated) {
        this.webstoreLastUpdated = webstoreLastUpdated;
    }

    public @Nullable String getWebstoreUrl() {
        return webstoreUrl;
    }

    public void setWebstoreUrl(@Nullable String webstoreUrl) {
        this.webstoreUrl = webstoreUrl;
    }
        
 

    
}    
