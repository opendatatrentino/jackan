/* 
 * Copyright 2015 Trento RISE (trentorise.eu)
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
public class Config {

    static public String JACKAN_LOG_PROPERTIES = "conf/logging.properties";

    private static Logger logger = Logger.getLogger(Config.class.getName());
    
    private static boolean initialized = false;

    public static void initLogger() {
        if (initialized) {
            logger.info("Logger is already initialized.");
        } else {
            System.out.println("Jackan: Going to initialize logging...");
            final InputStream inputStream = Config.class.getResourceAsStream("/" + JACKAN_LOG_PROPERTIES);
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
