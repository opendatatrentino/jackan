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

/**
 * Class to explicitly model a Ckan organization, which is <i> not </i> a group,
 * although it has the same attributes.
 *
 * {@link CkanOrganizationBase} holds fields that can be sent when
 * <a href="http://docs.ckan.org/en/latest/api/index.html?#ckan.logic.action.create.organization_create" target="_blank">creating
 * a group</a>, while {@link CkanOrganization} holds more fields that can be
 * returned with searches.
 *
 * This class initializes nothing to fully preserve all we get from ckan. In
 * practice, all fields of retrieved resources can be null except maybe
 * {@code name}.
 *
 * @author David Leoni
 */
public class CkanOrganizationBase extends CkanGroupOrgBase {

    public CkanOrganizationBase() {
        super();
        setOrganization(true);
    }

    /**
     * Constructor with minimal amount of parameters needed to successfully
     * create an instance on the server.
     *
     * @param name Name in the url, lowercased and without spaces. i.e.
     * management-of-territory
     */
    public CkanOrganizationBase(String name) {
        super(name);
        setOrganization(true);
    }
}
