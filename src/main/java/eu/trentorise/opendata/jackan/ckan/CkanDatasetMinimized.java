/* 
 * Copyright 2015 Trento RISE (trentorise.eu)
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

import java.util.List;

/** The class contains a  minmum set of fields that are used to create dataset
 * Created by Ivan Tankoyeu on 24/05/2014.
 */
public class CkanDatasetMinimized {

    private String name;
    private String url;
    private List<CkanPair> extras;
    private String title;
    private String licenseId;


    /**
     * 
     * @param name
     * 
     * @param url A page URL containg a description of the semantified dataset columns and the trasformations done on the original dataset. This URL will be also displayed as metadata in the catalog under dcat:landingPage
               

     * @param extras
     * @param title
     * @param licenseId 
     */
    public CkanDatasetMinimized(String name, String url, List<CkanPair> extras, String title, String licenseId) {
        this.name = name;
        this.url = url;
        this.title = title;
        this.licenseId = licenseId;
        this.extras = extras;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<CkanPair> getExtras() {
        return extras;
    }

    public void setExtras(List<CkanPair> extras) {
        this.extras = extras;
    }
}
