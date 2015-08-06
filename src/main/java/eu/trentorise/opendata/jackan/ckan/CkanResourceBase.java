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
package eu.trentorise.opendata.jackan.ckan;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 *
 * A Ckan Resource describes with metadata a physical file, which may reside
 * outside ckan. Resources are part of {@link CkanDataset}. In DCAT terminology,
 * a Ckan Resource is a DCAT Distribution.
 *
 * {@link CkanResourceBase} holds fields that can be sent when
 * <a href="http://docs.ckan.org/en/latest/api/index.html?#ckan.logic.action.create.resource_create" target="_blank">creating
 * a resource,</a>, while {@link CkanResource} holds more fields that can be
 * returned with searches.
 *
 * This class initializes nothing to fully preserve all we get from ckan. In
 * practice, all fields of retrieved resources can be null except maybe
 * {@code url}.
 *
 * @author David Leoni
 */
public class CkanResourceBase {

    private String cacheLastUpdated;
    private String cacheUrl;
    private Timestamp created;
    private String description;
    private String format;
    private String hash;
    private String id;
    private Timestamp lastModified;
    private String mimetype;
    private String mimetypeInner;
    private String name;
    private String resourceType;
    private String revisionId;
    private String size;
    private String url;

    private Timestamp webstoreLastUpdated;

    private String webstoreUrl;

    /**
     * The dataset this resource belongs to. Not present when getting resources
     * but needed when uploading them.
     */
    private String packageId;

    /**
     * See {@link #getOthers()}
     */
    private Map<String, Object> others;

    /**
     * The dataset this resource belongs to. Not present when getting resources
     * but needed when uploading them.
     */
    @Nullable
    public String getPackageId() {
        return packageId;
    }

    /**
     * The dataset id the resource belongs to. Not present when getting
     * resources but needed when uploading them.
     *
     * @param packageId the dataset this resource belongs to.
     */
    public void setPackageId(@Nullable String packageId) {
        this.packageId = packageId;
    }

    public CkanResourceBase() {
    }

    /**
     * Constructor with the minimal list of required items to successfully
     * create a resource on the server.
     *
     * @param url the Url to the pyhsical file i.e.
     * http://dati.trentino.it/storage/f/2013-05-09T140831/TRENTO_Laghi_monitorati_UTM.csv
     * (could also be a file outside ckan server)
     * @param packageId id of the dataset that contains the resource
     */
    public CkanResourceBase(String url,
            String packageId) {
        this();
        this.url = url;
        this.packageId = packageId;
    }

    /**
     * CKAN instances might have
     * <a href="http://docs.ckan.org/en/latest/extensions/adding-custom-fields.html">
     * custom data schemas</a> that force presence of custom properties among
     * 'regular' ones. In this case, they go to 'others' field.
     *
     * @see #putOthers(java.lang.String, java.lang.Object)
     */
    @JsonAnyGetter
    @Nullable
    public Map<String, Object> getOthers() {
        return others;
    }

    /**
     * @param others
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
            others = new HashMap();
        }
        others.put(name, value);
    }

    /**
     * Should be a Timestamp
     */
    public String getCacheLastUpdated() {
        return cacheLastUpdated;
    }

    /**
     * Should be a Timestamp
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
     * In JSON is something like this: i.e. "2013-05-09T14:08:32.666477" . Ckan
     * always refers to UTC timezone
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
     * Ckan always refers to UTC timezone
     */
    @Nullable
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    public void setLastModified(@Nullable Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * i.e. text/csv
     */
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
     * Human readable name, i.e. "Apple Production 2013 in CSV format". Not to
     * be confused with {@link CkanDataset#name} which instead is lowercased and
     * intended to be part of the url.
     *
     *
     * Notice we found name null in data.gov.uk datasets... i.e.
     * <a href="http://data.gov.uk/api/3/action/resource_show?id=77d2dba8-d0d9-49ef-9fd2-37a4a8bc5a17" target="_blank">
     * unclaimed-estates-list </a>, taken
     * <a href="http://data.gov.uk/api/3/action/package_search?rows=20&start=0" target="_blank">from
     * this dataset search</a> (They use description field instead)
     */
    public String getName() {
        return name;
    }

    /**
     * Human readable name, i.e. "Apple Production 2013 in CSV format". Not to
     * be confused with {@link CkanDataset#name} which instead is lowercased and
     * intended to be part of the url. For Nullable explanation see
     * {@link #getName()}
     */
    public void setName(@Nullable String name) {
        this.name = name;
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
     * @return File size in bytes, if calculated by ckan for files in storage,
     * like i.e. "242344". Otherwise it can be anything a human can insert.
     */
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

    /**
     * The Url to the pyhsical file i.e.
     * http://dati.trentino.it/storage/f/2013-05-09T140831/TRENTO_Laghi_monitorati_UTM.csv
     * (could also be a file outside ckan server)
     */
    public String getUrl() {
        return url;
    }

    /**
     * The Url to the pyhsical file i.e.
     * http://dati.trentino.it/storage/f/2013-05-09T140831/TRENTO_Laghi_monitorati_UTM.csv
     * (could also be a file outside ckan server)
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    public Timestamp getWebstoreLastUpdated() {
        return webstoreLastUpdated;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    public void setWebstoreLastUpdated(@Nullable Timestamp webstoreLastUpdated) {
        this.webstoreLastUpdated = webstoreLastUpdated;
    }

    /**
     * Found "active" as value. Maybe it is a CkanState?
     */
    public String getWebstoreUrl() {
        return webstoreUrl;
    }

    /**
     * @param webstoreUrl Found "active" as value. Maybe it is a CkanState?
     */
    public void setWebstoreUrl(String webstoreUrl) {
        this.webstoreUrl = webstoreUrl;
    }
}
