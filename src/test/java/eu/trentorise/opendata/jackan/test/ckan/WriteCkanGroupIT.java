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
package eu.trentorise.opendata.jackan.test.ckan;

import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanGroup;
import java.util.UUID;
import junitparams.Parameters;

public class WriteCkanGroupIT extends WriteCkanGroupOrg<CkanGroup> {

    public WriteCkanGroupIT() {
        super();
    }

    @Override
    protected CkanGroup newRandom() {
        return new CkanGroup("test-org-" + UUID.randomUUID().getMostSignificantBits());
    }

    @Override
    protected CkanGroup createRandom() {
        CkanGroup org = newRandom();
        return client.createGroup(org);
    }

    @Override
    protected CkanGroup create(CkanGroup groupOrg) {
        return client.createGroup(groupOrg);
    }

    @Override
    protected CkanGroup newEmpty() {
        return new CkanGroup();
    }

    @Override
    protected CkanGroup newName(String name) {
        return new CkanGroup(name);
    }

    @Override
    protected CkanGroup getGroupOrg(CkanClient client, String nameOrId) {
        return client.getGroup(nameOrId);
    }

    @Override
    protected String getExistingDatiTrentinoGroupOrgName() {
        return ReadCkanIT.AGRICOLTURA_GROUP_NAME;
    }

    @Override
    public void testCreateWithDuplicateId() {
        super.testCreateWithDuplicateId(); 
    }

    @Override
    public void testCreateWithDuplicateName() {
        super.testCreateWithDuplicateName(); 
    }

    @Override
    @Parameters(method = "wrongGroupOrgNames")
    public void testCreateWithWrongName(String groupOrgName) {
        super.testCreateWithWrongName(groupOrgName); 
    }

    @Override
    public void testCreateMirror() {
        super.testCreateMirror(); 
    }

    @Override
    public void testCreateWithNonExistingPackages() {
        super.testCreateWithNonExistingPackages(); 
    }

    @Override
    public void testCreateWithDatasetsWithoutId() {
        super.testCreateWithDatasetsWithoutId(); 
    }

    @Override
    public void testCreateWithPackages() {
        super.testCreateWithPackages(); 
    }

    @Override
    public void testCreateById() {
        super.testCreateById(); 
    }

    @Override
    public void testCreateMinimal() {
        super.testCreateMinimal(); 
    }
    
    
    
}
