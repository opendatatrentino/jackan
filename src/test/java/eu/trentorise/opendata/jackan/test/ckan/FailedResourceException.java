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
package eu.trentorise.opendata.jackan.test.ckan;

import eu.trentorise.opendata.jackan.CkanClient;

/**
 *
 * @author David Leoni
 */
public class FailedResourceException extends RuntimeException {

    CkanClient client;
    String resourceId;
    String datasetName;

    public FailedResourceException(CkanClient client, String msg, String datasetName, String resourceId) {
        super(msg);
        this.client = client;
        this.resourceId = resourceId;
        this.datasetName = datasetName;
    }

    public FailedResourceException(CkanClient client, String msg, String datasetName, String resourceId, Throwable thrwbl) {
        super(msg, thrwbl);
        this.client = client;
        this.resourceId = resourceId;
        this.datasetName = datasetName;
    }

    public CkanClient getClient() {
        return client;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getDatasetName() {
        return datasetName;
    }

    @Override
    public String toString() {
        String descr;
        try {
            descr = "resource ckan url=" + CkanClient.makeResourceUrl(client.getCatalogUrl(), datasetName, resourceId);
        } catch (Exception ex) {
            descr = "datasetName: " + datasetName + "resourceId: " + resourceId;
        }
        return "FailedResource: \n"
                + "  client=" + client + "\n"
                + "  " + descr + "\n";
    }

}
