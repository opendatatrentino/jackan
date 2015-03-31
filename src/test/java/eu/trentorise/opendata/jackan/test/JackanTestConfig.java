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

import eu.trentorise.opendata.commons.OdtConfig;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Leoni
 */
public class JackanTestConfig {

    private static final JackanTestConfig INSTANCE = new JackanTestConfig();

    /**
     * Path to file containing jackan testing specific properties
     */
    public static final String TEST_PROPERTIES_PATH = "conf/jackan.test.properties";

    private static final String OUTPUT_CKAN_PROPERTY = "jackan.test.ckan.output";
    private static final String OUTPUT_CKAN_TOKEN_PROPERTY = "jackan.test.ckan.output-token";

    private Properties properties;
    private boolean initialized = false;

    /**
     * Ckan used for tests which require writing
     */
    private String outputCkan;

    /**
     * Token for CKAN like "b7592183-53c4-57da-wq52-5b1cb84db9db"
     */
    private String outputCkanToken;

    private OdtConfig odtConfig;
    
    private JackanTestConfig() { 
        odtConfig = OdtConfig.of(JackanTestConfig.class);
    }

    /**
     * @throws IllegalStateException if {@link #loadLogConfig()} didn't succeed.
     */
    public String getOutputCkan() {
        if (initialized) {
            return outputCkan;
        } else {
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
        }
    }

    /**
     * @throws IllegalStateException if {@link #loadLogConfig()} didn't succeed.
     */
    public String getOutputCkanToken() {
        if (initialized) {
            return outputCkanToken;
        } else {
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
        }
    }

    /**
     * Loads logging config (see {@link OdtConfig#loadLogConfig()}) and 
     * configuration for writing tests at path {@link #TEST_PROPERTIES_PATH}
     */
    public void loadConfig() {
        OdtConfig.loadLogConfig(this.getClass());

        Logger logger = Logger.getLogger(JackanTestConfig.class.getName());
        //final InputStream inputStream = JackanTestConfig.class.getResourceAsStream("/" + TEST_PROPERTIES_PATH);                

        FileInputStream inputStream = null;

        try {

            try {                
                inputStream = new FileInputStream(TEST_PROPERTIES_PATH);
                logger.log(Level.INFO, "Loaded test configuration file {0}", TEST_PROPERTIES_PATH);
            }
            catch (Exception ex) {
                throw new IOException("Couldn't load Jackan test config file " + TEST_PROPERTIES_PATH + ", to enable writing tests please copy src/test/resources/jackan.test.properties to conf folder in the project root and edit as needed!", ex);
            }

            properties = new Properties();
            properties.load(inputStream);
            outputCkan = properties.getProperty(OUTPUT_CKAN_PROPERTY);
            if (outputCkan == null) {
                throw new IOException("Couldn't find property " + OUTPUT_CKAN_PROPERTY + " in configuration file " + TEST_PROPERTIES_PATH);
            } else {
                logger.log(Level.INFO, "Will use {0} for CKAN write tests", outputCkan);
            }

            outputCkanToken = properties.getProperty(OUTPUT_CKAN_TOKEN_PROPERTY);
            if (outputCkanToken == null) {
                throw new IOException("COULDN'T FIND PROPERTY " + OUTPUT_CKAN_TOKEN_PROPERTY + " IN CONFIGURATION FILE " + TEST_PROPERTIES_PATH);
            } else {
                logger.log(Level.INFO, "Will use token {0} for CKAN write tests", outputCkanToken);
            }
            initialized = true;

        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "JACKAN ERROR - COULDN'T INITIALIZE TEST ENVIRONMENT PROPERLY! TESTS REQUIRING WRITING MIGHT FAIL BECAUSE OF THIS.", e);
        }

    }

    /** Returns the singleton */
    public static JackanTestConfig of() {
        return INSTANCE;
    }
}
