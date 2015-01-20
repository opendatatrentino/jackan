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

import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanDatasetMinimized;
import eu.trentorise.opendata.jackan.ckan.CkanPair;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.ckan.CkanResourceMinimized;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

    public static Logger logger = Logger.getLogger(WriteCkanIT.class.getName());

    CkanClient client;

    @BeforeClass
    public static void setUpClass() {
        JackanTestConfig.of().loadConfig();
    }

    @Before
    public void setUp() {
        client = new CkanClient(JackanTestConfig.of().getOutputCkan(), JackanTestConfig.of().getOutputCkanToken());
    }

    @After
    public void tearDown() {
        client = null;
    }

    @Test
    public void testCreateDataset() throws URISyntaxException {

        CkanPair ckanPair = new CkanPair();
        ckanPair.setKey("test key");
        ckanPair.setValue("test value");
        List<CkanPair> extras = new ArrayList<CkanPair>();
        extras.add(ckanPair);

        String uri = "http://github.com/opendatatrentino/Jackan";

        long datasetNumber = UUID.randomUUID().getMostSignificantBits();

        String datasetName = "test-dataset-jackan-" + datasetNumber;

        CkanDataset ckanDataset = new CkanDataset(datasetName, uri, extras);
        ckanDataset.setTitle("Test Jackan Dataset " + datasetNumber);
        ckanDataset.setLicenseId("cc-zero");

        CkanDataset retDataset = client.createDataset(ckanDataset);

        assertNotNull(retDataset.getId());
        assertTrue(retDataset.getId().length() > 0);
        logger.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    @Test
    public void testCreateDatasetMinimized() throws URISyntaxException {

        CkanPair ckanPair = new CkanPair();
        ckanPair.setKey("test key");
        ckanPair.setValue("test value");
        List<CkanPair> extras = new ArrayList<CkanPair>();
        extras.add(ckanPair);

        String uri = "http://github.com/opendatatrentino/Jackan";

        long datasetNumber = UUID.randomUUID().getMostSignificantBits();

        String datasetName = "test-dataset-jackan-" + datasetNumber;

        CkanDatasetMinimized ckanDataset = new CkanDatasetMinimized(datasetName, 
                uri, 
                extras, 
                "Test Jackan Dataset " + datasetNumber, 
                "cc-zero");

        CkanDataset retDataset = client.createDataset(ckanDataset);

        assertNotNull(retDataset.getId());
        assertTrue(retDataset.getId().length() > 0);
        logger.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    @Test
    public void testCreateResource() throws URISyntaxException {

        String uri = "http://github.com/opendatatrentino/jackan";

        long datasetNumber = UUID.randomUUID().getMostSignificantBits();
        String datasetName = "test-dataset-jackan-" + datasetNumber;

        CkanDatasetMinimized ckanDataset = new CkanDatasetMinimized(datasetName,
                uri,
                new ArrayList(),
                "Test Jackan Dataset " + datasetNumber,
                "cc-zero");

        CkanDataset retDataset = client.createDataset(ckanDataset);

        CkanResource ckanResource = new CkanResource("JSONLD",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                uri,
                "Most interesting test resource in the universe",
                retDataset.getId(),
                null);

        CkanResource retCkanRes = client.createResource(ckanResource);

        assertNotNull(retCkanRes.getId());
        assertTrue(retCkanRes.getId().length() > 0);
        logger.log(Level.INFO, "Created resource with id {0} in catalog {1}", new Object[]{retCkanRes.getId(), JackanTestConfig.of().getOutputCkan()});

    }

    @Test
    public void testCreateResourceMinimized() throws URISyntaxException {

        String uri = "http://github.com/opendatatrentino/jackan";

        long datasetNumber = UUID.randomUUID().getMostSignificantBits();
        String datasetName = "test-dataset-jackan-" + datasetNumber;

        CkanDatasetMinimized ckanDataset = new CkanDatasetMinimized(datasetName,
                uri,
                new ArrayList(),
                "Test Jackan Dataset " + datasetNumber,
                "cc-zero");

        CkanDataset retDataset = client.createDataset(ckanDataset);

        CkanResourceMinimized ckanResource = new CkanResourceMinimized("JSONLD",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits() + datasetNumber,
                uri,
                "Most interesting test resource in the universe",
                retDataset.getId(),
                null);

        CkanResource retCkanRes = client.createResource(ckanResource);

        assertNotNull(retCkanRes.getId());
        assertTrue(retCkanRes.getId().length() > 0);
        logger.log(Level.INFO, "Created resource with id {0} in catalog {1}", new Object[]{retCkanRes.getId(), JackanTestConfig.of().getOutputCkan()});

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
        List<CkanPair> datasetExtras = new ArrayList<CkanPair>();
        datasetExtras.add(ckanPair);

        long datasetNumber = UUID.randomUUID().getMostSignificantBits();
        CkanDataset dataset = new CkanDataset("Test-Jackan-Dataset " + datasetNumber,
                "http://jackan-land-of-dreams.org",
                datasetExtras);
        dataset.setTitle("Test Jackan Dataset " + datasetNumber);

        dataset.setLicenseId("cc-zero");

        CkanDataset createdDataset = client.createDataset(dataset);

        CkanResource resource1 = new CkanResource("JSONLD",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                "http://go-play-with-jackan.org/myfile_1.jsonld",
                "First most interesting test resource in the universe",
                dataset.getId(),
                null);

        CkanResource createdResource = client.createResource(resource1);

        CkanResource resource2 = new CkanResource("JSONLD",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                "http://go-play-with-jackan.org/myfile_2.jsonld",
                "Second most interesting test resource in the universe",
                dataset.getId(),
                null);

        createdDataset.setAuthor("Jackan enthusiast");
        createdDataset.getResources().add(resource2);

        CkanDataset updatedDataset = client.updateDataset(createdDataset);

        throw new RuntimeException("todo write check updatedDataset is not corrupted");
    }
}
