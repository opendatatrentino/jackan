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
package eu.trentorise.opendata.jackan.test;

import eu.trentorise.opendata.jackan.Config;
import java.io.IOException;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author David Leoni
 */
public class TestConfig {

    static public String JACKAN_TEST_PROPERTIES = "conf/jackan-test.properties";

    static private String OUTPUT_CKAN_PROPERTY = "jackan.test.ckan.output";
    static private String OUTPUT_CKAN_TOKEN_PROPERTY = "jackan.test.ckan.output-token";

    private static Properties properties;

    /**
     * Ckan used for tests which require writing
     */
    static private String outputCkan;

    /**
     * Token for CKAN like "b7592183-53c4-57da-wq52-5b1cb84db9db"
     */
    static private String outputCkanToken;

    /**
     * @throws IllegalStateException if {@link #initLogger()} didn't succeed.
     */
    static public String getOutputCkan() {
        if (initialized) {
            return outputCkan;
        } else {
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
        }
    }

    /**
     * @throws IllegalStateException if {@link #initLogger()} didn't succeed.
     */
    static public String getOutputCkanToken() {
        if (initialized) {
            return outputCkanToken;
        } else {
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
        }
    }

    static private boolean initialized = false;

    static public void initLogger() {
        System.out.println("To see debug logging messages during testing copy src/test/resources/logging.properties.template into src/test/resources/logging.properties");
        Config.initLogger();
    }

    /**
     * Loads file {@link #JACKAN_TEST_PROPERTIES}
     */
    public static void initProperties() {

        Logger logger = Logger.getLogger(TestConfig.class.getName());
        final InputStream inputStream = TestConfig.class.getResourceAsStream("/" + JACKAN_TEST_PROPERTIES);
        try {
            if (inputStream == null) {
                throw new IOException("COULDN'T FIND TEST CONFIGURATION " + JACKAN_TEST_PROPERTIES + "\n "
                        + "TESTS REQUIRING WRITING TO CKAN WILL FAIL!");
            } else {
                logger.info("Loaded test configuration file " + JACKAN_TEST_PROPERTIES);
                properties = new Properties();
                properties.load(inputStream);
                outputCkan = properties.getProperty(OUTPUT_CKAN_PROPERTY);
                if (outputCkan == null) {
                    throw new IOException("Couldn't find property " + OUTPUT_CKAN_PROPERTY + " in configuration file " + JACKAN_TEST_PROPERTIES);
                } else {
                    logger.info("Will use " + outputCkan + " for CKAN write tests");
                }

                outputCkanToken = properties.getProperty(OUTPUT_CKAN_TOKEN_PROPERTY);
                if (outputCkanToken == null) {
                    throw new IOException("COULDN'T FIND PROPERTY " + OUTPUT_CKAN_TOKEN_PROPERTY + " IN CONFIGURATION FILE " + JACKAN_TEST_PROPERTIES);
                } else {
                    logger.info("Will use token " + outputCkanToken + " for CKAN write tests");
                }
                initialized = true;
            }
        } catch (Exception e) {
            logger.severe("JACKAN ERROR - COULDN'T INIALIZE TEST ENVIRONMENT PROPERLY! SOME TESTS MIGHT FAIL BECAUSE OF THIS.");
            logger.severe(e.getMessage());
        }

    }

}
