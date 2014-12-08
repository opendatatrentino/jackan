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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import static eu.trentorise.opendata.traceprov.impl.TraceProvUtils.checkNonNull;
import static eu.trentorise.opendata.traceprov.impl.TraceProvUtils.isNonEmpty;
import eu.trentorise.opendata.traceprov.impl.dcat.DcatDistribution;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author David Leoni
 */
public class CkanResource {

    @Nullable
    private String cacheLastUpdated;

    @Nullable
    private String cacheUrl;

    /*
     not in rest api 
     
     private String datasetName; // laghi-monitorati-trento
     private String datasetTitle; // Laghi monitorati Trento
     */
    private DateTime cacheUrlUpdated;

    private DateTime created;

    private String description;

    private List<CkanPair> extras;

    private String format;

    private String hash;

    private String id;

    @Nullable
    private DateTime lastModified;

    @Nullable
    private String mimetype;

    @Nullable
    private String mimetypeInner;

    private String name;

    @Nullable
    private String owner;

    private int position;

    private String resourceGroupId;

    private String resourceType;

    private String revisionId;

    @Nullable
    private String revisionTimestamp;

    @Nullable
    private String size;

    private CkanState state;

    private TrackingSummary trackingSummary;

    private String url;

    @Nullable
    private String urlType;

    @Nullable
    private DateTime webstoreLastUpdated;

    @Nullable
    private String webstoreUrl;

    /**
     * Convenience field not actually present in jsons returned by Ckan.
     */
    @Nullable
    private String datasetId;

    /**
     * Returns the dataset this resource belongs to. Convenience getter not
     * actually representing a field in Ckan jsons.
     */
    @JsonIgnore
    @Nullable
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * Convenience setter not actually representing a field in Ckan jsons.
     *
     * @param datasetId the dataset this resource belongs to.
     */
    public void setDatasetId(@Nullable String datasetId) {
        this.datasetId = datasetId;
    }

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras as they should. They will end up here.
     */
    private Map<String, Object> others = new HashMap<String, Object>();

    public CkanResource() {
    }

    /**
     * Custom constructor for dataset creation purpose. todo add stuff.
     */
    public CkanResource(String name, String url, List<CkanPair> extras) {
        this.name = name;
        this.url = url;
        this.extras = extras;

    }

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras as they should. In this case, they end up in 'others'
     * field
     */
    @JsonAnyGetter
    public Map<String, Object> getOthers() {
        return others;
    }

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras as they should. In this case, they end up in 'others'
     * field
     */
    @JsonAnySetter
    public void setOthers(String name, Object value) {
        others.put(name, value);
    }

    /**
     * Should be a Date
     */
    @Nullable
    public String getCacheLastUpdated() {
        return cacheLastUpdated;
    }

    /**
     * Should be a Date
     */
    public void setCacheLastUpdated(@Nullable String cacheLastUpdated) {
        this.cacheLastUpdated = cacheLastUpdated;
    }

    /**
     * God only knows what this is
     */
    @Nullable
    public String getCacheUrl() {
        return cacheUrl;
    }

    /**
     * God only knows what this is
     */
    public void setCacheUrl(@Nullable String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    /**
     * DateTime in UTC timezone
     */
    @Nullable
    public DateTime getCacheUrlUpdated() {
        return cacheUrlUpdated;
    }

    /**
     * internally date is stored with UTC timezone
     */
    public void setCacheUrlUpdated(DateTime cacheUrlUpdated) {
        this.cacheUrlUpdated = cacheUrlUpdated.toDateTime(DateTimeZone.UTC);
    }

    /**
     * i.e. "2013-05-09T14:08:32.666477" . Returned result is always in UTC
     * timezone.
     */
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

    @JsonIgnore
    public Map<String, String> getExtrasAsHashMap() {
        HashMap<String, String> hm = new HashMap();
        for (CkanPair cp : extras) {
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

    /**
     * In Ckan 1.8 was lowercase, 2.2a seems capitalcase.
     */
    public String getFormat() {
        return format;
    }

    /**
     * In Ckan 1.8 was lowercase, 2.2a seems capitalcase.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sometimes for dati.trentino.it can be the empty string
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sometimes for dati.trentino.it can be the empty string
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Returns the alphanumerical id, i.e.
     * "c4577b8f-5603-4098-917e-da03e8ddf461"
     */
    public String getId() {
        return id;
    }

    /**
     * @param id alphanumerical id, i.e. "c4577b8f-5603-4098-917e-da03e8ddf461"
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returned date is always in UTC format i.e. "2013-05-09T14:33:26.643040"
     */
    @Nullable
    public DateTime getLastModified() {
        return lastModified;
    }

    /**
     * Internally date is stored with UTC timezone
     */
    public void setLastModified(@Nullable DateTime lastModified) {
        if (lastModified != null) {
            this.lastModified = lastModified.toDateTime(DateTimeZone.UTC);
        } else {
            this.lastModified = null;
        }
    }

    /**
     * i.e. text/csv
     */
    @Nullable
    public String getMimetype() {
        return mimetype;
    }

    /**
     * @param mimetype i.e. text/csv
     */
    public void setMimetype(@Nullable String mimetype) {
        this.mimetype = mimetype;
    }

    /**
     * Unknown meaning, as usual. Can be the empty string or null
     */
    @Nullable
    public String getMimetypeInner() {
        return mimetypeInner;
    }

    /**
     * Unknown meaning, as usual. Can be the empty string or null
     */
    public void setMimetypeInner(@Nullable String mimetypeInner) {
        this.mimetypeInner = mimetypeInner;
    }

    /**
     *
     * Human readable name, i.e. "Apple Production 2013 in CSV format"
     *
     *
     * Notice we found name null in data.gov.uk datasets... i.e.
     * http://data.gov.uk/api/3/action/resource_show?id=77d2dba8-d0d9-49ef-9fd2-37a4a8bc5a17
     * taken from this dataset search:
     * http://data.gov.uk/api/3/action/package_search?rows=20&start=0 They use
     * description field instead
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Human readable name, i.e. "Apple Production 2013 in CSV format" We found
     * name null in data.gov.uk datasets... i.e.
     * http://data.gov.uk/api/3/action/resource_show?id=77d2dba8-d0d9-49ef-9fd2-37a4a8bc5a17
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Username of the owner
     */
    @Nullable
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner Username of the owner
     */
    public void setOwner(@Nullable String owner) {
        this.owner = owner;
    }

    /**
     * Position inside the dataset?
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position Position inside the dataset?
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * todo - What the hell is this? alphanumerical id, i.e.
     * "fd6375cd-1d6a-41e8-8e10-460a11e2308e"
     */
    public String getResourceGroupId() {
        return resourceGroupId;
    }

    /**
     * todo - What the hell is this? alphanumerical id, i.e.
     * "fd6375cd-1d6a-41e8-8e10-460a11e2308e"
     */
    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }

    /**
     * So far, found: "api", "file", "file.upload"
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * So far, found: "api", "file", "file.upload"
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * alphanumerical id, i.e. 0c949f17-d123-4379-8536-cfcf25b3b0e9
     */
    public String getRevisionId() {
        return revisionId;
    }

    /**
     * alphanumerical id, i.e. 0c949f17-d123-4379-8536-cfcf25b3b0e9
     */
    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    /**
     * Should be a date
     */
    @Nullable
    public String getRevisionTimestamp() {
        return revisionTimestamp;
    }

    /**
     * Should be a date
     */
    public void setRevisionTimestamp(@Nullable String revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    /**
     * @return File size in bytes, if calculated by ckan for files in storage,
     * like i.e. "242344". Otherwise it can be anything a human can insert.
     */
    @Nullable
    public String getSize() {
        return size;
    }

    /**
     * @param size File size in bytes, if calculated by ckan for files in
     * storage, like i.e. "242344". Otherwise it can be anything a human can
     * insert.
     */
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

    /**
     * Returns the Url to the pyhsical file, i.e.
     * http://dati.trentino.it/storage/f/2013-05-09T140831/TRENTO_Laghi_monitorati_UTM.csv
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the Url to the pyhsical file, i.e.
     * http://dati.trentino.it/storage/f/2013-05-09T140831/TRENTO_Laghi_monitorati_UTM.csv
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * todo - Don't know what it is
     */
    @Nullable
    public String getUrlType() {
        return urlType;
    }

    /**
     * todo - Don't know what it is
     */
    public void setUrlType(@Nullable String urlType) {
        this.urlType = urlType;
    }

    /**
     * Should be a Date. It is always returned in UTC timezone
     */
    @Nullable
    public DateTime getWebstoreLastUpdated() {
        return webstoreLastUpdated;
    }

    /**
     * Internally the date is stored with UTC timezone
     */
    public void setWebstoreLastUpdated(@Nullable DateTime webstoreLastUpdated) {
        if (webstoreLastUpdated != null) {
            this.webstoreLastUpdated = webstoreLastUpdated.toDateTime(DateTimeZone.UTC);
        } else {
            this.webstoreLastUpdated = null;
        }
    }

    /**
     * Found "active" as value. Maybe it is a CkanState?
     */
    @Nullable
    public String getWebstoreUrl() {
        return webstoreUrl;
    }

    /**
     * @param webstoreUrl Found "active" as value. Maybe it is a CkanState?
     */
    public void setWebstoreUrl(@Nullable String webstoreUrl) {
        this.webstoreUrl = webstoreUrl;
    }

   
    /** TODO this converter is largely incomplete!!! 
     * 
     * @param catalogURL may be an empty string
     * @param datasetId may be an empty string
     * @param license may be an empty string
     * 
     */
    public DcatDistribution toDcatDistribution(String catalogURL, String datasetId, String license){
        CkanClient.logger.warning("CONVERSION FROM CKAN RESOURCE TO DCAT DISTRIBUTION IS STILL EXPERIMENTAIL, IT MIGHT BE INCOMPLETE!!!");
        checkNonNull(catalogURL, "catalog URL");        
        checkNonNull(datasetId, "dataset id");        
        checkNonNull(license, "license");        
        
        String nCatalogUrl = TraceProvUtils.removeTrailingSlash(catalogURL);
        
        DcatDistribution dd = new DcatDistribution();                
        
        String resURL = "";
        
        if (isNonEmpty(catalogURL) && isNonEmpty(datasetId) && isNonEmpty(license)){
            resURL = CkanClient.makeResourceURL(nCatalogUrl, datasetId, this.getId());  
        }
        
        if (resURL.length() > 0){
            dd.setAccessURL(resURL);
        }
        
        if (this.getUrl() != null){
            dd.setDownloadURL(this.getUrl());
        }
                
        try {
            if (this.getSize() != null){
                dd.setByteSize(Integer.parseInt(this.getSize()));
            }
            
        } catch (NumberFormatException ex){
            CkanClient.logger.log(Level.WARNING, "COULDN'T CONVERT CKAN RESOURCE SIZE TO DCAT! REQUIRED AN INTEGER, FOUND {0} (ALTHOUGH STRINGS ARE VALID CKAN SIZES)", this.getSize());
        }
        
        if (this.getDatasetId() != null){
            dd.setDatasetIdentifier(this.getDatasetId());
        }
        
        if (this.getDescription() != null){
            dd.setDescription(this.getDescription());
        }
        
        
        CkanClient.logger.warning("TODO - SKIPPED 'DOWNLOAD URL' WHILE CONVERTING FROM CKAN TO DCAT");        
        //dd.setDownloadURL(null);
        
        if (this.getFormat() != null){
            dd.setFormat(this.getFormat());
        }
        
        DateTime lastModified = this.getLastModified();
        if (lastModified != null){
            dd.setIssued(lastModified.toString());
        }     
        if (license != null){
            dd.setLicense(license);
        }
        if (this.getMimetype() != null){
            dd.setMediaType(this.getMimetype());
        }
        if (this.getRevisionTimestamp() != null){
            dd.setModified(this.getRevisionTimestamp());
        }
        
        CkanClient.logger.warning("TODO - SKIPPED 'RIGHTS' WHILE CONVERTING FROM CKAN TO DCAT");        
        //dd.setRights("");        
        
        CkanClient.logger.warning("TODO - SKIPPED 'SPATIAL' WHILE CONVERTING FROM CKAN TO DCAT");        
        // dd.setSpatial("");
        
        if (this.getName() != null){
            dd.setTitle(this.getName());
        }
        
        if (resURL.length() > 0){
            dd.setURI(resURL);
        }
                
        return dd;
    }    
    
}
