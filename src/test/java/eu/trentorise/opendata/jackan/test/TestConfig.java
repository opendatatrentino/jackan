package eu.trentorise.opendata.jackan.test;

import eu.trentorise.opendata.jackan.Configuration;
import static eu.trentorise.opendata.jackan.ckan.CkanJacksonTest.logger;

/**
 *
 * @author David Leoni
 */
public class TestConfig {
    static public void initLogger(){
        logger.info("To see debug logging messages during testing copy src/test/resources/logging.properties.template into src/test/resources/logging.properties");
        Configuration.initLogger();        
    }
}
