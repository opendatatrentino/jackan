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
import static eu.trentorise.opendata.commons.OdtConfig.LOG_PROPERTIES_PATH;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author David Leoni
 */
public class JackanTestConfig extends OdtConfig {

    private static JackanTestConfig INSTANCE = new JackanTestConfig(); 

    /**Path to file containing testing specific properties */
    public static final String TEST_PROPERTIES_PATH = "META-INF/jackan-test.properties";

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

    private JackanTestConfig(){
        super();
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
     * Loads logging config (see {@link #loadLogConfig()}) and other configuration at path {@link #TEST_PROPERTIES_PATH}
     */
    public void loadConfig() {
        loadLogConfig();
        
        Logger logger = Logger.getLogger(JackanTestConfig.class.getName());
        //final InputStream inputStream = JackanTestConfig.class.getResourceAsStream("/" + TEST_PROPERTIES_PATH);                
        
        try {
            URL url = getClass().getResource("/" + TEST_PROPERTIES_PATH);
            if (url == null) {
                throw new IOException("COULDN'T FIND TEST CONFIGURATION " + TEST_PROPERTIES_PATH + "\n "
                        + "TESTS REQUIRING WRITING TO CKAN WILL FAIL!");
            } else {
                
                String path = url.toURI().getPath();         
                InputStream inputStream = new FileInputStream(path);                
                logger.log(Level.INFO, "Loaded test configuration file {0}", path);
                properties = new Properties();
                properties.load(inputStream);
                outputCkan = properties.getProperty(OUTPUT_CKAN_PROPERTY);
                if (outputCkan == null) {
                    throw new IOException("Couldn't find property " + OUTPUT_CKAN_PROPERTY + " in configuration file " + TEST_PROPERTIES_PATH);
                } else {
                    logger.info("Will use " + outputCkan + " for CKAN write tests");
                }

                outputCkanToken = properties.getProperty(OUTPUT_CKAN_TOKEN_PROPERTY);
                if (outputCkanToken == null) {
                    throw new IOException("COULDN'T FIND PROPERTY " + OUTPUT_CKAN_TOKEN_PROPERTY + " IN CONFIGURATION FILE " + TEST_PROPERTIES_PATH);
                } else {
                    logger.info("Will use token " + outputCkanToken + " for CKAN write tests");
                }
                initialized = true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "JACKAN ERROR - COULDN'T INITIALIZE TEST ENVIRONMENT PROPERLY! SOME TESTS MIGHT FAIL BECAUSE OF THIS.", e);
            logger.severe(e.getMessage());
        }

    }

    public static JackanTestConfig of(){
        return INSTANCE;
    }
}
