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

import javax.annotation.Nullable;

/**
 * {@inheritDoc}
 */
public class CkanResource extends CkanResourceBase {

    private String resourceGroupId;
    private String owner;
    private int position;
    private String revisionTimestamp;
    private TrackingSummary trackingSummary;
    private CkanState state;
    private String urlType;

    public CkanResource() {
    }

    /**
     * {@inheritDoc}     
     */
    public CkanResource(String url, String packageId) {
        super(url, packageId);
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
     *
     */
    public String getRevisionTimestamp() {
        return revisionTimestamp;
    }

    /**
     *
     */
    public void setRevisionTimestamp(@Nullable String revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
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
     * todo - Don't know what it is
     */    
    public String getUrlType() {
        return urlType;
    }

    /**
     * todo - Don't know what it is
     */
    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }    
}
