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

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.POLITICHE_SVILUPPO_ORGANIZATION_NAME;
import java.util.UUID;
import junitparams.Parameters;
import org.junit.Test;

/**
 *  IMPORTANT: OVERRIDDEN METHODS MUST COPY ANNOTATIONS @Test AND
 * EVENTUAL JUnitParams TAGS
 * @since 0.4.1
*/
public class WriteCkanOrganizationIT extends WriteCkanGroupOrg<CkanOrganization> {

    public WriteCkanOrganizationIT() {
        super();
    }

    @Override
    protected CkanOrganization newRandom() {
        return new CkanOrganization("test-org-" + UUID.randomUUID().getMostSignificantBits());
    }

    @Override
    protected CkanOrganization createRandom() {
        CkanOrganization org = newRandom();
        return client.createOrganization(org);
    }

    @Override
    protected CkanOrganization create(CkanOrganization groupOrg) {
        return client.createOrganization(groupOrg);
    }

    @Override
    protected CkanOrganization newEmpty() {
        return new CkanOrganization();
    }

    @Override
    protected CkanOrganization newName(String name) {
        return new CkanOrganization(name);
    }

    @Override
    protected CkanOrganization getGroupOrg(CkanClient client, String nameOrId) {
        return client.getOrganization(nameOrId);
    }

    @Override
    protected String getExistingDatiTrentinoGroupOrgName() {
        return POLITICHE_SVILUPPO_ORGANIZATION_NAME;
    }

    @Override
    @Test
    public void testCreateWithDuplicateId() {
        super.testCreateWithDuplicateId(); 
    }

    @Override
    @Test
    public void testCreateWithDuplicateName() {
        super.testCreateWithDuplicateName(); 
    }

    @Override
    @Test
    @Parameters(method = "wrongGroupOrgNames")
    public void testCreateWithWrongName(String groupOrgName) {
        super.testCreateWithWrongName(groupOrgName); 
    }

    @Override
    @Test
    public void testCreateMirror() {
        super.testCreateMirror(); 
    }

    @Override
    @Test
    public void testCreateWithNonExistingPackages() {
        super.testCreateWithNonExistingPackages(); 
    }

    @Override
    @Test
    public void testCreateWithDatasetsWithoutId() {
        super.testCreateWithDatasetsWithoutId(); 
    }
    
   @Override
    @Test
    @Parameters(method = "wrongIds")
    public void testCreateWithWrongId(String id) {
        super.testCreateWithWrongId(id); 
    }    

    @Override
    @Test
    public void testCreateWithPackages() {
        super.testCreateWithPackages(); 
    }

    @Override
    @Test
    public void testCreateById() {
        super.testCreateById(); 
    }

    @Override
    @Test
    public void testCreateMinimal() {
        super.testCreateMinimal(); 
    }

    @Override
    @Test
    public void testCreateWithUser() {
        super.testCreateWithUser();
    }

    
    
    
}
