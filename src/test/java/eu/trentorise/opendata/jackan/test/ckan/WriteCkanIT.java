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

import com.google.common.collect.Lists;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanGroup;
import eu.trentorise.opendata.jackan.ckan.CkanOrganization;
import eu.trentorise.opendata.jackan.ckan.CkanPair;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.ckan.CkanResourceBase;
import eu.trentorise.opendata.jackan.ckan.CkanState;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.POLITICHE_SVILUPPO_ORGANIZATION_NAME;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.PRODOTTI_CERTIFICATI_DATASET_NAME;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.PRODOTTI_CERTIFICATI_RESOURCE_ID;

import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import junitparams.JUnitParamsRunner;
import static junitparams.JUnitParamsRunner.$;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Assert;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Performs integration tests. Many tests here are also used by
 * {@link CkanTestReporter}
 *
 * @author David Leoni
 */
@RunWith(JUnitParamsRunner.class)
public class WriteCkanIT {
    
    public static final String JACKAN_URL = "http://opendatatrentino.github.io/jackan/";
    
    public static final String TEST_RESOURCE_ID = "81f579fe-7f10-4fa2-94f2-0011898dc78c";
    
    private static final Logger LOG = Logger.getLogger(WriteCkanIT.class.getName());
    
    private Object[] wrongDatasetNames() {
        return $(
                $((String) null),
                $(""),
                $("   "),
                $("$$$$$"),
                $("ab") // need at least 3 chars
        //$(new CkanClient(NOAA_GOV_US))                
        /*,
         $(new CkanClient(DATI_MATERA)),
         $(new CkanClient(DATA_GOV_UK)),
         $(new CkanClient(DATA_GOV_US)) */
        );
    }
    
    private Object[] wrongOrgNames() {
        return $(
                $((String) null),
                $(""),
                $("   "),
                $("$$$$$"),
                $("a") // need at least 2 chars
        //$(new CkanClient(NOAA_GOV_US))                
        /*,
         $(new CkanClient(DATI_MATERA)),
         $(new CkanClient(DATA_GOV_UK)),
         $(new CkanClient(DATA_GOV_US)) */
        );
    }
    
    private Object[] wrongUrls() {
        return $(
                $((String) null),
                $(""),
                $("   "),
                $("http:"),
                $("http://"),
                $("http://a")
        //$(new CkanClient(NOAA_GOV_US))                
        /*,
         $(new CkanClient(DATI_MATERA)),
         $(new CkanClient(DATA_GOV_UK)),
         $(new CkanClient(DATA_GOV_US)) */
        );
    }
    
    private Object[] wrongIds() {
        return $(
                $(""),
                $("   "),
                $("123"));
    }
    
    private CkanDataset makeRandomDataset() {
        CkanDataset ckanDataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        return client.createDataset(ckanDataset);
    }
    
    private CkanResource makeRandomResource() {
        CkanDataset dataset = makeRandomDataset();
        CkanResource resource = new CkanResource(JACKAN_URL, dataset.getId());
        return client.createResource(resource);
    }
    
    CkanClient client;
    
    CkanClient datiTrentinoClient;
    
    @BeforeClass
    public static void setUpClass() {
        JackanTestConfig.of().loadConfig();
    }
    
    @Before
    public void setUp() {
        client = new CkanClient(JackanTestConfig.of().getOutputCkan(), JackanTestConfig.of().getOutputCkanToken());
        datiTrentinoClient = new CkanClient("http://dati.trentino.it");
    }
    
    @After
    public void tearDown() {
        client = null;
    }
    
    @Test
    public void testDatasetCreateMinimal() {
        
        CkanDataset dataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        
        CkanDataset retDataset = client.createDataset(dataset);
        
        checkNotEmpty(retDataset.getId(), "Invalid dataset id!");
        assertEquals(dataset.getName(), retDataset.getName());
        LOG.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), JackanTestConfig.of().getOutputCkan()});
    }
    
    @Test
    public void crap(){
        CkanResource resource = new CkanResource(JACKAN_URL, null);
        resource.setId("2c701bc7-6f40-47ac-a9a9-69ed0503e70a");
        resource.setState(CkanState.deleted);
        client.updateResource(resource);
    }
    
    @Test
    public void testDatasetCreate() {
        
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
    public void testDatasetCreateWrongName(String datasetName) {
        
        try {
            CkanDataset dataset = new CkanDataset(datasetName);
            client.createDataset(dataset);
            Assert.fail();
        }
        catch (JackanException ex) {
            
        }
    }
    
    @Test
    public void testDatasetCreateDuplicateByName() {
        
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
    public void testDatasetCreateWithId() {
        
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
    public void testDatasetCreateNonExistentOrg() {
        
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
    public void testDatasetCreateNonExistentGroup() {
        
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
    public void testDatasetCreateWrongResourceNoUrl() {
        
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
    public void testDatasetCreateResourceWithoutId() {
        
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
    public void testDatasetCreateNonExistentLicenseId() {
        
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
    public void testDatasetCreateMirror() {
        
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
    public void testDatasetUpdate() {
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
    public void testDatasetUpdateResources() {
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
    
    
    @Test
    public void testResourceCreateMinimal() {
        
        CkanDataset dataset = makeRandomDataset();
        
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        
        CkanResource retRes = client.createResource(resource);
        
        checkNotEmpty(retRes.getId(), "Invalid created resource id!");
        assertEquals(resource.getUrl(), retRes.getUrl());
        assertEquals(null, retRes.getPackageId()); // because this won't be present in the result

        LOG.log(Level.INFO, "Created resource with id {0} in catalog {1}", new Object[]{retRes.getId(), JackanTestConfig.of().getOutputCkan()});
    }
    
    @Test
    public void testResourceCreate() {
        
        CkanDataset dataset = makeRandomDataset();
        
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
    public void testResourceCreateWithId() {
        
        CkanDataset dataset = makeRandomDataset();
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        resource.setId(UUID.randomUUID().toString());
        
        CkanResource retRes = client.createResource(resource);
        assertEquals(resource.getId(), retRes.getId());
        
    }
    
    @Test
    public void testResourceCreateWithWrongId() {
        
        CkanDataset dataset = makeRandomDataset();
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        resource.setId(Long.toString(UUID.randomUUID().getMostSignificantBits()));
        
        try {
            CkanResource retRes = client.createResource(resource);
            Assert.fail("Shouldn't be able to create resource with ill formatted short UUID " + resource.getId());
        }
        catch (JackanException ex) {
            
        }
        
    }
    
    @Test
    public void testResourceCreateDuplicateId() {
        
        CkanDataset dataset = makeRandomDataset();
        
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        resource.setId(UUID.randomUUID().toString());
        CkanResource retRes1 = client.createResource(resource);
        try {
            CkanResource retRes2 = client.createResource(resource);
            LOG.log(Level.FINE, "id 1: {0}", retRes1.getId());
            LOG.log(Level.FINE, "id 2: {0}", retRes2.getId());
            Assert.fail();
        }
        catch (JackanException ex) {
            
        }
    }
    
    @Test
    @Parameters(method = "wrongUrls")
    public void testResourceCreateWrongUrl(String url) {
        
        CkanDataset dataset = makeRandomDataset();
        
        try {
            CkanResourceBase resource = new CkanResourceBase(url, dataset.getId());
            client.createResource(resource);
            Assert.fail();
        }
        catch (JackanException ex) {
            
        }
    }
    
    @Test
    public void testResourceCreateWrongDatasetId() {
        
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, UUID.randomUUID().toString());
        try {
            client.createResource(resource);
            Assert.fail();
        }
        catch (JackanException ex) {
            
        }
    }
    
    @Test
    public void testResourceCreateMirror() {
        
        CkanDataset dataset = makeRandomDataset();
        
        CkanResource resource = datiTrentinoClient.getResource(PRODOTTI_CERTIFICATI_RESOURCE_ID);
        
        resource.setPackageId(dataset.getId());
        
        CkanResource retResource = client.createResource(resource);
        
        checkNotEmpty(retResource.getId(), "Invalid resource id!");
        
        LOG.log(Level.INFO, "created resource with id {0} in catalog {1}", new Object[]{retResource.getId(), JackanTestConfig.of().getOutputCkan()});
        
    }
    
    @Test
    public void testResourceUpdate() {
        CkanDataset dataset = makeRandomDataset();
        CkanResource resource = new CkanResource(JACKAN_URL, dataset.getId());
        resource.setSize("123");
        resource.setOwner("acme");  // owner is not in CkanResourceBase, shouldn't be sent in update post
        resource.putOthers("x", "y");
        
        CkanResource retRes1 = client.createResource(resource);
        
        retRes1.setDescription("abc");
        
        retRes1.setSize(null); // so we won't send it in the post
        retRes1.setOthers(null); // so we won't send it in the post and hopefully trigger client automerge feature

        CkanResource retRes2 = client.updateResource(retRes1);
        
        assertEquals(retRes1.getId(), retRes2.getId());
        assertEquals(retRes1.getUrl(), retRes2.getUrl());
        assertEquals("abc", retRes2.getDescription());
        assertEquals("123", retRes2.getSize());
        assertEquals(resource.getOthers(), retRes2.getOthers());
        assertEquals(null, retRes2.getOwner());  // owner is not among api docs for creation, so hopefully was jsonignored when sending retRes1            
    }
    
    @Test
    public void testResourceUpdateWithExistingId() {
        CkanResource res1 = makeRandomResource();
        CkanDataset dataset = makeRandomDataset();
        CkanResourceBase res2 = new CkanResourceBase(JACKAN_URL, dataset.getId());
        res2.setId(res1.getId());
        try {
            client.createResource(res2);
            Assert.fail("Shouldn't be able to create resource with existing id: " + res1.getId());
        }
        catch (JackanException ex) {
            
        }
    }
    
    @Test
    @Parameters(method = "wrongIds")
    public void testResourceUpdateWrongId(String id) {
        
        CkanDataset dataset = makeRandomDataset();
        
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset.getId());
        resource.setId(id);
        try {
            client.updateResource(resource);
            Assert.fail();
        }
        catch (JackanException ex) {
            
        }
    }

    /**
     * This shows it is not possible to move a resource from one dataset to
     * another by updating the resource packageId.
     */
    @Test
    public void testResourceUpdateDataset() {
        
        CkanDataset dataset1 = makeRandomDataset();
        CkanResourceBase resource = new CkanResourceBase(JACKAN_URL, dataset1.getId());
        CkanResource retResource1 = client.createResource(resource);
        CkanDataset dataset2 = makeRandomDataset();
        resource.setPackageId(dataset2.getId());
        CkanResource retResource2 = client.updateResource(retResource1);
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
    
    @Test
    public void testOrganizationCreateMinimal() {
        CkanOrganization org = new CkanOrganization("ab-" + UUID.randomUUID().getMostSignificantBits());
        
        CkanOrganization retOrg = client.createOrganization(org);
        
        checkNotEmpty(retOrg.getId(), "Invalid organization id!");
        /*        assertEquals(dataset.getName(), retDataset.getName());
         assertEquals(dataset.getUrl(), retDataset.getUrl());
         assertEquals(dataset.getExtras(), retDataset.getExtras());
         assertEquals(dataset.getTitle(), retDataset.getTitle());
         assertEquals(dataset.getLicenseId(), retDataset.getLicenseId());*/
        LOG.log(Level.INFO, "created organization with id {0} in catalog {1}", new Object[]{retOrg.getId(), JackanTestConfig.of().getOutputCkan()});
    }
    
    @Test
    public void testOrganizationCreateMirror() {
        
        CkanOrganization org = datiTrentinoClient.getOrganization(POLITICHE_SVILUPPO_ORGANIZATION_NAME);
        
        org.setName(POLITICHE_SVILUPPO_ORGANIZATION_NAME + "-" + UUID.randomUUID().getMostSignificantBits());
        org.setId(null);
        
        CkanOrganization retOrg = client.createOrganization(org);
        
        checkNotEmpty(retOrg.getId(), "Invalid organization id!");
        
        LOG.log(Level.INFO, "created organization with id {0} in catalog {1}", new Object[]{retOrg.getId(), JackanTestConfig.of().getOutputCkan()});
    }
    
    @Test
    @Parameters(method = "wrongOrgNames")
    public void testOrganizationCreateWrongName(String orgName) {
        String uri = "http://github.com/opendatatrentino/jackan";
        
        try {
            CkanOrganization org = new CkanOrganization(orgName);
            client.createOrganization(org);
            Assert.fail();
        }
        catch (Exception ex) {
            
        }
    }
    
    @Test
    public void testOrganizationCreateById() {
        CkanOrganization org = new CkanOrganization();
        org.setId(UUID.randomUUID().toString());
        org.setName(org.getId());
        CkanOrganization retOrg = client.createOrganization(org);
        assertEquals(org.getId(), retOrg.getId());
    }
    
    @Test
    public void testOrganizationCreateDuplicateByName() {
        
        String orgName = "test-organization-jackan-" + UUID.randomUUID().getMostSignificantBits();
        
        CkanOrganization org = new CkanOrganization(orgName);
        client.createOrganization(org);
        try {
            client.createOrganization(org);
            Assert.fail("Shouldn't be able to create organization with same name " + orgName);
        }
        catch (JackanException ex) {
            
        }
    }
    
    @Test
    public void testOrganizationCreateDuplicateById() {
        
        CkanOrganization org = new CkanOrganization();
        org.setId(UUID.randomUUID().toString());
        org.setName(org.getId());
        client.createOrganization(org);
        try {
            client.createOrganization(org);
            Assert.fail("Shouldn't be able to create organization with same id " + org.getId());
        }
        catch (JackanException ex) {
            
        }
    }


    
}
