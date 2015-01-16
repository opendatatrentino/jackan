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

import static eu.trentorise.opendata.commons.OdtUtils.checkNonEmpty;
import static eu.trentorise.opendata.commons.OdtUtils.checkNonNull;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.Immutable;

/**
 * The class contains a minimum set of fields that are used to create dataset
 *
 * @deprecated use {@link eu.trentorise.opendata.jackan.ckan.CkanDataset}
 * instead. Created by Ivan Tankoyeu on 24/05/2014.
 */
@Immutable
public final class CkanDatasetMinimized {

    private String name;
    private String url;
    private List<CkanPair> extras;
    private String title;
    private String licenseId;

    public CkanDatasetMinimized() {
        this.name = "";
        this.url = "";
        this.extras = new ArrayList();
        this.title = "";
        this.licenseId = "";
    }

    /**
     *
     * @param name
     * @param url A page URL containg a description of the semantified dataset
     * columns and the trasformations done on the original dataset. This URL
     * will be also displayed as metadata in the catalog under dcat:landingPage
     * @param extras
     * @param title
     * @param licenseId
     */
    public CkanDatasetMinimized(String name, String url, List<CkanPair> extras, String title, String licenseId) {
        this();
        checkNonEmpty(name, "ckan dataset name");
        checkNonNull(url, "ckan dataset url to description page");
        checkNonNull(extras, "ckan dataset extras");
        checkNonEmpty(title, "ckan dataset title");
        checkNonNull(licenseId, "ckan dataset license id");
        this.name = name;
        this.url = url;
        this.title = title;
        this.licenseId = licenseId;
        this.extras = extras;
    }

    public String getTitle() {
        return title;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<CkanPair> getExtras() {
        return extras;
    }

}
