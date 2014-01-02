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

package eu.trentorise.opendata.jackan.ckan;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Leoni
 */
public class CkanJacksonTest {
    static String DATI_TRENTINO = "http://dati.trentino.it";
    static Logger logger = LoggerFactory.getLogger(CkanJacksonTest.class);
    static String DATA_GOV_UK = "http://data.gov.uk";
    
    public CkanJacksonTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        logger.info("To see debug logging messages set \tlog4j.rootLogger=DEBUG, console\t in src/test/resources/log4j.properties");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Tests the CkanResponse wrapper
    */
    @Test
    public void testGetDatasetList() throws IOException{
        DatasetListResponse dlr = CkanClient.getObjectMapper().readValue("{\"help\":\"bla bla\", \"success\":true, \"result\":[\"a\",\"b\"]}", DatasetListResponse.class);
        assertTrue(dlr.result.size() == 2);
        assertEquals( "a",dlr.result.get(0));
        assertEquals( "b",dlr.result.get(1));
    }
    
    /**
     * Tests the ObjectMapper underscore conversion
     * @throws IOException 
     */
    @Test
    public void testRead() throws IOException{
        ObjectMapper om = CkanClient.getObjectMapper();
        String email = "a@b.org";
        String json = "{\"author_email\":\""+email+"\"}";        
        CkanDataset cd = om.readValue(json, CkanDataset.class);
        assertEquals(email, cd.getAuthorEmail());
    } 
    
    /**
     * Tests the ObjectMapper underscore conversion
     * @throws IOException 
     */
    @Test
    public void testWrite() throws IOException{
        String email = "a@b.org";        
        CkanDataset cd = new CkanDataset();
        cd.setAuthorEmail(email);
        String json = CkanClient.getObjectMapper().writeValueAsString(cd);
        assertEquals(email, new ObjectMapper().readTree(json).get("author_email").asText());        
    } 
        
    
    @Test
    public void testReadGroup() throws IOException{                
        String json = "{\"is_organization\":true}";        
        CkanGroup g = CkanClient.getObjectMapper().readValue(json, CkanGroup.class);
        assertTrue(g.isOrganization());
    }     

    @Test
    public void testWriteGroup() throws IOException{                
        
        CkanGroup cg = new CkanGroup();
        cg.setOrganization(true);
        String json = CkanClient.getObjectMapper().writeValueAsString(cg);
        assertEquals(true, new ObjectMapper().readTree(json).get("is_organization").asBoolean());    
    }     

    @Test
    public void testReadError() throws IOException{                
        String json = "{\"message\": \"a\",\"__type\":\"b\"}";        
        CkanError er = CkanError.read(json);
        assertEquals("b", er.getType());
    }  
    
    
    
  /*  @Test
    public void testDataGovUkDatasetList() throws CKANException {
        
        Connection c = new Connection(DATA_GOV_UK);
        Client cl = new Client(c, null);
        List<String> dsl = cl.getDatasetList().result;
        assertTrue(dsl.size() > 0);          
        
    } */    
/*    
    @Test
    public void testGetDataset() throws CKANException {
        Connection c = new Connection(DATI_TRENTINO);
        CkanClient cl = new CkanClient(c, null);
        CkanDataset dataset = cl.getCkanDataset("anagrafica-sensori-ufficio-dighe");
        logger.debug("dataset = " + dataset);
    }
    */
}
