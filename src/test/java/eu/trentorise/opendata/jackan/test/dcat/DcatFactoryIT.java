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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.dcat.DcatFactory;
import eu.trentorise.opendata.jackan.dcat.GreedyDcatFactory;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import eu.trentorise.opendata.jackan.test.ckan.FailedResourceException;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.DATI_TOSCANA;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.DATI_TRENTINO;
import static eu.trentorise.opendata.jackan.test.ckan.ReadCkanIT.TEST_ELEMENTS;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import eu.trentorise.opendata.jackan.test.JackanTestRunner;
import static junitparams.JUnitParamsRunner.$;
import junitparams.Parameters;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * To try out the factory with data in the wild
 * todo methods don't really assert much
 * @author David Leoni
 * @since 0.4.1
 */
@RunWith(JackanTestRunner.class)
public class DcatFactoryIT {

    /**
     * Object mapper for reading
     */
    private ObjectMapper objectMapper;

    private DcatFactory dcatFactory;
    private GreedyDcatFactory greedyDcatFactory;   
    
    @BeforeClass
    public static void setUpClass() {
        JackanTestConfig.of().loadConfig();

    }

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        CkanClient.configureObjectMapper(objectMapper);
        dcatFactory = new DcatFactory();
        greedyDcatFactory = new GreedyDcatFactory();
    }

    @After
    public void tearDown() {
        objectMapper = null;
        dcatFactory = null;
        greedyDcatFactory = null;
    }

    /**
     * All reading tests will be tried with all these catalogs.
     */
    public Object[] clients() {
        return $(
                $(new CkanClient(DATI_TRENTINO)),
                $(new CkanClient(DATI_TOSCANA))
        //$(new CkanClient(NOAA_GOV_US))                
        /*,
         $(new CkanClient(DATI_MATERA)),
         $(new CkanClient(DATA_GOV_UK)),
         $(new CkanClient(DATA_GOV_US)) */
        );
    }    
    
    /**
     * For now we just test the thing doesn't explode todo add better tests.
    */
    @Test
    @Parameters(method = "clients")
    public void testDatasetAndResource(CkanClient client) {
        List<String> dsl = client.getDatasetList(TEST_ELEMENTS, 0);
        assertTrue(dsl.size() > 0);

        List<FailedResourceException> failedResources = new ArrayList();

        for (String datasetName : dsl.subList(0, Math.min(dsl.size(), TEST_ELEMENTS))) {
            CkanDataset dataset = client.getDataset(datasetName);
            assertEquals(datasetName, dataset.getName());
            dcatFactory.makeDataset(dataset, client.getCatalogUrl(), Locale.ITALIAN);
            greedyDcatFactory.makeDataset(dataset, client.getCatalogUrl(), Locale.ITALIAN);
            
            for (CkanResource resource : dataset.getResources().subList(0, Math.min(dataset.getResources().size(), TEST_ELEMENTS))) {
                try {
                    CkanResource res = client.getResource(resource.getId());
                    dcatFactory.makeDistribution(res, datasetName, client.getCatalogUrl(), dataset.getId(), Locale.ITALIAN);
                    greedyDcatFactory.makeDistribution(res, datasetName, client.getCatalogUrl(), dataset.getId(), Locale.ITALIAN);                   
                }
                catch (Exception ex) {
                    failedResources.add(new FailedResourceException(client, "Error while fetching/converting resource!", datasetName, resource.getId(), ex));
                }
            }
        }

        if (!failedResources.isEmpty()) {
            throw new RuntimeException("Couldn't fetch/convert these resources: \n " + failedResources.toString());
        }
    }

}
