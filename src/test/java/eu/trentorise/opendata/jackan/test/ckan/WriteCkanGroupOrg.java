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

import com.google.common.collect.Lists;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroupOrg;
import eu.trentorise.opendata.jackan.model.CkanUser;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import junitparams.Parameters;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Abstract class with common tests for both CkanOrganization and CkanGroup.
 *
 * IMPORTANT: OVERRIDDEN METHODS IN DESCENDANTS MUST COPY annotations @Test AND
 * EVENTUAL JUnitParams TAGS
 *
 * @author David Leoni
 * @since 0.4.1
 */
abstract class WriteCkanGroupOrg<T extends CkanGroupOrg> extends WriteCkanTest {

    private static final Logger LOG = Logger.getLogger(WriteCkanGroupOrg.class.getName());

    public WriteCkanGroupOrg() {
        super();
    }

    protected abstract T newRandom();

    protected abstract T createRandom();

    protected abstract T create(T groupOrg);

    protected abstract T newEmpty();

    protected abstract T newName(String name);

    protected abstract T getGroupOrg(CkanClient client, String nameOrId);

    protected abstract String getExistingDatiTrentinoGroupOrgName();

    protected String className() {
        return newEmpty().getClass().getSimpleName();
    }

    @Test
    public void testCreateMinimal() {
        T groupOrg = newRandom();

        T retGroupOrg = create(groupOrg);

        assertEquals(groupOrg.getName(), retGroupOrg.getName());
        LOG.log(Level.INFO, "created " + className() + " with id {0} in catalog {1}", new Object[]{retGroupOrg.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    @Test
    public void testCreateById() {
        T groupOrg = newEmpty();
        groupOrg.setId(UUID.randomUUID().toString());
        groupOrg.setName("jackan-test-org-" + groupOrg.getId());
        T retGroupOrg = create(groupOrg);
        assertEquals(groupOrg.getId(), retGroupOrg.getId());
        assertEquals(groupOrg.getName(), retGroupOrg.getName());
    }

    @Test
    public void testCreateWithPackages() {
        T groupOrg = newName("jackan-test-org-" + UUID.randomUUID().getMostSignificantBits());
        CkanDataset dataset = createRandomDataset();
        groupOrg.setPackages(Lists.newArrayList(dataset));
        T retGroupOrg = create(groupOrg);
        CkanDataset retDataset = client.getDataset(dataset.getId());
    }

    @Test
    public void testCreateWithDatasetsWithoutId() {
        T org = newName("test-org-" + UUID.randomUUID().getMostSignificantBits());
        CkanDataset dataset = new CkanDataset("jackan-test-dataset-" + UUID.randomUUID().toString());
        org.setPackages(Lists.newArrayList(dataset));
        try {
            T retGroupOrg = create(org);
            Assert.fail("Shouldn't be possible to create an " + className() + " with datasets withour ids!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithUser() {
        T groupOrg = newName("jackan-test-org-" + UUID.randomUUID().getMostSignificantBits());

        CkanUser user = createRandomUser();

        groupOrg.setUsers(Lists.newArrayList(user));

        T retGroupOrg = create(groupOrg);

        assertEquals(2, retGroupOrg.getUsers().size());
        boolean found = false;
        for (CkanUser u : retGroupOrg.getUsers()) {
            if (user.getId().equals(u.getId())) {
                found = true;
            }
        }
        if (!found) {
            Assert.fail();
        }

    }

    @Test
    public void testCreateWithNonExistingPackages() {
        T groupOrg = newName("jackan-test-org-" + UUID.randomUUID().getMostSignificantBits());
        CkanDataset dataset = new CkanDataset("jackan-test-dataset-" + UUID.randomUUID().toString());
        dataset.setId(UUID.randomUUID().toString());
        groupOrg.setPackages(Lists.newArrayList(dataset));
        try {
            T retGroupOrg = create(groupOrg);
            Assert.fail("Shouldn't be possible to create an " + className() + " with nonexisting datasets!");
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateMirror() {

        T groupOrg = getGroupOrg(datiTrentinoClient, getExistingDatiTrentinoGroupOrgName());

        groupOrg.setName(getExistingDatiTrentinoGroupOrgName() + "-" + UUID.randomUUID().getMostSignificantBits());
        groupOrg.setId(null);

        T retGroupOrg = create(groupOrg);

        checkNotEmpty(retGroupOrg.getId(), "Invalid " + className() + " id!");

        LOG.log(Level.INFO, "created " + className() + " with id {0} in catalog {1}", new Object[]{retGroupOrg.getId(), JackanTestConfig.of().getOutputCkan()});
    }

    @Test
    @Parameters(method = "wrongGroupOrgNames")
    public void testCreateWithWrongName(String groupOrgName) {

        try {
            T groupOrg = newName(groupOrgName);
            create(groupOrg);
            Assert.fail("Shouldn't be able to create " + className() + " with wrong name " + groupOrgName);
        }
        catch (JackanException ex) {

        }
    }

    @Test
    @Parameters(method = "wrongIds")
    public void testCreateWithWrongId(String id) {

        try {
            T groupOrg = newName("jackan-test-group-" + randomUUID());
            groupOrg.setId(id);
            create(groupOrg);
            Assert.fail("Shouldn't be able to create " + className() + " with wrong id " + id);
        }
        catch (JackanException ex) {

        }
    }

    @Test
    public void testCreateWithDuplicateName() {

        String name = "jackan-test-grouporg-jackan-" + UUID.randomUUID().getMostSignificantBits();

        T groupOrg = newName(name);
        create(groupOrg);
        groupOrg.setId(null);
        try {
            create(groupOrg);
            Assert.fail("Shouldn't be able to create " + className() + " with same name " + name);
        }
        catch (JackanException ex) {
            LOG.fine("");
        }
    }

    @Test
    public void testCreateWithDuplicateId() {

        T groupOrg = newEmpty();
        groupOrg.setId(randomUUID());
        groupOrg.setName("jackan-test-grouporg-" + groupOrg.getId());
        T retGroupOrg1 = create(groupOrg);
        assertEquals(retGroupOrg1.getId(), groupOrg.getId());
        retGroupOrg1.setName("jackan-test-grouporg-" + randomUUID());
        try {
            T retGroupOrg2 = create(retGroupOrg1);
            assertEquals(retGroupOrg1.getId(), retGroupOrg2.getId());
            Assert.fail("Shouldn't be able to create " + className() + " with same id " + groupOrg.getId());
        }
        catch (JackanException ex) {

        }
    }

}
