/**
 * *****************************************************************************
 * Copyright 2013-2014 Trento Rise (www.trentorise.eu/)
 * 
* All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License (LGPL)
 * version 2.1 which accompanies this distribution, and is available at
 * 
* http://www.gnu.org/licenses/lgpl-2.1.html
 * 
* This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
*******************************************************************************
 */
package eu.trentorise.opendata.jackan.test.ckan;

import eu.trentorise.opendata.jackan.Configuration;
import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.SearchResults;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanDatasetMinimized;
import eu.trentorise.opendata.jackan.ckan.CkanGroup;
import eu.trentorise.opendata.jackan.ckan.CkanJacksonTest;
import eu.trentorise.opendata.jackan.ckan.CkanPair;
import eu.trentorise.opendata.jackan.ckan.CkanQuery;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.ckan.CkanResourceMinimized;
import eu.trentorise.opendata.jackan.ckan.CkanTag;
import eu.trentorise.opendata.jackan.ckan.CkanUser;
import eu.trentorise.opendata.jackan.test.TestConfig;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.fail;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 *
 * @author David Leoni
 */
public class CkanClientITCase {

    /**
     * todo store it somewhere
     */
    private static final String TEST_CATALOG = "http://192.168.213.196:8001";
    private static final String TEST_TOKEN = "b7592183-56cc-479a-9c52-5b1cb84db9db";
    private static final String TEST_RESOURCE_ID="1aff9c7a-895a-4c12-b02b-e0f9548afc90";    
    
    public static Logger logger = Logger.getLogger(CkanClientITCase.class.getName());
    static String DATI_TRENTINO = "http://dati.trentino.it";
    static String DATA_GOV_UK = "http://data.gov.uk";
    public static final String LAGHI_MONITORATI_TRENTO_NAME = "laghi-monitorati-trento-143675";
    public static final String LAGHI_MONITORATI_TRENTO_ID = "3745b44c-751f-40b3-8e97-ccd725bfbe8a";
    public static final String LAGHI_MONITORATI_TRENTO_XML_RESOURCE_NAME = "Metadati in formato XML";

    CkanClient client;
    
    @BeforeClass
    public static void setUpClass() {        
        TestConfig.initLogger();
    }
    
    @Before
    public void setUp() {
        client = new CkanClient(DATI_TRENTINO);
    }

    @After
    public void tearDown() {
        client = null;
    }

    @Test
    public void testDatasetList() {

        List<String> dsl = client.getDatasetList();
        assertTrue(dsl.size() > 0);
    }

    /**
     * Ckan docs don't tell offset starts with 0
     */
    @Test
    public void testDatasetListWithLimit() {

        List<String> dsl = client.getDatasetList(1, 0);
        assertEquals(1, dsl.size());
    }

    @Test
    public void testDataset() {

        CkanDataset dataset = client.getDataset(LAGHI_MONITORATI_TRENTO_ID);
        assertEquals(LAGHI_MONITORATI_TRENTO_NAME, dataset.getName());
    }

    //@Test
//    public void testResourceInsideDataset() {
//        CkanDataset dataset = client.getDataset(LAGHI_MONITORATI_TRENTO_ID);
//        List<CkanResource> resources = dataset.getResources();
//        for (CkanResource r : resources) {
//            if (r.getFormat().equals("XML")) {
//                assertEquals(LAGHI_MONITORATI_TRENTO_XML_RESOURCE_NAME, resources.get(0).getName());
//                return;
//            }
//        }
//        fail("Couldn't find xml resource in " + LAGHI_MONITORATI_TRENTO_NAME + " dataset");
//
//    }
    
    @Test
    public void testUserList() {
        List<CkanUser> ul = client.getUserList();
        assertTrue(ul.size() > 0);
    }

    @Test
    public void testUser() {
        CkanUser u = client.getUser("admin");
        assertEquals("admin", u.getName());
    }

    @Test
    public void testGroupList() {
        List<CkanGroup> gl = client.getGroupList();
        assertTrue(gl.size() > 0);
    }

    @Test
    public void testGroup() {
        CkanGroup g = client.getGroup("gestione-del-territorio");
        assertEquals("gestione-del-territorio", g.getName());
    }

    @Test
    public void testOrganizationList() {
        List<CkanGroup> gl = client.getOrganizationList();
        assertTrue(gl.size() > 0);
    }

    @Test
    public void testOrganization() {
        CkanGroup g = client.getOrganization("comune-di-trento");
        assertEquals("comune-di-trento", g.getName());
    }

    @Test
    public void testTagList() {
        List<CkanTag> tl = client.getTagList();
        assertTrue(tl.size() > 0);
    }

    @Test
    public void testTagNamesList() {
        List<String> tl = client.getTagNamesList("serviz");

        assertTrue(tl.get(0).toLowerCase().contains("serviz"));
    }

    @Test
    public void testSearchDatasetsByText() {
        SearchResults<CkanDataset> r = client.searchDatasets("laghi", 10, 0);

        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    public void testSearchDatasetsByGroups() {
        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byGroupNames("gestione-del-territorio"), 10, 0);

        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    public void testSearchDatasetsByOrganization() {
        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byOrganizationName("pat-sistema-informativo-ambiente-e-territorio"), 10, 0);
        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    public void testSearchDatasetsByTags() {
        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byTagNames("strati prioritari", "cisis"), 10, 0);
        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    public void testSearchDatasetsByLicenseIds() {
        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter().byLicenseId("cc-zero"), 10, 0);
        assertEquals("cc-zero", r.getResults().get(0).getLicenseId());
        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    public void testFullSearch() {
        SearchResults<CkanDataset> r = client.searchDatasets(CkanQuery.filter()
                .byText("viabilità ferroviaria")
                .byGroupNames("gestione-del-territorio")
                .byOrganizationName("pat-sistema-informativo-ambiente-e-territorio")
                .byTagNames("strati prioritari", "cisis")
                .byLicenseId("cc-zero"), 10, 0);
        assertEquals("cc-zero", r.getResults().get(0).getLicenseId());
        assertTrue("I should get at least one result", r.getResults().size() > 0);
    }

    @Test
    public void testCkanError() {
        try {
            CkanDataset dataset = client.getDataset("666");
            fail();
        } catch (JackanException ex) {

        }

    }

    @Test
    public void testCreateDataSet() throws URISyntaxException {

        CkanClient cClient = new CkanClient(TEST_CATALOG, TEST_TOKEN);

        CkanPair ckanPair = new CkanPair();
        ckanPair.setKey("test key");
        ckanPair.setValue("test value");
        List<CkanPair> extras = new ArrayList<CkanPair>();
        extras.add(ckanPair);

        String uri = "http://github.com/opendatatrentino/Jackan";

        long uuid = UUID.randomUUID().getMostSignificantBits();

        String datasetName = "test-dataset-jackan-" + uuid;

        CkanDatasetMinimized ckanDataset = new CkanDatasetMinimized(datasetName, uri, extras, "Test Jackan Dataset " + uuid, "cc-zero");

        CkanDataset retDataset = cClient.createDataset(ckanDataset);

        assertNotNull(retDataset.getId());
        assertTrue(retDataset.getId().length() > 0);
        logger.info("created dataset with id " + retDataset.getId() + " in catalog " + TEST_CATALOG);
    }

    @Test
    public void testCreateResource() throws URISyntaxException {

        CkanClient cClient = new CkanClient(TEST_CATALOG, TEST_TOKEN);

        String uri = "http://github.com/opendatatrentino/jackan";

        long uuidDataset = UUID.randomUUID().getMostSignificantBits();
        String datasetName = "test-dataset-jackan-" + uuidDataset;

        CkanDatasetMinimized ckanDataset = new CkanDatasetMinimized(datasetName, uri, new ArrayList<CkanPair>(), "Test Jackan Dataset " + uuidDataset, "cc-zero");
        CkanDataset retDataset = cClient.createDataset(ckanDataset);

        CkanResourceMinimized ckanResource = new CkanResourceMinimized("JSONLD", "Jackan test resource " + UUID.randomUUID().getMostSignificantBits() + uuidDataset,
                uri,
                "Most interesting test resource in the universe",
                retDataset.getId(),
                null);

        CkanResource retCkanRes = cClient.createResource(ckanResource);

        assertNotNull(retCkanRes.getId());
        assertTrue(retCkanRes.getId().length() > 0);
        logger.info("Created resource with id " + retCkanRes.getId() + " in catalog " + TEST_CATALOG);

    }

    /**
     * todo review this!!!
     * @throws URISyntaxException 
     */
    @Test
    @Ignore
    public void testUpdateResource() throws URISyntaxException {
        CkanClient cClient = new CkanClient(TEST_CATALOG, TEST_TOKEN);

        URI uri = null;

        uri = new URI("http", "www.unitn.it", null, null);

        CkanResourceMinimized ckanResource = new CkanResourceMinimized("JSONLD", "ivanresource2", uri.toASCIIString(), "test resource", "07dfd366-2107-4c06-97f5-2acdeff49aff", null);

        ckanResource.setId(TEST_RESOURCE_ID);
        CkanResource cResource = cClient.updateResource(ckanResource);
        logger.log(Level.INFO, "Ckan Resource URL changed:{0}", cResource.getUrl());

    }
}
