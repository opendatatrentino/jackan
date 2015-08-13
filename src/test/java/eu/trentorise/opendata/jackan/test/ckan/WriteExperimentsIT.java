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

import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class WriteExperimentsIT extends WriteCkanTest {

    private static final Logger LOG = Logger.getLogger(WriteExperimentsIT.class.getName());

    @Test
    @Ignore
    public void testUploadFile() {
        String ret = ExperimentalCkanClient.of(client).uploadFile("abc", "jackan-test-file-" + randomUUID());
        LOG.fine("Uploaded resource " + ret);
    }
}
