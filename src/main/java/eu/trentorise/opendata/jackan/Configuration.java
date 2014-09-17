package eu.trentorise.opendata.jackan;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Class to initialize logging
 *
 * @author David Leoni
 */
public class Configuration {

    static public String JACKAN_LOG_PROPERTIES = "logging.properties";

    private static Logger logger = Logger.getLogger(Configuration.class.getName());
    
    private static boolean initialized = false;

    public static void initLogger() {
        if (initialized) {
            logger.info("Logger is already initialized.");
        } else {
            System.out.println("Jackan: Going to initialize logging...");
            final InputStream inputStream = Configuration.class.getResourceAsStream("/" + JACKAN_LOG_PROPERTIES);
            try {
                if (inputStream == null) {
                    throw new IOException("JACKAN ERROR! COULDN'T FIND LOG CONFIGURATION FILE: " + JACKAN_LOG_PROPERTIES);
                }
                LogManager.getLogManager().readConfiguration(inputStream);
                
                logger.info("Configured logger with file: " + JACKAN_LOG_PROPERTIES);
                logger.fine("Do you hear me?");
                initialized = true;
            } catch (Exception e) {
                Logger.getAnonymousLogger().severe("JACKAN ERROR - COULDN'T INITIALIZE LOGGING!");
                Logger.getAnonymousLogger().severe(e.getMessage());
            }
        }
    }
}
