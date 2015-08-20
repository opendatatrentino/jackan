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
package eu.trentorise.opendata.jackan.test.ckan;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanTag;
import eu.trentorise.opendata.jackan.model.CkanUser;
import eu.trentorise.opendata.jackan.model.CkanVocabulary;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import junitparams.JUnitParamsRunner;
import static junitparams.JUnitParamsRunner.$;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * Performs integration tests. Many tests here are also used by
 * {@link CkanTestReporter}
 *
 * @author David Leoni
 * @since 0.4.1
 */
@RunWith(JUnitParamsRunner.class)
public abstract class WriteCkanTest {

    public static final String JACKAN_URL = "http://opendatatrentino.github.io/jackan/";

    public static final String TEST_RESOURCE_ID = "81f579fe-7f10-4fa2-94f2-0011898dc78c";

    private static final Logger LOG = Logger.getLogger(WriteCkanTest.class.getName());

    protected Object[] wrongDatasetNames() {
        return $(
                $((String) null),
                $(""),
                $("   "),
                $("$$$$$"),
                $("a") // need at least 2 chars
        );
    }

    protected Object[] wrongGroupOrgNames() {
        return $(
                $((String) null),
                $(""),
                $("   "),
                $("$$$$$"),
                $("a") // need at least 2 chars
        );
    }

    protected Object[] wrongUrls() {
        return $(
                $((String) null),
                $(""),
                $("   "),
                $("http:"),
                $("http://")
        );
    }

    protected Object[] wrongIds() {
        return $(
                $(""),
                $("   "),
                $("123"));
    }

    protected CkanClient client;

    protected CkanClient datiTrentinoClient;

    @BeforeClass
    public static void setUpClass() {
        JackanTestConfig.of().loadConfig();
    }

    @Before
    public void setUp() {                        
        client = JackanTestConfig.of().makeClientInstanceForWriting();
        datiTrentinoClient = new CkanClient(ReadCkanIT.DATI_TRENTINO);
    }

    @After
    public void tearDown() {
        client = null;
    }

    public WriteCkanTest() {
    }

    protected CkanDataset createRandomDataset() {
        CkanDataset ckanDataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        return client.createDataset(ckanDataset);
    }

    protected CkanDataset createRandomDataset(int numResources) {
        CkanDataset ckanDataset = new CkanDataset("test-dataset-jackan-" + UUID.randomUUID().getMostSignificantBits());
        List<CkanResource> resources = new ArrayList();
        for (int i = 0; i < numResources; i++) {
            resources.add(new CkanResource(JACKAN_URL + "test-resources/#" + UUID.randomUUID().toString(), null));

        }
        ckanDataset.setResources(resources);

        return client.createDataset(ckanDataset);
    }

    protected CkanResource createRandomResource() {
        CkanDataset dataset = createRandomDataset();
        CkanResource resource = new CkanResource(JACKAN_URL, dataset.getId());
        return client.createResource(resource);
    }

    protected CkanOrganization createRandomOrganization() {
        CkanOrganization organization = new CkanOrganization("test-org-" + randomUUID());
        return client.createOrganization(organization);
    }

    protected CkanGroup createRandomGroup() {
        CkanGroup group = new CkanGroup("test-group-" + randomUUID());
        return client.createGroup(group);
    }

    protected CkanVocabulary createRandomVocabulary() {
        CkanVocabulary voc = new CkanVocabulary("test-vocabulary-" + randomUUID(), new ArrayList());
        return client.createVocabulary(voc);
    }
    
    protected CkanUser createRandomUser() {
        CkanUser voc = new CkanUser("test-user-" + randomUUID(), "enthusiast@jackan.org", "abracadabra");
        return client.createUser(voc);
    }
    

    protected CkanTag createRandomTag() {
        CkanVocabulary voc = createRandomVocabulary();
        CkanTag tag = new CkanTag("test-tag-" + randomUUID(), voc.getId());
        return client.createTag(tag);
    }

    protected String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
