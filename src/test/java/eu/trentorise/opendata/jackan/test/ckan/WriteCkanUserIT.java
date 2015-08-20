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

import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.jackan.model.CkanUser;
import eu.trentorise.opendata.jackan.model.CkanUserBase;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author David Leoni
 * @since 0.4.1
 */
public class WriteCkanUserIT extends WriteCkanTest {

    private static final Logger LOG = Logger.getLogger(WriteCkanUserIT.class.getName());

    @Test
    public void testCreateMinimal() {

        CkanUserBase user = new CkanUserBase("test-user-" + randomUUID(), "a@b.c", "abcd");

        CkanUser retUser = client.createUser(user);

        checkNotEmpty(retUser.getId(), "Invalid created user id!");
        assertEquals(user.getName(), retUser.getName());
        assertEquals(user.getEmail(), retUser.getEmail());         
        assertEquals(null, retUser.getPassword()); 

        LOG.log(Level.INFO, "Created user with id {0} in catalog {1}", new Object[]{retUser.getId(), JackanTestConfig.of().getOutputCkan()});
    }

 }
