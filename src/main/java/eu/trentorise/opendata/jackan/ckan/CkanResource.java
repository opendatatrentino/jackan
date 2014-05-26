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

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;



/**
 * @author David Leoni
 */
public class CkanResource {
    /**
     * Should be a Date
     */    
    @Nullable private String cacheLastUpdated;
    /**
     * God only knows what this is
     */
    
    @Nullable private String cacheUrl;
    /*
    not in rest api 
     
    private String datasetName; // laghi-monitorati-trento
    private String datasetTitle; // Laghi monitorati Trento
    */
  
    /**
     * DateTime in UTC timezone
     */
    private DateTime cacheUrlUpdated;
    
    /**
     * i.e. "2013-05-09T14:08:32.666477"
     */
    private DateTime created;
    
    private String description;
    
    private List<CkanPair> extras;
    
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
    
    /**
     * i.e. "2013-05-09T14:33:26.643040"
     */
    @Nullable
    private DateTime lastModified;
    
    /**
     * i.e. text/csv
     */
    @Nullable
    private String mimetype;
    
    /**
     * Unknown meaning, as usual. Can be the empty string or null
     */
    @Nullable private String mimetypeInner;
    
    /**
     * Human readable name, i.e. "Apple Production 2013 in CSV format"
     */
    private String name;
    
    /**
     * username of the owner i.e. 
     */
    @Nullable private String owner;
    
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
    @Nullable private String revisionTimestamp;
    /**
     * File size in bytes, if calculated by ckan for files in storage. 
     * Otherwise it can be anything a human can insert. i.e. "242344"
     */
    @Nullable
    private String size;
    
    private CkanState state;
    
    private TrackingSummary trackingSummary;
    private String url;
    /**
     * Don't know what it is
     */
    @Nullable private String urlType;
    
    /**
     * Should be a Date
     */
    @Nullable private DateTime webstoreLastUpdated;

    /**
     * found "active" as value. Maybe it is a CkanState
     */
    @Nullable private String webstoreUrl;
    
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
    

    @Nullable public String getCacheLastUpdated() {
        return cacheLastUpdated;
    }

    public void setCacheLastUpdated(@Nullable String cacheLastUpdated) {
        this.cacheLastUpdated = cacheLastUpdated;
    }

    @Nullable public String getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(@Nullable String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }
    
    @Nullable public DateTime getCacheUrlUpdated() {
        return cacheUrlUpdated;
    }

    /**
       internally date is stored with UTC timezone
    */   

    public void setCacheUrlUpdated(DateTime cacheUrlUpdated) {
        this.cacheUrlUpdated = cacheUrlUpdated.toDateTime(DateTimeZone.UTC);
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

    @JsonIgnore
    public Map<String,String> getExtrasAsHashMap(){
        HashMap<String,String> hm = new HashMap();
        for (CkanPair cp : extras){
            hm.put(cp.getKey(), cp.getValue());
        }
        return hm;
    }    
    
    public List<CkanPair> getExtras() {
        return extras;
    }

    public void setExtras(List<CkanPair> extras) {
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

    @Nullable
    public DateTime getLastModified() {
        return lastModified;
    }

    /**
       internally date is stored with UTC timezone
    */       
    public void setLastModified(@Nullable DateTime lastModified) {
        if (lastModified != null){
            this.lastModified = lastModified.toDateTime(DateTimeZone.UTC);  
        } else {
            this.lastModified = null;
        }
    }

    @Nullable
    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(@Nullable String mimetype) {
        this.mimetype = mimetype;
    }

    @Nullable public String getMimetypeInner() {
        return mimetypeInner;
    }

    public void setMimetypeInner(@Nullable String mimetypeInner) {
        this.mimetypeInner = mimetypeInner;
    }

    /**
     * We found name null in data.gov.uk datasets... i.e. 
     * http://data.gov.uk/api/3/action/resource_show?id=77d2dba8-d0d9-49ef-9fd2-37a4a8bc5a17
     * taken from this dataset search:
     * http://data.gov.uk/api/3/action/package_search?rows=20&start=0
     * They use description field instead
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * We found name null in data.gov.uk datasets... i.e. http://data.gov.uk/api/3/action/resource_show?id=77d2dba8-d0d9-49ef-9fd2-37a4a8bc5a17
     */    
    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable public String getOwner() {
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

    @Nullable public String getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(@Nullable String revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    /**
     * @return File size in bytes, if calculated by ckan for files in storage. i.e. "242344" 
     * Otherwise it can be anything a human can insert.
     */
    @Nullable
    public String getSize() {
        return size;
    }

    public void setSize(@Nullable String size) {
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

    @Nullable public String getUrlType() {
        return urlType;
    }

    public void setUrlType(@Nullable String urlType) {
        this.urlType = urlType;
    }

    @Nullable public DateTime getWebstoreLastUpdated() {
        return webstoreLastUpdated;
    }

    /**
       internally date is stored with UTC timezone
    */       
    public void setWebstoreLastUpdated(@Nullable DateTime webstoreLastUpdated) {
        if (webstoreLastUpdated != null) {
            this.webstoreLastUpdated = webstoreLastUpdated.toDateTime(DateTimeZone.UTC);
        } else {
            this.webstoreLastUpdated = null;
        }
    }

    @Nullable public String getWebstoreUrl() {
        return webstoreUrl;
    }

    public void setWebstoreUrl(@Nullable String webstoreUrl) {
        this.webstoreUrl = webstoreUrl;
    }
        
 

    
}    
