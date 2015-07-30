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

import org.apache.http.HttpHost;

/**
 * This client performs additional checks when writing to CKAN to ensure written
 * content is correct. For this reason it might do additional calls and results
 * of validation might be different from default Ckan ones. But if Ckan actually
 * performed all the checks it should do there wouldn't be any need of this
 * class as well..
 *
 * @author David Leoni
 */
public class CheckedCkanClient extends CkanClient {

    public CheckedCkanClient(String url) {
        super(url);
    }

    public CheckedCkanClient(String URL, String token) {
        super(URL, token);
    }

    public CheckedCkanClient(String URL, String token, HttpHost proxy) {
        super(URL, token, proxy);
    }

    @Override
    public synchronized CkanOrganization createOrganization(CkanOrganization org) {
        return super.createOrganization(org);
    }

    @Override
    public synchronized CkanDataset createDataset(CkanDataset dataset) {
        return super.createDataset(dataset);
    }

    @Override
    public synchronized CkanResource createResource(CkanResource resource) {
        return super.createResource(resource);
    }

}
