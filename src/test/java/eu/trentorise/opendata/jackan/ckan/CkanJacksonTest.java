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
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import java.io.*;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

    public static final Logger logger = Logger.getLogger(CkanJacksonTest.class.getName());

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

    /**
     * @throws IOException
     */
    @Test
    public void testReadNullString() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
        String json = "{\"size\":null}";
        CkanResource r = om.readValue(json, CkanResource.class);

        assertTrue(r.getSize() == null);
    }

    /**
     * @throws IOException
     */
    @Test
    public void testReadEmptyString() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
        String json = "{\"size\":\"\"}";
        CkanResource r = om.readValue(json, CkanResource.class);

        assertTrue(r.getSize().equals(""));
    }

    /**
     * Tests the ObjectMapper underscore conversion
     *
     * @throws IOException
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

    static public class JodaA {

        private DateTime dt;

        public DateTime getDt() {
            return dt;
        }

        public void setDt(DateTime dt) {
            this.dt = dt;
        }                

    }
    

    @Test
    public void testJoda_1() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
        JodaA ja = new JodaA();

        ja.setDt(new DateTime(123, DateTimeZone.UTC));
        String json = om.writeValueAsString(ja);
        //logger.debug("json = " + json);
        // todo Since we are using Joda jackson is not respecting the date format config without the 'Z' we set in the object mapper.        
        // see https://github.com/opendatatrentino/Jackan/issues/1
        assertEquals("1970-01-01T00:00:00.123", om.readTree(json).get("dt").asText());

        JodaA ja2 = om.readValue(json, JodaA.class);
        //logger.debug("ja = " + ja.getDt().toString());
        //logger.debug("ja2 = " + ja2.getDt().toString());
        assertTrue(ja.getDt().equals(ja2.getDt()));
    }

    /**
     * Sometimes dear ckan returns "None" instead of proper JSON null. This
     * tests the "None" to null conversion for dates.
     */
    @Test
    public void testJodaNone() throws IOException {
        ObjectMapper om = CkanClient.getObjectMapperClone();
                       
        JodaA nullJa = om.readValue("{\"dt\":\"None\"}", JodaA.class);
        assertNull(nullJa.getDt());

        JodaA nonNullJa = om.readValue("{\"dt\":\"1970-01-01T00:00:00.123\"}", JodaA.class);                
        assertNotNull(nonNullJa.getDt());
        
    }

}
