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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.trentorise.opendata.jackan.test.TestConfig;
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
 * White box testing
 * @author David Leoni, Ivan Tankoyeu
 */
public class CkanJacksonTest {
    public static Logger logger = Logger.getLogger(CkanJacksonTest.class.getName());



    public CkanJacksonTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        TestConfig.initLogger();
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
    public void testWrite() throws IOException {
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
     * Tests the 'others' field that collects fields sometimes errouneously present in jsons from ckan
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










}



