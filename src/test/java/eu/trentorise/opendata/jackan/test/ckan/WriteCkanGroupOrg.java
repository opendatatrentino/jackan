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

import com.google.common.collect.Lists;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanGroupOrg;
import eu.trentorise.opendata.jackan.ckan.CkanUser;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import junitparams.Parameters;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */

/**
 * Abstract class with common tests for both CkanOrganization and CkanGroup
 * @author David Leoni
 */
abstract class WriteCkanGroupOrg<T extends CkanGroupOrg> extends WriteCkanTest {
    private static final Logger LOG = Logger.getLogger(WriteCkanGroupOrg.class.getName());

    public WriteCkanGroupOrg() {
        super();
    }
    
    
    
    protected abstract T newRandom();
    
    protected abstract T createRandom();
    
    protected abstract T create(T groupOrg);
    
    protected abstract T newEmpty();
    
    protected abstract T newName(String name);

    protected abstract T getGroupOrg(CkanClient client, String nameOrId);

    protected abstract String getExistingDatiTrentinoGroupOrgName();
    
    protected String className(){
        return newEmpty().getClass().getSimpleName();
    }
    
    @Test
    public void testCreateMinimal() {
        T groupOrg = newRandom();

        T retGroupOrg = create(groupOrg);

        assertEquals(groupOrg.getName(), retGroupOrg.getName());
        LOG.log(Level.INFO, "created "+ className() + " with id {0} in catalog {1}", new Object[]{retGroupOrg.getId(), JackanTestConfig.of().getOutputCkan()});
    }
    
    @Test
    public void testCreateById() {
        T groupOrg = newEmpty();
        groupOrg.setId(UUID.randomUUID().toString());
        groupOrg.setName("test-org-"+groupOrg.getId());
        T retGroupOrg = create(groupOrg);
        assertEquals(groupOrg.getId(), retGroupOrg.getId());
        assertEquals(groupOrg.getName(), retGroupOrg.getName());
    }    
    
    @Test
    public void testCreateWithPackages(){
        T groupOrg = newName("test-org-" + UUID.randomUUID().getMostSignificantBits());
        CkanDataset dataset = createRandomDataset();        
        groupOrg.setPackages(Lists.newArrayList(dataset));
        T retGroupOrg = create(groupOrg);
        CkanDataset retDataset = client.getDataset(dataset.getId());        
    }    
    
    @Test
    public void testCreateWithDatasetsWithoutId(){
        T org = newName("test-org-" + UUID.randomUUID().getMostSignificantBits());
        CkanDataset dataset = new CkanDataset("test-dataset-"+UUID.randomUUID().toString());        
        org.setPackages(Lists.newArrayList(dataset));
        try {
            T retGroupOrg = create(org);
            Assert.fail("Shouldn't be possible to create an "+ className() + " with datasets withour ids!");
        } catch (JackanException ex){
            
        }        
    }  
    
@Test
    public void testCreateWithUser(){
        T groupOrg = newName("test-org-" + UUID.randomUUID().getMostSignificantBits());
        
        CkanUser user = createRandomUser();
        
        groupOrg.setUsers(Lists.newArrayList(user));
        
        T retGroupOrg = create(groupOrg);
        
        assertEquals(2, retGroupOrg.getUsers().size());
        boolean found = false;
        for (CkanUser u : retGroupOrg.getUsers()){
            if (user.getId().equals(u.getId())){
                found = true;
            }
        }
        if (!found){
            Assert.fail();
        }
        
    }      
    
    
    @Test
    public void testCreateWithNonExistingPackages(){
        T groupOrg = newName("test-org-" + UUID.randomUUID().getMostSignificantBits());
        CkanDataset dataset = new CkanDataset("test-dataset-"+UUID.randomUUID().toString());
        dataset.setId(UUID.randomUUID().toString());
        groupOrg.setPackages(Lists.newArrayList(dataset));
        try {
            T retGroupOrg = create(groupOrg);
            Assert.fail("Shouldn't be possible to create an "+ className() + " with nonexisting datasets!");
        } catch (JackanException ex){
            
        }        
    }        
    
    @Test
    public void testCreateMirror() {

        T groupOrg = getGroupOrg(datiTrentinoClient, getExistingDatiTrentinoGroupOrgName());

        groupOrg.setName(getExistingDatiTrentinoGroupOrgName() + "-" + UUID.randomUUID().getMostSignificantBits());
        groupOrg.setId(null);

        T retGroupOrg = create(groupOrg);

        checkNotEmpty(retGroupOrg.getId(), "Invalid "+ className() + " id!");

        LOG.log(Level.INFO, "created "+ className() + " with id {0} in catalog {1}", new Object[]{retGroupOrg.getId(), JackanTestConfig.of().getOutputCkan()});
    }
    


    @Test
    @Parameters(method = "wrongGroupOrgNames")
    public void testCreateWithWrongName(String groupOrgName) {        

        try {
            T groupOrg = newName(groupOrgName);
            create(groupOrg);
            Assert.fail("Shouldn't be able to create "+ className() + " with wrong name " + groupOrgName);
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithDuplicateName() {

        String name = "test-grouporg-jackan-" + UUID.randomUUID().getMostSignificantBits();

        T groupOrg = newName(name);
        create(groupOrg);
        try {
            create(groupOrg);
            Assert.fail("Shouldn't be able to create "+ className() + " with same name " + name);
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithDuplicateId() {

        T groupOrg = newEmpty();
        groupOrg.setId(UUID.randomUUID().toString());
        groupOrg.setName("test-grouporg-" + groupOrg.getId());
        create(groupOrg);
        try {
            create(groupOrg);
            Assert.fail("Shouldn't be able to create "+ className() + " with same id " + groupOrg.getId());
        }
        catch (JackanException ex) {

        }
    }


}
