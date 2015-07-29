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
package eu.trentorise.opendata.jackan.ckan;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.trentorise.opendata.commons.OdtConfig;
import java.io.*;
import java.util.Date;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * White box testing (uses same package as CkanClient)
 *
 * @author David Leoni, Ivan Tankoyeu
 */
public class CkanJacksonTest {

    private static final Logger LOG = Logger.getLogger(CkanJacksonTest.class.getName());

    public CkanJacksonTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        OdtConfig.loadLogConfig(CkanJacksonTest.class);
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
    public void testGetDatasetList() throws IOException {
        DatasetListResponse dlr = CkanClient.getObjectMapperClone().readValue("{\"help\":\"bla bla\", \"success\":true, \"result\":[\"a\",\"b\"]}", DatasetListResponse.class);
        assertTrue(dlr.result.size() == 2);
        assertEquals("a", dlr.result.get(0));
        assertEquals("b", dlr.result.get(1));
    }

    @Test
    public void testReadError() throws IOException {
        String json = "{\"message\": \"a\",\"__type\":\"b\"}";
        CkanError er = CkanError.read(json);
        assertEquals("b", er.getType());
    }

    /**
     * Tests the ObjectMapper underscore conversion
     *
     * @throws IOException
     */
    @Test
    public void testRead() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
        String email = "a@b.org";
        String json = "{\"author_email\":\"" + email + "\"}";
        CkanDataset cd = om.readValue(json, CkanDataset.class);
        assertEquals(email, cd.getAuthorEmail());
    }

    @Test
    public void testReadNullString() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
        String json = "{\"size\":null}";
        CkanResource r = om.readValue(json, CkanResource.class);

        assertTrue(r.getSize() == null);
    }

    @Test
    @SuppressWarnings("null")
    public void testReadEmptyString() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
        String json = "{\"size\":\"\"}";
        CkanResource r = om.readValue(json, CkanResource.class);

        assertTrue(r.getSize().equals(""));
    }

    static public class DateWrapper {

        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

    }

    @Test
    public void testDateWithMillisecs() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
        

        String json = "{\"date\":\"1970-01-01T00:00:00.000123\"}";        
        LOG.fine(json);
        DateWrapper dw = om.readValue(json, DateWrapper.class);
        String newJson = om.writeValueAsString(dw);        
        assertEquals("1970-01-01T00:00:00.000123", om.readTree(newJson).get("date").asText());

        DateWrapper dw2 = om.readValue(json, DateWrapper.class);
        
    }
    
        @Test
    public void testDateNoMillisecs() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
        String json = "{\"date\":\"2013-12-17T00:00:00\"}";                
        DateWrapper dw = om.readValue(json, DateWrapper.class);        
    }


    /**
     * Sometimes dear ckan returns "None" instead of proper JSON null. This
     * tests the "None" to null conversion for dates.
     */
    @Test
    public void testDateNone() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();

        DateWrapper nullJa = om.readValue("{\"date\":\"None\"}", DateWrapper.class);
        assertNull(nullJa.getDate());

        DateWrapper nonNullWrapper = om.readValue("{\"date\":\"1970-01-01T00:00:00.123\"}", DateWrapper.class);
        assertNotNull(nonNullWrapper.getDate());

    }


    /**
     * Tests the ObjectMapper underscore conversion
     */
    @Test
    public void testDatasetWrite() throws IOException {
        String email = "a@b.org";
        CkanDataset cd = new CkanDataset();
        cd.setAuthorEmail(email);
        String json = CkanClient.getObjectMapperClone().writeValueAsString(cd);
        assertEquals(email, new ObjectMapper().readTree(json).get("author_email").asText());
    }

    @Test
    public void testReadGroup() throws IOException {
        String json = "{\"is_organization\":true}";
        CkanGroup g = CkanClient.getObjectMapperClone().readValue(json, CkanGroup.class);
        assertTrue(g.isOrganization());
    }

    @Test
    public void testWriteGroup() throws IOException {

        CkanGroup cg = new CkanGroup();
        cg.setOrganization(true);
        String json = CkanClient.getObjectMapperClone().writeValueAsString(cg);
        assertEquals(true, new ObjectMapper().readTree(json).get("is_organization").asBoolean());
    }

    /**
     * Tests the 'others' field that collects fields sometimes errouneously
     * present in jsons from ckan
     *
     * @throws IOException
     */
    @Test
    public void testOthers() throws IOException {
        String json = "{\"name\":\"n\",\"z\":1}";
        CkanDataset cd = CkanClient.getObjectMapperClone().readValue(json, CkanDataset.class);
        assertEquals("n", cd.getName());
        assertEquals(1, cd.getOthers().get("z"));
    }

}
