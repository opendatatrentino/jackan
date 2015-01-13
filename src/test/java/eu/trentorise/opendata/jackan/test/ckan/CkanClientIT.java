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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.SearchResults;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanGroup;
import eu.trentorise.opendata.jackan.ckan.CkanLicense;
import eu.trentorise.opendata.jackan.ckan.CkanOrganization;
import eu.trentorise.opendata.jackan.ckan.CkanPair;
import eu.trentorise.opendata.jackan.ckan.CkanQuery;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.ckan.CkanTag;
import eu.trentorise.opendata.jackan.ckan.CkanUser;
import eu.trentorise.opendata.jackan.test.TestConfig;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junitparams.JUnitParamsRunner.$;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 *
 * @author David Leoni
 */
@RunWith(JUnitParamsRunner.class)
public class CkanClientIT {

    public static Logger logger = Logger.getLogger(CkanClientIT.class.getName());

    public static String DATI_TRENTINO = "http://dati.trentino.it";
    public static String DATI_TOSCANA = "http://dati.toscana.it";
    public static String DATI_MATERA = "http://dati.comune.matera.it";
    public static String DATA_GOV_UK = "http://data.gov.uk";
    public static String DATA_GOV_US = "http://catalog.data.gov";

    /**
     * Unfortunately this one uses old api version, we can't use it.
     */
    public static String DATI_PIEMONTE = "http://www.dati.piemonte.it/rpapisrv/api/rest";

    public static final String LAGHI_MONITORATI_TRENTO_NAME = "laghi-monitorati-trento-143675";
    public static final String LAGHI_MONITORATI_TRENTO_ID = "3745b44c-751f-40b3-8e97-ccd725bfbe8a";
    public static final String LAGHI_MONITORATI_TRENTO_XML_RESOURCE_NAME = "Metadati in formato XML";

    private Multimap<String, String> datasetList = LinkedListMultimap.create();

    CkanClient client;
    private int TEST_ELEMENTS = 5;

    private Object[] clients() {
        return $(
                $(new CkanClient(DATI_TRENTINO)),
                $(new CkanClient(DATI_TOSCANA)),
                $(new CkanClient(DATI_MATERA)),
                $(new CkanClient(DATA_GOV_UK)),
                $(new CkanClient(DATA_GOV_US))
        );
    }

    @BeforeClass
    public static void setUpClass() {
        TestConfig.initLogger();
        TestConfig.initProperties();
    }

    @Before
    public void setUp() {
        client = new CkanClient(DATI_TRENTINO);
    }

    @After
    public void tearDown() {
        client = null;
    }

    /**
     * todo we should do some ckan internal version detector (sic)
     */
    @Test
    @Parameters(method = "clients")
    public void testApiVersionSupported(CkanClient client) {
        int version = client.getApiVersion();
        assertTrue("Found api version " + version + ", supported versions are: " + CkanClient.SUPPORTED_API_VERSIONS,
                CkanClient.SUPPORTED_API_VERSIONS.contains(version));
    }

    @Test
    @Parameters(method = "clients")
    public void testDatasetList(CkanClient client) {
        List<String> dsl = client.getDatasetList();
        assertTrue(dsl.size() > 0);
    }

    /**
     * Ckan docs don't tell offset starts with 0
     *
     * For some weird reason {@link #DATI_MATERA} claims to have api v3 but does
     * not support limit & offset params
     */
    @Test
    @Parameters(method = "clients")
    public void testDatasetListWithLimit(CkanClient client) {
        List<String> dsl = client.getDatasetList(1, 0);
        assertEquals(1, dsl.size());
    }

    @Test
    @Parameters(method = "clients")
    public void testSearchDatasetsByText(CkanClient client) {
        List<String> dsl = client.getDatasetList(1, 0);
        assertTrue(dsl.size() > 0);

        SearchResults<CkanDataset> r = client.searchDatasets(dsl.get(0), 10, 0);

        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    @Parameters(method = "clients")
    public void testDatasetAndResource(CkanClient client) {
        List<String> dsl = client.getDatasetList(TEST_ELEMENTS, 0);
        assertTrue(dsl.size() > 0);

        List<FailedResourceException> failedResources = new ArrayList();

        for (String datasetName : dsl.subList(0, Math.min(dsl.size(), TEST_ELEMENTS))) {
            CkanDataset dataset = client.getDataset(datasetName);
            assertEquals(datasetName, dataset.getName());
            for (CkanResource resource : dataset.getResources().subList(0, Math.min(dataset.getResources().size(), TEST_ELEMENTS))) {
                try {
                    client.getResource(resource.getId());
                }
                catch (Exception ex) {
                    failedResources.add(new FailedResourceException(client, "Error while fetching resource!", datasetName, resource.getId(), ex));
                }
            }
        }

        if (!failedResources.isEmpty()) {
            throw new RuntimeException("Couldn't fetch these resources: \n " + failedResources.toString());
        }

    }

    @Test
    @Parameters(method = "clients")
    public void testLicenseList(CkanClient client) {
        List<CkanLicense> licenses = client.getLicenseList();
        assertTrue(licenses.size() > 0);
    }

    @Test
    @Parameters(method = "clients")
    public void testTagList(CkanClient client) {
        List<CkanTag> tl = client.getTagList();
        assertTrue(tl.size() > 0);
    }

    /**
     * Searched by name
     */
    @Test
    @Parameters(method = "clients")
    public void testTagNameList(CkanClient client) {

        List<CkanTag> tagList = client.getTagList();
        assertTrue(tagList.size() > 0);

        String firstTagName = tagList.get(0).getName();
        assertTrue(firstTagName.length() > 0);
        String searchText = firstTagName.substring(0, firstTagName.length() - 1);

        List<String> tl = client.getTagNamesList(searchText);

        assertTrue(tl.get(0).toLowerCase().contains(searchText));
    }

    @Test
    @Parameters(method = "clients")
    public void testUserList(CkanClient client) {
        List<CkanUser> ul = client.getUserList();
        assertTrue(ul.size() > 0);
    }

    /**
     * Tries to get the "admin" user.
     */
    @Test
    @Parameters(method = "clients")
    public void testUser(CkanClient client) {
        CkanUser u = client.getUser("admin");
        assertEquals("admin", u.getName());
    }

    @Test
    @Parameters(method = "clients")
    public void testGroupList(CkanClient client) {
        List<CkanGroup> gl = client.getGroupList();
        assertTrue(gl.size() > 0);
    }

    @Test
    @Parameters(method = "clients")
    public void testGroup(CkanClient client) {
        List<CkanGroup> gl = client.getGroupList();
        assertTrue(gl.size() > 0);

        for (CkanGroup g : gl.subList(0, Math.min(gl.size(), TEST_ELEMENTS))) {
            CkanGroup fetchedGroup = client.getGroup(g.getId());
            assertEquals(g.getName(), fetchedGroup.getName());
        }
    }

    @Test
    @Parameters(method = "clients")
    public void testOrganizationList(CkanClient client) {
        List<CkanOrganization> gl = client.getOrganizationList();
        assertTrue(gl.size() > 0);

    }

    @Test
    @Parameters(method = "clients")
    public void testOrganization(CkanClient client) {
        List<CkanOrganization> gl = client.getOrganizationList();
        assertTrue(gl.size() > 0);

        for (CkanOrganization g : gl.subList(0, Math.min(gl.size(), TEST_ELEMENTS))) {
            CkanOrganization fetchedOrganization = client.getOrganization(g.getId());
            assertEquals(g.getName(), fetchedOrganization.getName());
        }
    }

    @Test
    @Parameters(method = "clients")
    public void testFormatList(CkanClient client) {
        Set<String> formats = client.getFormats();
        assertTrue(formats.size() > 0);
    }

    @Test
    @Parameters(method = "clients")
    public void testSearchDatasetsByGroups(CkanClient client) {
        List<CkanGroup> groups = client.getGroupList();
        assertTrue(groups.size() > 0);

        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byGroupNames(groups.get(0).getName()), 10, 0);

        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    @Parameters(method = "clients")
    public void testSearchDatasetsByOrganization(CkanClient client) {
        List<CkanOrganization> organizations = client.getOrganizationList();
        assertTrue(organizations.size() > 0);

        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byOrganizationName(organizations.get(0).getName()), 10, 0);
        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    @Parameters(method = "clients")
    public void testSearchDatasetsByTags(CkanClient client) {

        List<String> tagNames = client.getTagNamesList();
        assertTrue(tagNames.size() > 0);

        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byTagNames(tagNames.get(0)), 10, 0);
        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    @Parameters(method = "clients")
    public void testSearchDatasetsByLicenseIds(CkanClient client) throws JsonProcessingException {
        List<CkanLicense> licenses = client.getLicenseList();
        assertTrue(licenses.size() > 0);
        for (CkanLicense license : licenses) {
            SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byLicenseId(license.getId()), 10, 0);
            if (r.getResults().size() > 0) {
                return;
            }
        }
        Assert.fail("I should get at least one dataset matching some license! Tried licenses were: " + CkanClient.getObjectMapperClone().writeValueAsString(licenses));
    }

    @Test
    public void testFullSearch() {
        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter()
                .byText("elenco dei prodotti trentini")
                .byGroupNames("agricoltura")
                .byOrganizationName("pat-s-sviluppo-rurale")
                .byTagNames("prodotti tipici", "enogastronomia")
                .byLicenseId("cc-zero"), 10, 0);
        assertEquals("cc-zero", r.getResults().get(0).getLicenseId());
        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    public void testCkanError() {
        try {
            CkanDataset dataset = client.getDataset("666");
            fail();
        }
        catch (JackanException ex) {

        }

    }

    @Test
    public void testCreateDataset() throws URISyntaxException {

        CkanClient cClient = new CkanClient(TestConfig.getOutputCkan(), TestConfig.getOutputCkanToken());

        CkanPair ckanPair = new CkanPair();
        ckanPair.setKey("test key");
        ckanPair.setValue("test value");
        List<CkanPair> extras = new ArrayList<CkanPair>();
        extras.add(ckanPair);

        String uri = "http://github.com/opendatatrentino/Jackan";

        long uuid = UUID.randomUUID().getMostSignificantBits();

        String datasetName = "test-dataset-jackan-" + uuid;

        CkanDataset ckanDataset = new CkanDataset();
        ckanDataset.setName(datasetName);
        ckanDataset.setUrl(uri);
        ckanDataset.setExtras(extras);
        ckanDataset.setTitle("Test Jackan Dataset " + uuid);
        ckanDataset.setLicenseId("cc-zero");

        CkanDataset retDataset = cClient.createDataset(ckanDataset);

        assertNotNull(retDataset.getId());
        assertTrue(retDataset.getId().length() > 0);
        logger.log(Level.INFO, "created dataset with id {0} in catalog {1}", new Object[]{retDataset.getId(), TestConfig.getOutputCkan()});
    }

    @Test
    public void testCreateResource() throws URISyntaxException {

        CkanClient cClient = new CkanClient(TestConfig.getOutputCkan(), TestConfig.getOutputCkanToken());

        String uri = "http://github.com/opendatatrentino/jackan";

        long uuidDataset = UUID.randomUUID().getMostSignificantBits();
        String datasetName = "test-dataset-jackan-" + uuidDataset;

        CkanDataset ckanDataset = new CkanDataset(datasetName,
                uri,
                new ArrayList(),
                "Test Jackan Dataset " + uuidDataset,
                "cc-zero");

        CkanDataset retDataset = cClient.createDataset(ckanDataset);

        CkanResource ckanResource = new CkanResource("JSONLD",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits() + uuidDataset,
                uri,
                "Most interesting test resource in the universe",
                retDataset.getId(),
                null);

        CkanResource retCkanRes = cClient.createResource(ckanResource);

        assertNotNull(retCkanRes.getId());
        assertTrue(retCkanRes.getId().length() > 0);
        logger.log(Level.INFO, "Created resource with id {0} in catalog {1}", new Object[]{retCkanRes.getId(), TestConfig.getOutputCkan()});

    }

    /**
     * todo review this!!!
     *
     * @throws URISyntaxException
     */
    @Test
    @Ignore
    public void testUpdateDataset() throws URISyntaxException {
        CkanClient cClient = new CkanClient(TestConfig.getOutputCkan(), TestConfig.getOutputCkanToken());

        URI uri = null;

        uri = new URI("http", "www.unitn.it", null, null);

        CkanDataset dataset = new CkanDataset("Test-Jackan-Dataset " + UUID.randomUUID().getMostSignificantBits(),
                "http://jackan-land-of-dreams.org",
                new ArrayList(),
                "Test Jackan Dataset " + UUID.randomUUID().getMostSignificantBits(),
                "cc-zero");

        // CkanResourceMinimized ckanResource = new CkanResourceMinimized("JSONLD", "ivanresource2", uri.toASCIIString(), "test resource", "07dfd366-2107-4c06-97f5-2acdeff49aff", null);
        CkanResource resource1 = new CkanResource("JSONLD",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                "http://go-play-with-jackan.org/myfile_1.jsonld",
                "First most interesting test resource in the universe",
                dataset.getId(),
                null);

        CkanDataset createdDataset = cClient.createDataset(dataset);
        CkanResource createdResource = cClient.createResource(resource1);

        CkanResource resource2 = new CkanResource("JSONLD",
                "Jackan test resource " + UUID.randomUUID().getMostSignificantBits(),
                "http://go-play-with-jackan.org/myfile_2.jsonld",
                "Second most interesting test resource in the universe",
                dataset.getId(),
                null);

        createdDataset.setAuthor("Jackan enthusiast");
        createdDataset.getResources().add(resource2);

        // CkanDataset updatedDataset = cClient.updateDataset(createdDataset);
        // logger.log(Level.INFO, "Ckan Resource URL changed:{0}", cResource.getUrl());
    }

}
