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

import java.sql.Timestamp;

/**
 * Abstract class with additional fields found during searches of groups and
 * organizations.
 *
 * @author David Leoni
 */
abstract class CkanGroupOrg extends CkanGroupOrgBase {

    private Timestamp created;
    private String displayName;
    private String imageDisplayUrl;
    private int numFollowers;
    private int packageCount;
    
    protected CkanGroupOrg(){
        super();
    }
    
    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageDisplayUrl() {
        return imageDisplayUrl;
    }

    public void setImageDisplayUrl(String imageDisplayUrl) {
        this.imageDisplayUrl = imageDisplayUrl;
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
}
