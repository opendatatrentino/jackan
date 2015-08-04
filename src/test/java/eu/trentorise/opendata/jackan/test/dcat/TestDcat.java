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
package eu.trentorise.opendata.jackan.test.dcat;

import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.dcat.DcatFactory;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import eu.trentorise.opendata.traceprov.dcat.DcatDataset;
import eu.trentorise.opendata.traceprov.dcat.DcatDistribution;
import java.util.Locale;
import java.util.UUID;

import junitparams.JUnitParamsRunner;
import org.junit.BeforeClass;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Performs integration tests. Many tests here are also used by
 * {@link CkanTestReporter}
 *
 * @author David Leoni
 */
@RunWith(JUnitParamsRunner.class)
public class TestDcat {

    private static final String CATALOG_URL = "https://github.com/opendatatrentino/jackan";   
    
    @BeforeClass
    public static void setUpClass() {
        JackanTestConfig.of().loadConfig();
    }
    
    private static String makeUuid(int i){
        byte[] bs = {(byte)i};
        return UUID.nameUUIDFromBytes(bs).toString();
    }    

    @Test
    public void testDistribution() {
        CkanResource res = new CkanResource();
        
        DcatDistribution distribution = DcatFactory.distribution(
                res,
                CATALOG_URL,
                makeUuid(1), "cc-zero", Locale.ITALIAN);

    }

    @Test
    public void testDataset() {
        CkanDataset dataset = new CkanDataset();
        
        DcatDataset dcatDataset = DcatFactory.dataset(dataset,CATALOG_URL, Locale.ITALIAN);

    }
}
