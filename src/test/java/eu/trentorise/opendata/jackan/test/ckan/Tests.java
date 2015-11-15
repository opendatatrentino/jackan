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

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Non-ckan related tests.
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
        
        assertEquals(ReadCkanIT.DATI_TRENTINO,
                     CkanClient.builder()
                         .setCatalogUrl(ReadCkanIT.DATI_TRENTINO)
                         .build()
                         .getCatalogUrl());
        
        assertEquals(myProxy,
                CkanClient.builder()
                    .setCatalogUrl(ReadCkanIT.DATI_TRENTINO)
                    .setProxy(myProxy)
                    .build()
                    .getProxy());
        
        assertEquals("http://a.b:123",
                CkanClient.builder()
                    .setCatalogUrl(ReadCkanIT.DATI_TRENTINO)
                    .setProxy("http://a.b:123 ")
                    .build()
                    .getProxy());
        
        try {
            CkanClient.builder()
            .setCatalogUrl(ReadCkanIT.DATI_TRENTINO)
            .setProxy("http://a.b/")            
            .build();
            Assert.fail("shouldn't arrive here!");
        } catch (IllegalArgumentException ex){           
        }
        
        try {
            CkanClient.builder()
            .setCatalogUrl(ReadCkanIT.DATI_TRENTINO)
            .setProxy("http://a.b/c")            
            .build();
            Assert.fail("shouldn't arrive here!");
        } catch (IllegalArgumentException ex){           
        }
        
        
        assertEquals(1,
                CkanClient.builder()
                    .setCatalogUrl(ReadCkanIT.DATI_TRENTINO)
                    .setTimeout(1)
                    .build()
                    .getTimeout());
        
        try {
            CkanClient.builder()
            .setCatalogUrl(ReadCkanIT.DATI_TRENTINO)
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
}
