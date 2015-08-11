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
package eu.trentorise.opendata.jackan;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Usage example:  {@code
 *      CkanQuery.filter().byText("litigations").byGroupNames("justice")
 * }
 *
 * @author David Leoni
 */
public final class CkanQuery {

    private String text;
    private List<String> groupNames;
    private List<String> organizationNames;
    private List<String> tagNames;
    private List<String> licenseIds;

    private CkanQuery() {
        this.text = "";
        this.groupNames = new ArrayList();
        this.organizationNames = new ArrayList();
        this.tagNames = new ArrayList();
        this.licenseIds = new ArrayList();
    }

    /**
     * Each filtered dataset must belong to all the given groups i.e.
     * "british-academy", "home-office", "newcastle-city-council"
     */
    public CkanQuery byGroupNames(Iterable<String> groupNames) {
        this.groupNames = Lists.newArrayList(groupNames);
        return this;
    }

    /**
     * Each filtered dataset must belong to all the given groups i.e.
     * "british-academy", "home-office", "newcastle-city-council"
     */
    public CkanQuery byGroupNames(String... groupNames) {
        this.groupNames = Arrays.asList(groupNames);
        return this;
    }

    /**
     * @param text i.e. "health care London"
     */
    public CkanQuery byText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Each filtered dataset must belong to the given organization
     *
     * @param organizationName i.e. "audit-commission",
     * "remploy-limited","royal-society"
     */
    public CkanQuery byOrganizationName(String organizationName) {
        this.organizationNames = Lists.newArrayList(organizationName);
        return this;
    }

    /**
     * Each filtered dataset must have all the given tags
     *
     * @param tagNames i.e. "Community health partnership", "youth-justice",
     * "trade-policy",
     */
    public CkanQuery byTagNames(Iterable<String> tagNames) {
        this.tagNames = Lists.newArrayList(tagNames);
        return this;
    }

    /**
     * Each filtered dataset must have all the given tags
     *
     * @param tagNames i.e. "Community health partnership", "youth-justice",
     * "trade-policy",
     */
    public CkanQuery byTagNames(String... tagNames) {
        this.tagNames = Arrays.asList(tagNames);
        return this;
    }

    /**
     * Each filtered dataset must have the given license
     *
     * @param licenseId i.e. "cc-by", "odc-by"
     */
    public CkanQuery byLicenseId(String licenseId) {
        this.licenseIds = Lists.newArrayList(licenseId);
        return this;
    }

    /**
     * Factory method to start creating the query.
     */
    public static CkanQuery filter() {
        return new CkanQuery();
    }

    public String getText() {
        return text;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public List<String> getOrganizationNames() {
        return organizationNames;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public List<String> getLicenseIds() {
        return licenseIds;
    }

}
