/*
 * Copyright 2015 Trento Rise.
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

import eu.trentorise.opendata.commons.BuildInfo;
import eu.trentorise.opendata.commons.TodConfig;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.DATI_TRENTINO;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Non-ckan related tests and test utilities
 * 
 * @author David Leoni
 */
public class Tests {

    @BeforeClass
    public static void setUpClass() {
        TodConfig.loadLogConfig(Tests.class);
    }
    

    @Test
    public void testBuildInfo() {
        BuildInfo buildInfo = TodConfig.of(JackanTestConfig.class).getBuildInfo();
        assertTrue(buildInfo.getVersion().length() > 0);
        assertTrue(buildInfo.getScmUrl().length() > 0);
    }
    
    @Test
    public void testRandom(){
        assertNotEquals(new Random().nextLong(), new Random().nextLong());
    }    
    

    
    
    @Test
    public void testBuilder(){
        
        String myProxy = "http://a.b:123";
        
        assertEquals(DATI_TRENTINO,
                     CkanClient.builder()
                         .setCatalogUrl(DATI_TRENTINO)
                         .build()
                         .getCatalogUrl());
        
        assertEquals(myProxy,
                CkanClient.builder()
                    .setCatalogUrl(DATI_TRENTINO)
                    .setProxy(myProxy)
                    .build()
                    .getProxy());
        
        assertEquals("http://a.b:123",
                CkanClient.builder()
                    .setCatalogUrl(DATI_TRENTINO)
                    .setProxy("http://a.b:123 ")
                    .build()
                    .getProxy());
        
        try {
            CkanClient.builder()
            .setCatalogUrl(DATI_TRENTINO)
            .setProxy("http://a.b/")            
            .build();
            Assert.fail("shouldn't arrive here!");
        } catch (IllegalArgumentException ex){           
        }
        
        try {
            CkanClient.builder()
            .setCatalogUrl(DATI_TRENTINO)
            .setProxy("http://a.b/c")            
            .build();
            Assert.fail("shouldn't arrive here!");
        } catch (IllegalArgumentException ex){           
        }
        
        
        assertEquals(1,
                CkanClient.builder()
                    .setCatalogUrl(DATI_TRENTINO)
                    .setTimeout(1)
                    .build()
                    .getTimeout());
        
        try {
            CkanClient.builder()
            .setCatalogUrl(DATI_TRENTINO)
            .setTimeout(0)            
            .build();
            Assert.fail("shouldn't arrive here!");
        } catch (Exception ex){           
        }
        
        try {
            CkanClient.builder().build();
            Assert.fail("shouldn't arrive here!");
        } catch (Exception ex){           
        }
               
    }
    
    @Test
    public void testMakeResourceUrl(){
        String catalogUrl = "http://dati.trentino.it";
        String datasetName = "impianti-di-risalita-vivifiemme-2013";
        String resourceId = "779d1d9d-9370-47f4-a194-1b0328c32f02";
        
        assertEquals("http://dati.trentino.it/dataset/impianti-di-risalita-vivifiemme-2013/resource/779d1d9d-9370-47f4-a194-1b0328c32f02",
                CkanClient.makeResourceUrl(catalogUrl, datasetName, resourceId));        
    }

    @Test
    public void testMakeDatasetUrl(){
        
        assertEquals("http://dati.trentino.it/dataset/impianti-di-risalita-vivifiemme-2013",
                CkanClient.makeDatasetUrl(DATI_TRENTINO, "impianti-di-risalita-vivifiemme-2013"));        
    }
    
    @Test
    public void testMakeGroupUrl(){
        assertEquals("http://dati.trentino.it/group/gestione-del-territorio",
                CkanClient.makeGroupUrl(DATI_TRENTINO, "gestione-del-territorio"));        
    }
    
    @Test
    public void testMakeOrganizationUrl(){                      
        assertEquals("http://dati.trentino.it/organization/comune-di-trento",
                CkanClient.makeOrganizationUrl(DATI_TRENTINO, "comune-di-trento"));        
    }

    /**
     * Checks {@code map1} is included in {@code map2}
     * 
     * @since 0.4.3
     */
    public static void checkIsIncluded(@Nullable Map map1, 
                                       @Nullable Map map2){
        
        assertTrue("map1 is " + map1 + " while map2 is " + map2,!( map1 == null ^ map2 == null));
        
        for (Object key : map1.keySet()){
            assertTrue("map2 should doesn't key " + key, map2.containsKey(key));
            assertEquals(map1.get(key), map2.get(key));
        }
    }
}
