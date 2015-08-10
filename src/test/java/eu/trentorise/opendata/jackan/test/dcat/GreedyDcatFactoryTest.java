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
package eu.trentorise.opendata.jackan.test.dcat;

import com.google.common.collect.Lists;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanGroup;
import eu.trentorise.opendata.jackan.ckan.CkanPair;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.dcat.GreedyDcatFactory;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import eu.trentorise.opendata.traceprov.dcat.DcatDataset;
import eu.trentorise.opendata.traceprov.dcat.DcatDistribution;
import java.util.Locale;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class GreedyDcatFactoryTest {
     private static final String CATALOG_URL = "https://github.com/opendatatrentino/jackan";

    @BeforeClass
    public static void setUpClass() {
        JackanTestConfig.of().loadConfig();
    }

    private static String makeUuid(int i) {
        if (i < 0 || i > 255){
            throw new RuntimeException("Provided integer must be 0 <= i < 255");
        }
        byte[] bs = {(byte) i};
        return UUID.nameUUIDFromBytes(bs).toString();
    }

    @Test
    public void testDistribution() {
        CkanResource res = new CkanResource();

        DcatDistribution distribution = GreedyDcatFactory.of().distribution(
                res,
                CATALOG_URL,
                makeUuid(1),
                "cc-zero",
                Locale.ITALIAN);                        

    }

    @Test
    public void testDatasetOthers() {
        CkanDataset dataset = new CkanDataset();
        dataset.putOthers("language", "[\"it\"]");

        DcatDataset dcatDataset = GreedyDcatFactory.of().dataset(dataset, CATALOG_URL, Locale.ENGLISH);
        assertEquals(1, dcatDataset.getLanguages().size());
        assertEquals(Locale.ITALIAN, dcatDataset.getLanguages().get(0));
    }

    @Test
    public void testDatasetExtras() {
        CkanDataset dataset = new CkanDataset();
        dataset.setExtras(Lists.newArrayList(new CkanPair("language", "[\"it\"]")));

        DcatDataset dcatDataset = GreedyDcatFactory.of().dataset(dataset, CATALOG_URL, Locale.ENGLISH);
        assertEquals(1, dcatDataset.getLanguages().size());
        assertEquals(Locale.ITALIAN, dcatDataset.getLanguages().get(0));
    }
    
    @Test
    public void testDatasetWithResource(){
        CkanDataset dataset = new CkanDataset();
        dataset.setId(makeUuid(1));
        dataset.setResources(Lists.newArrayList(new CkanResource(CATALOG_URL, null)));

        DcatDataset dcatDataset = GreedyDcatFactory.of().dataset(dataset, CATALOG_URL, Locale.ENGLISH);
        
        assertEquals(1, dcatDataset.getDistributions().size());
        assertEquals(CATALOG_URL, dcatDataset.getDistributions().get(0).getAccessURL());
    }
    
    @Test
    public void testDatasetThemes(){
        CkanDataset dataset = new CkanDataset();
        dataset.setId(makeUuid(1));
        CkanGroup group = new CkanGroup();
        group.setTitle("xyz");        
        group.setName("abc");
        dataset.setGroups(Lists.newArrayList(group));
        
        DcatDataset dcatDataset = GreedyDcatFactory.of().dataset(dataset, CATALOG_URL, Locale.ENGLISH);
        
        assertEquals(1, dcatDataset.getThemes().size());
        assertEquals(Dict.of(Locale.ENGLISH, "xyz"), dcatDataset.getThemes().get(0).getPrefLabel());
    }
}
