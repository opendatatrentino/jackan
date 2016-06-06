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
import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.CkanQuery;
import eu.trentorise.opendata.jackan.SearchResults;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.model.*;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.PRODOTTI_CERTIFICATI_DATASET_NAME;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author David Leoni
 * @since 0.4.1
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

    @Test
    @SuppressWarnings("UnusedAssignment")
    public void createExample() {
        // here we use CheckedCkanClient for extra safety
        CkanClient myClient = new CheckedCkanClient("http://put-your-catalog.org", "put your ckan api key token");
        myClient = client; // little trick so test is going to run for real...

        CkanDatasetBase dataset = new CkanDatasetBase();
        dataset.setName("my-cool-dataset-" + new Random().nextLong());
        // notice Jackan will only send field 'name' as it is non-null
        CkanDataset createdDataset = myClient.createDataset(dataset);

        checkNotEmpty(createdDataset.getId(), "Invalid dataset id!");
        assertEquals(dataset.getName(), createdDataset.getName());
        System.out.println("Dataset is available online at " + CkanClient.makeDatasetUrl(myClient.getCatalogUrl(), dataset.getName()));
    }

    /**
     * Shows Jackan-specific patch-update functionality to change tags assigned to a dataset
     * (and also that new free tags can be created at dataset creation)
     */
    @Test
    @SuppressWarnings("UnusedAssignment")
    public void patchUpdateExample() {

        // here we use CheckedCkanClient for extra safety
        CkanClient myClient = new CheckedCkanClient("http://put-your-catalog.org", "put your ckan api key token");
        myClient = client; // little trick so test is going to run for real...
        
        CkanDatasetBase dataset = new CkanDatasetBase("my-dataset-" + new Random().nextLong());

        // we create a dataset with one tag 'cool'
        List<CkanTag> tags_1 = new ArrayList<>();
        tags_1.add(new CkanTag("cool"));
        dataset.setTags(tags_1);
        CkanDataset createdDataset = myClient.createDataset(dataset);

        // now we assign a new array with one tag ["amazing"] 
        List<CkanTag> tags_2 = new ArrayList<>();
        tags_2.add(new CkanTag("amazing"));
        createdDataset.setTags(tags_2);

        // let's patch-update, jackan will take care of merging tags to prevent erasure of 'cool'
        CkanDataset updatedDataset = myClient.patchUpdateDataset(createdDataset);

        assert 2 == updatedDataset.getTags().size(); //  'amazing' has been added to ['cool']
        System.out.println("Merged tags = "
                + updatedDataset.getTags().get(0).getName()
                + ", " + updatedDataset.getTags().get(1).getName());

        System.out.println("Updated dataset is available online at " + CkanClient.makeDatasetUrl(myClient.getCatalogUrl(), dataset.getName()));
    }

    @Test
    public void testCreate() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        ArrayList<CkanPair> extras = Lists.newArrayList(new CkanPair("v", "w"));
        dataset.setExtras(extras);
        dataset.putOthers("x", "y");
        dataset.setLicenseTitle("abc");
        CkanResource resource = new CkanResource(JACKAN_URL, null);
        resource.setId(randomUUID()); // otherwise CKAN won't create one for us (sic)
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

    /**
     * Checks it is possible to create a dataset as 'deleted'
     */
    @Test
    public void testCreateAsDeleted() {
        CkanDatasetBase dataset = new CkanDatasetBase(randomUUID());
        dataset.setState(CkanState.deleted);
        CkanDataset retDataset = client.createDataset(dataset);
        assertEquals(CkanState.deleted, retDataset.getState());
    }

    @Test
    public void testCreateWithOrganization() {
        CkanOrganization org = createRandomOrganization();
        CkanDatasetBase dataset = new CkanDatasetBase("test-dataset-" + randomUUID());
        dataset.setOwnerOrg(org.getId());
        CkanDataset retDataset = client.createDataset(dataset);
        assertEquals(org.getId(), retDataset.getOwnerOrg());
        SearchResults<CkanDataset> sr = client.searchDatasets(CkanQuery.filter().byOrganizationName(org.getName()), 100, 0);
        assertEquals(1, sr.getCount());
        assertEquals(1, sr.getResults().size());
        assertEquals(retDataset.getId(), sr.getResults().get(0).getId());
    }

    /**
     * Shows it is not necessary t have an existing tag to create the dataset,
     * which MAY make sense.
     */
    @Test
    public void testCreateWithNonExistingTag() {
        CkanDatasetBase dataset = new CkanDatasetBase("test-dataset-" + randomUUID());
        CkanTag tag = new CkanTag();
        tag.setName("test-tag-" + randomUUID());
        dataset.setTags(Lists.newArrayList(tag));
        CkanDataset retDataset = client.createDataset(dataset);
        assertEquals(tag.getName(), retDataset.getTags().get(0).getName());
        SearchResults<CkanDataset> sr = client.searchDatasets(CkanQuery.filter().byTagNames(tag.getName()), 100, 0);
        assertEquals(1, sr.getCount());
        assertEquals(1, sr.getResults().size());
        assertEquals(retDataset.getId(), sr.getResults().get(0).getId());
    }

    @Test
    public void testCreateWithExistingTag() {
        CkanDatasetBase dataset = new CkanDatasetBase("test-dataset-" + randomUUID());
        CkanTag tag = createRandomTag();
        dataset.setTags(Lists.newArrayList(tag));
        CkanDataset retDataset = client.createDataset(dataset);
        assertEquals(tag.getName(), retDataset.getTags().get(0).getName());
        SearchResults<CkanDataset> sr = client.searchDatasets(CkanQuery.filter().byTagNames(tag.getName()), 100, 0);
        /* todo don't know why but it finds nothing...
         assertEquals(1, sr.getCount());
         assertEquals(1, sr.getResults().size());
         assertEquals(retDataset.getId(), sr.getResults().get(0).getId());
         */
    }

    @Test
    public void testCreateWithGroup() {
        CkanGroup group = createRandomGroup();
        CkanDatasetBase dataset = new CkanDatasetBase("test-dataset-" + randomUUID());
        dataset.setGroups(Lists.newArrayList(group));
        CkanDataset retDataset = client.createDataset(dataset);
        assertEquals(group.getId(), retDataset.getGroups().get(0).getId());
        SearchResults<CkanDataset> sr = client.searchDatasets(CkanQuery.filter().byGroupNames(group.getName()), 100, 0);
        assertEquals(1, sr.getCount());
        assertEquals(1, sr.getResults().size());
        assertEquals(retDataset.getId(), sr.getResults().get(0).getId());
    }

    @Test
    @Parameters(method = "wrongDatasetNames")
    public void testCreateWithWrongName(String datasetName) {

        try {
            CkanDataset dataset = new CkanDataset(datasetName);
            client.createDataset(dataset);
            Assert.fail();
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithDuplicateName() {

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
        dataset.setId(randomUUID());
        dataset.setName("test-dataset-" + dataset.getId());
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
        dataset.setOwnerOrg(randomUUID());
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with non existent owner org!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithNonExistentGroup() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        CkanGroup group = new CkanGroup();
        group.setId(randomUUID());
        dataset.setGroups(Lists.newArrayList(group));
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with non existent group ");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithWrongResourceNoUrl() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        CkanResource resource = new CkanResource(); // missing url 
        resource.setId(randomUUID());
        dataset.setResources(Lists.newArrayList(resource));
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with resource without url");
        }
        catch (JackanException ex) {

        }
    }

    /**
     * Missing id should be automatically assigned.
     */
    @Test
    public void testCreateWithResourceWithoutId() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        CkanResource resource = new CkanResource(JACKAN_URL, null);
        dataset.setResources(Lists.newArrayList(resource));

        CkanDataset retDataset = client.createDataset(dataset);
        String id = retDataset.getResources().get(0).getId();
        UUID.fromString(id);
    }

    @Test
    public void testCreateWithNonExistentLicenseId() {

        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        dataset.setLicenseId(randomUUID());
        try {
            client.createDataset(dataset);
            Assert.fail("Shouldn't be able to create dataset with non existent license id!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateMirror() {

        CkanDataset dataset = datiTrentinoClient.getDataset(PRODOTTI_CERTIFICATI_DATASET_NAME);

        dataset.setExtras(new ArrayList<CkanPair>()); // dati.trentino has custom schemas and merges metadata among regular fields

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
    public void testPatchUpdateDataset() {
        CkanDataset dataset = new CkanDataset(randomUUID());

        dataset.setAuthor("ugo");
        dataset.setLicenseTitle("bla"); // not in CkanResourceBase, shouldn't be sent in create post
        dataset.putOthers("x", "y");
        dataset.setExtras(Lists.newArrayList(new CkanPair("v", "w")));

        CkanDataset retDataset1 = client.createDataset(dataset);

        retDataset1.setNotes("abc");
        retDataset1.setAuthor(null); // so we won't send it in the post
        // so we test if lists are still preserved in our update
        retDataset1.setExtras(null);
        retDataset1.setOthers(null);

        CkanDataset retDataset2 = client.patchUpdateDataset(retDataset1);

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
    public void testPatchUpdateDatasetWithName() {
        CkanDataset dataset1 = createRandomDataset();
        CkanDataset dataset2 = new CkanDataset(dataset1.getName());
        dataset2.setNotes("abc");
        CkanDataset retDataset = client.patchUpdateDataset(dataset2);
        assertEquals("abc", retDataset.getNotes());
    }

    /**
     * Shows that if we set resources to null on update they don't get destroyed
     * on the server.
     */
    @Test
    public void testPatchUpdateDatasetResources() {
        CkanDataset dataset = new CkanDataset(randomUUID());

        dataset.setNotes("abc");

        CkanResource resource = new CkanResource(JACKAN_URL, null);
        resource.setId(randomUUID()); // otherwise CKAN won't create one for us (sic)
        dataset.setResources(Lists.newArrayList(resource));
        CkanDataset retDataset1 = client.createDataset(dataset);

        retDataset1.setNotes("cba");
        retDataset1.setResources(null); // so we won't send resources

        CkanDataset retDataset2 = client.patchUpdateDataset(retDataset1);
        assertEquals(1, retDataset2.getResources().size());
        assertEquals(resource.getId(), retDataset2.getResources().get(0).getId());
    }

    @Test
    public void testPatchUpdateDatasetWithWrongId() {

        CkanDataset dataset = new CkanDataset();
        dataset.setId("test-dataset-" + randomUUID());

        try {
            client.patchUpdateDataset(dataset);
            Assert.fail("Shouldn't be able to update non-existing dataset!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    @Parameters(method = "wrongDatasetNames")
    public void testPatchUpdateDatasetWithWrongName(String datasetName) {
        if (datasetName != null) {
            CkanDataset dataset = createRandomDataset();
            dataset.setName(datasetName);
            try {
                client.patchUpdateDataset(dataset);
                Assert.fail("Shouldn't be possible to patch update dataset with wrong name: " + datasetName);

            }
            catch (JackanException ex) {

            }
        }

    }

    @Test
    public void testPatchUpdateDatasetWithDuplicateName() {

        CkanDataset dataset1 = createRandomDataset();
        CkanDataset dataset2 = createRandomDataset();

        dataset2.setName(dataset1.getName());
        try {
            client.patchUpdateDataset(dataset2);
            Assert.fail("Shouldn't be able to update dataset with same name " + dataset1.getName() + " of dataset with id " + dataset1.getId());
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testPatchUpdateDatasetWithNonExistentOrganization() {

        CkanDataset dataset = createRandomDataset();

        dataset.setOwnerOrg(randomUUID());
        try {
            client.patchUpdateDataset(dataset);
            Assert.fail("Shouldn't be able to update dataset with non existent owner org!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testPatchUpdateDatasetWithResource() {

        CkanDataset dataset = new CkanDataset("test-dataset-" + randomUUID());
        CkanResource resource1 = new CkanResource(JACKAN_URL, null);
        dataset.setResources(Lists.newArrayList(resource1));
        client.createDataset(dataset);

        CkanResource resource2 = new CkanResource(JACKAN_URL, null);
        dataset.setResources(Lists.newArrayList(resource2));
        CkanDataset retDataset1 = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset1.getResources().size());

        dataset.setResources(Lists.newArrayList(retDataset1.getResources().get(0)));
        CkanDataset retDataset2 = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset2.getResources().size());

    }

    @Test
    public void testPatchUpdateDatasetWithOrganization() {

        CkanDataset dataset = new CkanDataset("test-dataset-" + randomUUID());
        CkanOrganization org1 = createRandomOrganization();
        dataset.setOwnerOrg(org1.getId());
        client.createDataset(dataset);

        CkanOrganization org2 = createRandomOrganization();
        dataset.setOwnerOrg(org2.getId());
        CkanDataset retDataset = client.patchUpdateDataset(dataset);
        assertEquals(org2.getId(), retDataset.getOwnerOrg());
    }

    @Test
    public void testPatchUpdateDatasetWithGroup() {

        CkanDataset dataset = new CkanDataset("test-dataset-" + randomUUID());
        CkanGroup group1 = createRandomGroup();
        dataset.setGroups(Lists.newArrayList(group1));
        client.createDataset(dataset);

        CkanGroup group2 = createRandomGroup();
        dataset.setGroups(Lists.newArrayList(group2));
        CkanDataset retDataset1 = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset1.getGroups().size());

        CkanDataset retDataset2 = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset2.getGroups().size());
    }

    @Test
    public void testPatchUpdateDatasetWithTag() {

        CkanDataset dataset = new CkanDataset("test-dataset-" + randomUUID());
        CkanTag tag1 = createRandomTag();
        dataset.setTags(Lists.newArrayList(tag1));
        client.createDataset(dataset);

        CkanTag tag2 = createRandomTag();
        dataset.setTags(Lists.newArrayList(tag2));
        CkanDataset retDataset1 = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset1.getTags().size());

        CkanDataset retDataset2 = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset2.getTags().size());
    }

    @Test
    public void testPatchUpdateDatasetWithExtra() {

        CkanDataset dataset = new CkanDataset("test-dataset-" + randomUUID());
        CkanPair extra1 = new CkanPair("x", "y");
        dataset.setExtras(Lists.newArrayList(extra1));
        client.createDataset(dataset);

        CkanPair extra2 = new CkanPair("v", "w");
        dataset.setExtras(Lists.newArrayList(extra2));
        CkanDataset retDataset1 = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset1.getExtras().size());

        CkanDataset retDataset2 = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset2.getExtras().size());
    }

    @Test
    @Ignore
    public void testPatchUpdateDatasetWithRelationshipAsObject() {
        throw new UnsupportedOperationException("todo implement me");
    }

    @Test
    @Ignore
    public void testPatchUpdateDatasetWithRelationshipAsSubject() {
        throw new UnsupportedOperationException("todo implement me");
    }

    @Test
    public void testPatchUpdateDatasetWithNonExistentTag() {

        CkanDataset dataset = new CkanDataset("test-dataset-" + randomUUID());
        CkanTag tag1 = createRandomTag();
        dataset.setTags(Lists.newArrayList(tag1));
        client.createDataset(dataset);

        dataset.setTags(Lists.newArrayList(new CkanTag("test-tag-" + randomUUID())));

        CkanDataset retDataset = client.patchUpdateDataset(dataset);
        assertEquals(2, retDataset.getTags().size());
    }

    @Test
    public void testPatchUpdateDatasetWithNonExistentGroup() {

        CkanDataset dataset = createRandomDataset();
        CkanGroup group = new CkanGroup();
        group.setId(randomUUID());
        dataset.setGroups(Lists.newArrayList(group));
        try {
            client.patchUpdateDataset(dataset);
            Assert.fail("Shouldn't be able to update dataset with non-existent group ");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testPatchUpdateDatasetWithWrongResourceWithoutUrl() {

        CkanDataset dataset = createRandomDataset();

        CkanResource resource = new CkanResource(); // missing url 
        resource.setId(randomUUID());
        dataset.setResources(Lists.newArrayList(resource));
        try {
            client.patchUpdateDataset(dataset);
            Assert.fail("Shouldn't be able to update dataset with resource without url");
        }
        catch (JackanException ex) {
            ex.getMessage();
        }
    }

    @Test
    public void testPatchUpdateDatasetWithNonExistentLicenseId() {

        CkanDataset dataset = createRandomDataset();
        dataset.setLicenseId(randomUUID());
        try {
            client.patchUpdateDataset(dataset);
            Assert.fail("Shouldn't be able to update dataset with non existent license id! ");
        }
        catch (JackanException ex) {

        }
    }

    /**
     * Shows it is possible to mark a dataset as deleted by update
     */
    @Test
    public void testPatchUpdateDatasetAsDeleted() {
        CkanDataset dataset = createRandomDataset();
        dataset.setState(CkanState.deleted);
        CkanDataset retDataset = client.patchUpdateDataset(dataset);
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
        String resourceId = randomUUID();
        try {
            client.deleteResource(resourceId);
            Assert.fail("Shouldn't be possible to delete non existing resource!");
        }
        catch (JackanException ex) {

        }

    }

}
