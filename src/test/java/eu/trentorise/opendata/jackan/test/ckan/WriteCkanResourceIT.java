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

import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanResourceBase;
import eu.trentorise.opendata.jackan.model.CkanState;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.PRODOTTI_CERTIFICATI_RESOURCE_ID;
import static eu.trentorise.opendata.jackan.test.ckan.WriteCkanTest.JACKAN_URL;
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
public class WriteCkanResourceIT extends WriteCkanTest {

    private static final Logger LOG = Logger.getLogger(WriteCkanResourceIT.class.getName());

    @Test
    public void testCreateMinimal() {

        CkanDataset dataset = createRandomDataset();

        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());

        CkanResource retRes = client.createResource(resource);

        checkNotEmpty(retRes.getId(), "Invalid created resource id!");
        assertEquals(resource.getUrl(), retRes.getUrl());
        assertEquals(null, retRes.getPackageId()); // because this won't be present in the result

        LOG.log(Level.INFO, "Created resource with id {0} in catalog {1}", new Object[]{retRes.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    @Test
    public void testCreate() {

        CkanDataset dataset = createRandomDataset();

        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());

        resource.putOthers("x", "y");

        CkanResource retRes = client.createResource(resource);

        checkNotEmpty(retRes.getId(), "Invalid created resource id!");
        assertEquals(resource.getUrl(), retRes.getUrl());
        assertEquals(null, retRes.getPackageId()); // because this won't be present in the result
        assertEquals(resource.getOthers(), retRes.getOthers());

        LOG.log(Level.INFO, "Created resource with id {0} in catalog {1}", new Object[]{retRes.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    /**
     * Shows it's possible to force an id in a resource.
     */
    @Test
    public void testCreateWithId() {

        CkanDataset dataset = createRandomDataset();
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        resource.setId(UUID.randomUUID().toString());

        CkanResource retRes = client.createResource(resource);
        assertEquals(resource.getId(), retRes.getId());

    }

    @Test
    @Parameters(method = "wrongIds")
    public void testCreateWithWrongId(String wrongId) {

        CkanDataset dataset = createRandomDataset();
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        resource.setId(wrongId);
        try {
            CkanResource retRes = client.createResource(resource);
            Assert.fail("Shouldn't be able to create resource with ill formatted UUID '" + resource.getId() + "'");
        }
        catch (JackanException ex) {

        }

    }

    @Test
    public void testCreateDuplicateId() {

        CkanDataset dataset = createRandomDataset();

        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        resource.setId(UUID.randomUUID().toString());
        CkanResource retRes1 = client.createResource(resource);
        try {
            CkanResource retRes2 = client.createResource(resource);
            LOG.log(Level.FINE, "id 1: {0}", retRes1.getId());
            LOG.log(Level.FINE, "id 2: {0}", retRes2.getId());
            Assert.fail("Shouldn't be able to create resource with duplicate id!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    @Parameters(method = "wrongUrls")
    public void testCreateWithWrongUrl(String url) {

        CkanDataset dataset = createRandomDataset();

        try {
            CkanResourceBase resource = new CkanResourceBase(url, dataset.getId());
            client.createResource(resource);
            Assert.fail("Shouldn't be able to create resource with wrong url: " + url);
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithWrongDatasetId() {

        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, UUID.randomUUID().toString());
        try {
            client.createResource(resource);
            Assert.fail();
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateMirror() {

        CkanDataset dataset = createRandomDataset();

        CkanResource resource = datiTrentinoClient.getResource(PRODOTTI_CERTIFICATI_RESOURCE_ID);

        resource.setPackageId(dataset.getId());

        resource.setId(randomUUID());

        CkanResource retResource = client.createResource(resource);

        checkNotEmpty(retResource.getId(), "Invalid resource id!");

        LOG.log(Level.INFO, "created resource with id {0} in catalog {1}", new Object[]{retResource.getId(), JackanTestConfig.of().getOutputCkan()});

    }

    @Test
    public void testPatchUpdate() {
        CkanDataset dataset = createRandomDataset();
        CkanResource resource = new CkanResource(JACKAN_URL, dataset.getId());
        resource.setSize("123");
        resource.setOwner("acme");  // owner is not in CkanResourceBase, shouldn't be sent in update post
        resource.putOthers("x", "y");

        CkanResource retRes1 = client.createResource(resource);

        retRes1.setDescription("abc");

        retRes1.setSize(null); // so we won't send it in the post
        retRes1.setOthers(null); // so we won't send it in the post and hopefully trigger client automerge feature

        CkanResource retRes2 = client.patchUpdateResource(retRes1);

        assertEquals(retRes1.getId(), retRes2.getId());
        assertEquals(retRes1.getUrl(), retRes2.getUrl());
        assertEquals("abc", retRes2.getDescription());
        assertEquals("123", retRes2.getSize());
        assertEquals(resource.getOthers(), retRes2.getOthers());
        assertEquals(null, retRes2.getOwner());  // owner is not among api docs for creation, so hopefully was jsonignored when sending retRes1            
    }

    @Test
    public void testPatchUpdateNonExistingResource() {

        CkanDataset dataset = createRandomDataset();

        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        resource.setId(randomUUID());
        try {
            client.patchUpdateResource(resource);
            Assert.fail("Shouldn't be able to patch update non existing resource!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    @Parameters(method = "wrongUrls")
    public void testPatchUpdatWithWrongUrl(String url) {

        CkanResource resource = createRandomResource();
        resource.setUrl(url);

        try {
            client.patchUpdateResource(resource);
            Assert.fail("Shouldn't be able to patch update resource with wrong url: " + url);
        }
        catch (JackanException ex) {

        }
    }

    /**
     * This shows it is not possible to move a resource from one dataset to
     * another by updating the resource packageId.
     */
    @Test
    public void testPatchUpdateWithDifferentDatasetId() {

        CkanDataset dataset1 = createRandomDataset();
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset1.getId());
        CkanResource retResource1 = client.createResource(resource);
        CkanDataset dataset2 = createRandomDataset();
        resource.setPackageId(dataset2.getId());
        CkanResource retResource2 = client.patchUpdateResource(retResource1);
        assertEquals(null, retResource2.getPackageId());

        boolean found = false;
        CkanDataset newDataset1 = client.getDataset(dataset1.getId());
        for (CkanResource cr : newDataset1.getResources()) {
            if (retResource1.getId().equals(cr.getId())) {
                found = true;
            }
        }
        if (!found) {
            Assert.fail("In theory it sbouldn't be possible to 'move' a resource from one dataset to another by updating the resource package id!");
        }

    }

    /**
     * Shows it is not possible to mark as deleted a resource by update. For
     * dataset it is different, see {@link WriteCkanDatasetIT#testPatchUpdateAsDeleted()
     * }
     */
    @Test
    public void testPatchUpdateAsDeleted() {
        CkanResource resource = createRandomResource();
        resource.setState(CkanState.deleted);
        CkanResource retResource1 = client.patchUpdateResource(resource);
        assertEquals(CkanState.active, retResource1.getState());

    }

    /**
     * Shows resources are just marked as 'deleted', but still accessible from
     * webapi.
     */
    @Test
    public void testDelete() {
        CkanDataset dataset = createRandomDataset(1);
        String resourceId = dataset.getResources().get(0).getId();
        client.deleteResource(resourceId);

        CkanResource retResource = client.getResource(resourceId);
        assertEquals(CkanState.deleted, retResource.getState());
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
        }
        catch (JackanException ex) {

        }

    }
       
}
