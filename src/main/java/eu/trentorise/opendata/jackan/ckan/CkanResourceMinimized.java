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

/**
 * @deprecated use {@link eu.trentorise.opendata.jackan.ckan.CkanResource}
 * instead Created by Ivan Tankoyeu on 23/05/2014.
 */
public class CkanResourceMinimized {

    private String format;
    private String name;
    private String url;
    private String description;
    private String packageId;
    private String mimetype;
    private String id;

    public CkanResourceMinimized(String format, String name, String url, String description, String packageId, String mimetype) {
        this.format = format;
        this.name = name;
        this.url = url;
        this.description = description;
        this.packageId = packageId;
        this.mimetype = mimetype;

    }

    public CkanResourceMinimized(String format, String name, String url, String description, String packageId, String mimetype, String resourceId) {
        this.format = format;
        this.name = name;
        this.url = url;
        this.description = description;
        this.packageId = packageId;
        this.mimetype = mimetype;
        this.id = resourceId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

}
