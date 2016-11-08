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
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import eu.trentorise.opendata.jackan.SearchResults;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.exceptions.CkanNotFoundException;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanLicense;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.CkanQuery;
import eu.trentorise.opendata.jackan.model.CkanError;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanTag;
import eu.trentorise.opendata.jackan.model.CkanUser;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junitparams.JUnitParamsRunner.$;
import eu.trentorise.opendata.jackan.test.JackanTestRunner;
import junitparams.Parameters;
import org.apache.http.HttpHost;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

/**
 * Performs integration tests. Many tests here are also used by
 * {@link CkanTestReporter}
 *
 * @author David Leoni
 */
@RunWith(JackanTestRunner.class)
public class ReadCkanIT {

    public static final Logger logger = Logger.getLogger(ReadCkanIT.class.getName());

    public static String DATI_TRENTINO = "http://dati.trentino.it";
    public static String DATI_TOSCANA = "http://dati.toscana.it";
    public static String DATI_MATERA = "http://dati.comune.matera.it";
    public static String DATA_GOV_UK = "http://data.gov.uk";
    public static String DATA_GOV_US = "http://catalog.data.gov";
    public static String DATAHUB_IO = "http://datahub.io";
    

    /** for testing timeouts */
    public static String UNREACHABLE_HOST = "http://10.0.0.0";
    
    /**
     * National Oceanic and Atmospheric Administration (United States)
     */
    public static String NOAA_GOV_US = "https://data.noaa.gov";

    /**
     * Unfortunately this one uses old api version, we can't use it.
     */
    public static String DATI_PIEMONTE = "http://www.dati.piemonte.it/rpapisrv/api/rest";

    public static final String LAGHI_MONITORATI_TRENTO_NAME = "laghi-monitorati-trento-143675";
    public static final String LAGHI_MONITORATI_TRENTO_ID = "3745b44c-751f-40b3-8e97-ccd725bfbe8a";
    public static final String LAGHI_MONITORATI_TRENTO_XML_RESOURCE_NAME = "Metadati in formato XML";
    public static final String PRODOTTI_CERTIFICATI_DATASET_NAME = "prodotti-certificati";
    public static final String PRODOTTI_CERTIFICATI_RESOURCE_ID = "fe507a10-4c49-4b18-8bf6-6705198cfd42";
    public static final String POLITICHE_SVILUPPO_ORGANIZATION_NAME = "pat-s-sviluppo-rurale";
    public static final String AGRICOLTURA_GROUP_NAME = "agricoltura";

    private Multimap<String, String> datasetList = LinkedListMultimap.create();

    public static int TEST_ELEMENTS = 5;

    /**
     * Object mapper for reading
     */
    private ObjectMapper objectMapper;

    /**
     * All reading tests will be tried with all these catalogs.
     */
    public Object[] clients() {
        return $(
                $(new CkanClient(DATI_TRENTINO)),
                $(new CkanClient(DATI_TOSCANA))
        //$(new CkanClient(NOAA_GOV_US))                
        /*,
         $(new CkanClient(DATI_MATERA)),
         $(new CkanClient(DATA_GOV_UK)),
         $(new CkanClient(DATA_GOV_US)) */
        );
    }

    @BeforeClass
    public static void setUpClass() {
        JackanTestConfig.of().loadConfig();
    }

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        CkanClient.configureObjectMapper(objectMapper);
    }

    @After
    public void tearDown() {
        objectMapper = null;
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
     * Tries to get some users.
     */
    @Test
    @Parameters(method = "clients")
    public void testUser(CkanClient client) {
        List<CkanUser> ul = client.getUserList();
        assertTrue(ul.size() > 0);        
        for (CkanUser u : ul.subList(0, Math.min(ul.size(), TEST_ELEMENTS))) {
            CkanUser fetchedUser = client.getUser(u.getId());
            assertEquals(u.getName(), fetchedUser.getName());
        }
    }

    @Test
    @Parameters(method = "clients")
    public void testGroupList(CkanClient client) {
        List<CkanGroup> gl = client.getGroupList();
        assertTrue(gl.size() > 0);
        // api actually returns the number of datasets, so we check they are properly 'converted'.
        assertEquals(null, gl.get(0).getPackages());

    }

    @Test
    @Parameters(method = "clients")
    public void testGroupNames(CkanClient client) {
        List<String> gl = client.getGroupNames();
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
        // api actually returns the number of datasets, so we check they are properly 'converted'.
        assertEquals(null, gl.get(0).getPackages());
    }

    @Test
    @Parameters(method = "clients")
    public void testOrganizationNames(CkanClient client) {
        List<String> gl = client.getOrganizationNames();
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

        List<String> datasetNamesList = client.getDatasetList();
        assertTrue(datasetNamesList.size() > 0);

        List<CkanTag> tagList = client.getTagList();
        assertTrue(tagList.size() > 0);

        String tagName;
        for (String datasetName : datasetNamesList.subList(0, Math.min(datasetList.size(), TEST_ELEMENTS))) {
            CkanDataset dataset = client.getDataset(datasetName);
            List<CkanTag> tags = dataset.getTags();
            if (tags.size() > 0 && tags.get(0).getName().length() > 0) {
                tagName = tags.get(0).getName();
                SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byTagNames(tagName), 10, 0);
                if (r.getResults().isEmpty()) {
                    Assert.fail("I should find dataset " + dataset.getUrl() + " when searching for tag " + tagName);
                } else {
                    return;
                }
            }
        }

        for (CkanTag tag : tagList.subList(0, Math.min(tagList.size(), TEST_ELEMENTS))) {
            SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byTagNames(tag.getName()), 10, 0);
            if (r.getResults().size() > 0) {
                return;
            }
        }

        Assert.fail("Couldn't find a dataset containing a tag so to be able to test search by tag.");
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
        Assert.fail("I should get at least one dataset matching some license! Tried licenses were: " + objectMapper.writeValueAsString(licenses));
    }

    @Test
    @Parameters(method = "clients")
    public void testCkanError(CkanClient client) {
        try {
            CkanDataset dataset = client.getDataset(UUID.randomUUID().toString());
            fail();
        }
        catch (CkanNotFoundException ex) {
            checkNotNull(ex.getCkanResponse().getError());
            assertEquals(CkanError.NOT_FOUND_ERROR, ex.getCkanResponse().getError().getType());
        }

    }

    @Test
    public void testFullSearch() {
                 
         SearchResults<CkanDataset> r = new CkanClient(DATI_TRENTINO).searchDatasets(CkanQuery.filter()
         .byText("elenco dei prodotti trentini")
         .byGroupNames("agricoltura")
         .byOrganizationName("pat-s-sviluppo-rurale")
         .byTagNames("prodotti tipici", "enogastronomia")
         .byLicenseId("cc-zero"), 10, 0);
         assertEquals("cc-zero", r.getResults().get(0).getLicenseId());
         assertTrue("I should get at least one result", r.getResults().size() > 0);
                
    }

    @Test
    public void testTimeout(){
        CkanClient myClient = CkanClient.builder()
                .setCatalogUrl(UNREACHABLE_HOST)
        .setTimeout(500)
        .build();
        
        try {
            myClient.getApiVersion();
        } catch (CkanException ex){
            
        }
        
        try {
            myClient.getDatasetList();
        } catch (CkanException ex){
            
        }
        
    }

    /**
     * Preliminary testcase for Https issues.
     * 
     * See https://github.com/opendatatrentino/jackan/issues/39
     * 
     * @since 0.4.3
     */
    @Test
    public void testHttps(){
        CkanClient client = new CkanClient("https://datahub.io", null);
        List<String> dsl = client.getDatasetList(10,0);
        assertTrue(dsl.size() > 0);       
        logger.log(Level.FINE, dsl.toString());
    }

    /**
     * @since 0.4.3
     */
    public Object[] localhosts() {
        return $(
                $("localhost"),
                $("127.0.0.1"),
                $("http://localhost")
        );
    }
    
    
    /**
     * Test cases for https://github.com/opendatatrentino/jackan/pull/12
     * @since 0.4.3
     */      
    @Test
    @Parameters(method = "localhosts")
    public void testHttpProxy(String localhost){
        int port = Tests.findFreePort();
        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                    .withPort(port)
                    .start();
        try {
            CkanClient client = CkanClient.builder()
            .setCatalogUrl(DATAHUB_IO)
            .setProxy(localhost + ":" + port)
            .build();
            
            List<String> dsl = client.getDatasetList(10,0);
            assertTrue(dsl.size() > 0);
        } finally {
            server.stop();    
        }        
    }
    
    
}
