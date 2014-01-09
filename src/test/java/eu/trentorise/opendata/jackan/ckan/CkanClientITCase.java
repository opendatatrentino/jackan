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

import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.SearchResults;
import static eu.trentorise.opendata.jackan.ckan.CkanJacksonTest.DATI_TRENTINO;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo make this an integration test
 * @author David Leoni
 */
public class CkanClientITCase {
    static Logger logger = LoggerFactory.getLogger(CkanJacksonTest.class);
    public static final String LAGHI_MONITORATI_TRENTO_NAME = "laghi-monitorati-trento";
    public static final String LAGHI_MONITORATI_TRENTO_ID = "96b8aae4e211f3e5a70cdbcbb722264256ae2e7d";
    
    CkanClient client;
        
    @Before
    public void setUp() {                
        client = new CkanClient(DATI_TRENTINO); 
    }
    
    @After
    public void tearDown() {
        client.close();
        client = null;
    }    
    
    @Test
    public void testDatasetList()  {
  
        List<String> dsl = client.getDatasetList();
        assertTrue(dsl.size() > 0);                  
    }    
    
    @Test
    public void testDatasetListWithLimit()  {
                
     
        List<String> dsl = client.getDatasetList(1,0);
        assertEquals(1, dsl.size());                  
    }
    
   
    @Test
    public void testDataset()  {
                        
        CkanDataset dataset = client.getDataset(LAGHI_MONITORATI_TRENTO_ID);
        assertEquals(dataset.getName(),LAGHI_MONITORATI_TRENTO_NAME);
    }
    
    
    
    @Test
    public void testUserList(){                     
        ArrayList<CkanUser> ul = client.getUserList();
        assertTrue(ul.size() > 0);    
    }

    @Test
    public void testUser(){
        CkanUser u = client.getUser("admin");
        assertEquals(u.getName(), "admin");    
    }
            
    
    @Test
    public void testGroupList(){                     
        ArrayList<CkanGroup> gl = client.getGroupList();
        assertTrue(gl.size() > 0);    
    }

    @Test
    public void testGroup(){                     
        CkanGroup g = client.getGroup("gestione-del-territorio");
        assertEquals(g.getName(), "gestione-del-territorio");
    }

    @Test
    public void testTagList(){                     
        ArrayList<CkanTag> tl = client.getTagList();
        assertTrue(tl.size() > 0);    
    }

    @Test
    public void testTagNamesList(){                     
        ArrayList<String> tl = client.getTagNamesList("serviz");
        assertTrue(tl.size() > 0);
        assertTrue(tl.get(0).toLowerCase().contains("serviz"));
    }
    
    
    
    @Test
    public void testSearchDatasets(){
        SearchResults<CkanDataset> r = client.searchDatasets("laghi", 10, 0);
        assertTrue(r.getCount() > 0);
        assertTrue("I should get at least one result", r.getResults().size() > 0 );
    }    
    
    
    
    @Test
    public void testCkanError(){
        try {
            CkanDataset dataset = client.getDataset("666");
            fail();
        } catch (JackanException ex){
            
        }
        
    }
}
