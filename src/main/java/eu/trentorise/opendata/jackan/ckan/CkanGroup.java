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

import java.sql.Timestamp;

/**
 * {@inheritDoc} 
 */
public class CkanGroup extends CkanGroupBase implements CkanGroupOrg {

    private Timestamp created;
    private String displayName;
    private String imageDisplayUrl;
    private int numFollowers;
    private int packageCount;

    public CkanGroup() {
        super();
    }

    public CkanGroup(String name) {
        super(name);
    }
                 
    @Override
    public Timestamp getCreated() {
        return created;
    }

    @Override
    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getImageDisplayUrl() {
        return imageDisplayUrl;
    }

    @Override
    public void setImageDisplayUrl(String imageDisplayUrl) {
        this.imageDisplayUrl = imageDisplayUrl;
    }

    @Override
    public int getNumFollowers() {
        return numFollowers;
    }

    @Override
    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }

    @Override
    public int getPackageCount() {
        return packageCount;
    }

    @Override
    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }


}
