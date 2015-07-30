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

import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanPair;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.PRODOTTI_CERTIFICATI_DATASET_NAME;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import junitparams.JUnitParamsRunner;

import org.junit.After;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

    public static final String TEST_RESOURCE_ID = "81f579fe-7f10-4fa2-94f2-0011898dc78c";

    private static final Logger LOG = Logger.getLogger(WriteCkanIT.class.getName());

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
    public void testCreateSimpleDataset()  {

        CkanPair ckanPair = new CkanPair();
        ckanPair.setKey("test key");
        ckanPair.setValue("test value");
        List<CkanPair> extras = new ArrayList();
        extras.add(ckanPair);

        String uri = "http://github.com/opendatatrentino/jackan";

        long datasetNumber = UUID.randomUUID().getMostSignificantBits();

        String datasetName = "test-dataset-jackan-" + datasetNumber;

        CkanDataset dataset = new CkanDataset(datasetName, uri, extras);
        dataset.setTitle("Test Jackan Dataset " + datasetNumber);
        dataset.setLicenseId("cc-zero");

        CkanDataset retDataset = client.createDataset(dataset);

        checkNotEmpty(retDataset.getId(), "Invalid dataset id!");
        assertEquals(dataset.getName(), retDataset.getName());
        assertEquals(dataset.getUrl(), retDataset.getUrl());
        assertEquals(dataset.getExtras(), retDataset.getExtras());
        assertEquals(dataset.getTitle(), retDataset.getTitle());
        assertEquals(dataset.getLicenseId(), retDataset.getLicenseId());
        LOG.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    
    @Test
    @Ignore
    public void testCreateComplexDataset() {

        CkanDataset dataset = datiTrentinoClient.getDataset(PRODOTTI_CERTIFICATI_DATASET_NAME);
                
        dataset.setExtras(new ArrayList()); // dati.trentino has custom schemas and merges metadata among regular fields
        
        CkanDataset retDataset = client.createDataset(dataset);

        checkNotEmpty(retDataset.getId(), "Invalid dataset id!");
        
        LOG.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), JackanTestConfig.of().getOutputCkan()});
    }
    
    
    @Test
    public void testCreateResource() throws URISyntaxException {

        String url = "http://github.com/opendatatrentino/jackan";

        long datasetNumber = UUID.randomUUID().getMostSignificantBits();
        String datasetName = "test-dataset-jackan-" + datasetNumber;

        CkanDataset ckanDataset = new CkanDataset(datasetName,
                url,
                new ArrayList());
        ckanDataset.setTitle("Test Jackan Dataset " + datasetNumber);
        ckanDataset.setLicenseId("cc-zero");

        CkanDataset retDataset = client.createDataset(ckanDataset);

        CkanResource res = new CkanResource("jsonld",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                url,
                "Most interesting test resource in the universe",
                retDataset.getId());

        // todo add more fields
        CkanResource retRes = client.createResource(res);

        checkNotEmpty(retRes.getId(), "Invalid created resource id!");
        assertEquals(res.getFormat(), retRes.getFormat());
        assertEquals(res.getName(), retRes.getName());
        assertEquals(res.getUrl(), retRes.getUrl());
        assertEquals(res.getDescription(), retRes.getDescription());
        assertEquals(null, retRes.getPackageId()); // because this won't be present in the result
        
        LOG.log(Level.INFO, "Created resource with id {0} in catalog {1}", new Object[]{retRes.getId(), JackanTestConfig.of().getOutputCkan()});

    }

    

    /**
     * todo review this!!!
     *
     */
    @Test
    public void testUpdateResourceMinimized() {
        long datasetNumber = UUID.randomUUID().getMostSignificantBits();
        CkanDataset dataset = new CkanDataset("Test-Jackan-Dataset " + datasetNumber,
                "http://jackan-land-of-dreams.org",
                new ArrayList());

        dataset.setTitle("Test Jackan Dataset " + datasetNumber);

        dataset.setLicenseId("cc-zero");

        CkanDataset createdDataset = client.createDataset(dataset);

        CkanResource resource1 = new CkanResource("JSONLD",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                "http://go-play-with-jackan.org/myfile_1.jsonld",
                "First most interesting test resource in the universe",
                dataset.getId());

        CkanResource createdResource = client.createResource(resource1);

        //client.updateResource(null)
        throw new RuntimeException("todo implement test update resource!");
    }

    /**
     * todo review this!!!
     *
     * @throws URISyntaxException
     */
    @Test
    public void testUpdateDataset() throws URISyntaxException {

        CkanPair ckanPair = new CkanPair();
        ckanPair.setKey("test key");
        ckanPair.setValue("test value");
        List<CkanPair> datasetExtras = new ArrayList();
        datasetExtras.add(ckanPair);

        long datasetNumber = UUID.randomUUID().getMostSignificantBits();
        CkanDataset dataset = new CkanDataset("Test-Jackan-Dataset " + datasetNumber,
                "http://jackan-land-of-dreams.org",
                datasetExtras);
        dataset.setTitle("Test Jackan Dataset " + datasetNumber);

        dataset.setLicenseId("cc-zero");

        CkanDataset createdDataset = client.createDataset(dataset);

        CkanResource resource1 = new CkanResource("jsonld",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                "http://go-play-with-jackan.org/myfile_1.jsonld",
                "First most interesting test resource in the universe",
                dataset.getId());

        CkanResource createdResource = client.createResource(resource1);

        CkanResource resource2 = new CkanResource("jsonld",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                "http://go-play-with-jackan.org/myfile_2.jsonld",
                "Second most interesting test resource in the universe",
                dataset.getId());

        createdDataset.setAuthor("Jackan enthusiast");
        createdDataset.getResources().add(resource2);

        CkanDataset updatedDataset = client.updateDataset(createdDataset);

        throw new RuntimeException("todo write check updatedDataset is not corrupted");
    }

    @Test
    public void testUpdateResource() {

        CkanResource ckanResource = new CkanResource("JSONLD", "ivanresource2", "http://mysite.org", "test resource", "81f579fe-7f10-4fa2-94f2-0011898dc78c");
        
        CkanResource ckanResource2 = new CkanResource("JSONLD", "my test resource", "http://mysite.org", "test res", "81f579fe-7f10-4fa2-94f2-0011898dc78c");
     
        ckanResource.setOwner("Tankoyeu");
        ckanResource.setId(TEST_RESOURCE_ID);
        assertEquals(ckanResource.getOwner(), "Tankoyeu");

        CkanResource cResource1 = client.updateResource(ckanResource, true);
        assertEquals(cResource1.getOwner(), "Tankoyeu");

        CkanResource cResource2 = client.updateResource(ckanResource2, false);
        assertEquals(cResource2.getOwner(), "None");

    }
}
