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
 * Interface with additional fields found during searches of groups and organizations.
 *
 * @author David Leoni
 */
interface CkanGroupOrg {

    /**
     * Ckan always refers to UTC timezone
     */
    public Timestamp getCreated();

    /**
     * Ckan always refers to UTC timezone
     */
    public void setCreated(Timestamp created);

    /**
     * i.e. Department of Justice
     */
    public String getDisplayName();

    /**
     * i.e. Department of Justice
     */
    public void setDisplayName(String displayName);

    public String getImageDisplayUrl();

    public void setImageDisplayUrl(String imageDisplayUrl);

    public int getNumFollowers();

    public void setNumFollowers(int numFollowers);

    public int getPackageCount();

    public void setPackageCount(int packageCount);

}
