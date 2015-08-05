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
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanDatasetBase;
import eu.trentorise.opendata.jackan.ckan.CkanGroup;
import eu.trentorise.opendata.jackan.ckan.CkanPair;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.ckan.CkanResourceBase;
import eu.trentorise.opendata.jackan.ckan.CkanState;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.PRODOTTI_CERTIFICATI_DATASET_NAME;
import static eu.trentorise.opendata.jackan.test.ckan.WriteCkanTest.JACKAN_URL;
import java.util.ArrayList;
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
public class WriteCkanDatasetIT extends WriteCkanTest {

    private static final Logger LOG = Logger.getLogger(WriteCkanDatasetIT.class.getName());

    @Test
    public void testCreateMinimal() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());

        CkanDataset retDataset = client.createDataset(dataset);

        checkNotEmpty(retDataset.getId(), "Invalid dataset id!");
        assertEquals(dataset.getName(), retDataset.getName());
        LOG.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    /**
     * Checks it is possible to create a dataset as 'deleted'
     */
    @Test
    public void testCreateAsDeleted() {
        CkanDatasetBase dataset = new CkanDatasetBase(UUID.randomUUID().toString());
        dataset.setState(CkanState.deleted);
        CkanDataset retDataset = client.createDataset(dataset);
        assertEquals(CkanState.deleted, retDataset.getState());
    }

    @Test
    public void testCreate() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        ArrayList<CkanPair> extras = Lists.newArrayList(new CkanPair("v", "w"));
        dataset.setExtras(extras);
        dataset.putOthers("x", "y");
        dataset.setLicenseTitle("abc");
        CkanResource resource = new CkanResource(JACKAN_URL, null);
        resource.setId(UUID.randomUUID().toString()); // otherwise CKAN won't create one for us (sic)
        dataset.setResources(Lists.newArrayList(resource));

        CkanDataset retDataset = client.createDataset(dataset);

        checkNotEmpty(retDataset.getId(), "Invalid dataset id!");
        assertEquals(dataset.getName(), retDataset.getName());
        LOG.info("todo Don't know how to test writing 'others', currently in dati.trentino instance just doesn't write them.");
        // assertEquals(dataset.getOthers(), retDataset.getOthers()); 
        assertEquals(dataset.getExtras(), retDataset.getExtras());
        assertEquals(null, retDataset.getLicenseTitle()); // should not have been sent, thus shouldn't return.
        assertEquals(1, retDataset.getResources().size());
        assertEquals(JACKAN_URL, retDataset.getResources().get(0).getUrl());
        assertEquals(resource.getId(), retDataset.getResources().get(0).getId());

        LOG.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    @Test
    @Parameters(method = "wrongDatasetNames")
    public void testCreateWrongName(String datasetName) {

        try {
            CkanDataset dataset = new CkanDataset(datasetName);
            client.createDataset(dataset);
            Assert.fail();
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateDuplicateByName() {

        String datasetName = "test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits();

        CkanDataset dataset = new CkanDataset(datasetName);
        client.createDataset(dataset);
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with same name " + datasetName);
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithId() {

        CkanDataset dataset = new CkanDataset();
        dataset.setId(UUID.randomUUID().toString());
        dataset.setName(dataset.getId());
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with id!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateNonExistentOrganization() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        dataset.setOwnerOrg(UUID.randomUUID().toString());
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with inexistent owner org!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateNonExistentGroup() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        CkanGroup group = new CkanGroup();
        group.setId(UUID.randomUUID().toString());
        dataset.setGroups(Lists.newArrayList(group));
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with inexistent group ");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWrongResourceNoUrl() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        CkanResource resource = new CkanResource(); // missing url 
        resource.setId(UUID.randomUUID().toString());
        dataset.setResources(Lists.newArrayList(resource));
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with resource without url");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateResourceWithoutId() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        CkanResource resource = new CkanResource(JACKAN_URL, null);
        dataset.setResources(Lists.newArrayList(resource));
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with resource without id!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateNonExistentLicenseId() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        dataset.setLicenseId("666");
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with inexistent license id! ");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateMirror() {

        CkanDataset dataset = datiTrentinoClient.getDataset(PRODOTTI_CERTIFICATI_DATASET_NAME);

        dataset.setExtras(new ArrayList()); // dati.trentino has custom schemas and merges metadata among regular fields

        dataset.setId(null);
        dataset.setName("prodotti-certificati-" + UUID.randomUUID().getMostSignificantBits());
        //dataset.setOrganization(null);
        dataset.setOwnerOrg(null);
        dataset.setGroups(null);

        CkanDataset retDataset = client.createDataset(dataset);

        checkNotEmpty(retDataset.getId(), "Invalid dataset id!");

        LOG.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    @Test
    public void testUpdate() {
        CkanDataset dataset = new CkanDataset(UUID.randomUUID().toString());

        dataset.setAuthor("ugo");
        dataset.setLicenseTitle("bla"); // not in CkanResourceBase, shouldn't be sent in create post
        dataset.putOthers("x", "y");
        dataset.setExtras(Lists.newArrayList(new CkanPair("v", "w")));

        CkanDataset retDataset1 = client.createDataset(dataset);

        retDataset1.setNotes("abc");
        retDataset1.setAuthor(null); // so we won't send it in the post
        retDataset1.setExtras(null); // so we test if extras are still preserved in our update
        retDataset1.setOthers(null); // so we test if others are still preserved in our update. 

        CkanDataset retDataset2 = client.updateDataset(retDataset1);

        assertEquals(retDataset1.getId(), retDataset2.getId());
        assertEquals(retDataset1.getName(), retDataset2.getName());
        assertEquals("abc", retDataset2.getNotes());
        assertEquals("ugo", retDataset2.getAuthor());
        assertEquals(dataset.getExtras(), retDataset2.getExtras());
        LOG.info("todo don't know how to test 'getOthers', see testDataCreate()");
        // assertEquals(dataset.getOthers(), retDataset2.getOthers()); 
        assertEquals(null, retDataset2.getLicenseTitle());  // licenseTitle is not among api docs for creation, so hopefully was jsonignored when sending retDataset1  

    }

    @Test
    public void testUpdateResources() {
        CkanDataset dataset = new CkanDataset(UUID.randomUUID().toString());

        dataset.setNotes("abc");

        CkanResource resource = new CkanResource(JACKAN_URL, null);
        resource.setId(UUID.randomUUID().toString()); // otherwise CKAN won't create one for us (sic)
        dataset.setResources(Lists.newArrayList(resource));
        CkanDataset retDataset1 = client.createDataset(dataset);

        retDataset1.setNotes("cba");
        retDataset1.setResources(null); // so we won't send resources

        CkanDataset retDataset2 = client.updateDataset(retDataset1);
        assertEquals(1, retDataset2.getResources().size());
        assertEquals(resource.getId(), retDataset2.getResources().get(0).getId());
    }

    /**
     * Checks it is possible to mark a dataset as deleted by update
     */
    @Test
    public void testUpdateAsDeleted() {
        CkanDataset dataset = createRandomDataset();
        dataset.setState(CkanState.deleted);
        CkanDataset retDataset = client.updateDataset(dataset);        
        assertEquals(CkanState.deleted, retDataset.getState());

        client.getDataset(dataset.getId());
        assertEquals(CkanState.deleted, retDataset.getState());

    }
    
    /**
     * Shows datasets are just marked as 'deleted', but still accessible from
     * webapi. Also resources within will still be active.
     */
    @Test
    public void testDeleteById() {
        CkanDataset dataset = createRandomDataset(1);        
        client.deleteDataset(dataset.getId());

        CkanDataset retDataset = client.getDataset(dataset.getId());
        assertEquals(CkanState.deleted, retDataset.getState());
        assertEquals(CkanState.active, retDataset.getResources().get(0).getState());
    }
    
    /**
     * Shows datasets are just marked as 'deleted', but still accessible from
     * webapi. Also resources within will still be active.
     */
    @Test
    public void testDeleteByName() {
        CkanDataset dataset = createRandomDataset(1);        
        client.deleteDataset(dataset.getName());

        CkanDataset retDataset = client.getDataset(dataset.getId());
        assertEquals(CkanState.deleted, retDataset.getState());
        assertEquals(CkanState.active, retDataset.getResources().get(0).getState());
    }
    
    /**
     * Shows resources are just marked as 'deleted', but still accessible from
     * webapi.
     */
    @Test
    public void testDeleteNonExisting() {
        String resourceId = UUID.randomUUID().toString();
        try {
            client.deleteResource(resourceId);
            Assert.fail("Shouldn't be possible to delete non existing resource!");
        } catch(JackanException ex){
            
        }

    }    

}
