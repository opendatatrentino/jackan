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

import eu.trentorise.opendata.commons.TodConfig;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.CkanClient.Builder;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.exceptions.JackanNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class JackanTestConfig {

    private static final JackanTestConfig INSTANCE = new JackanTestConfig();

    /**
     * Path to file containing jackan testing specific properties
     */
    public static final String TEST_PROPERTIES_PATH = "jackan.test.properties";

    private static final String OUTPUT_CKAN_PROPERTY = "jackan.test.ckan.output";
    private static final String OUTPUT_CKAN_TOKEN_PROPERTY = "jackan.test.ckan.output-token";
    private static final String CLIENT_CLASS_PROPERTY = "jackan.test.ckan.client-class";
    private static final String PROXY_PROPERTY = "jackan.test.ckan.proxy";
    private static final String TIMEOUT_PROPERTY = "jackan.test.ckan.timeout";
    private static final String RESOURCE_FILE = "jackan.test.ckan.resource-file";
    private static final String ALTERNATE_RESOURCE_FILE = "jackan.test.ckan.alternate-resource-file";

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

    /**
     * By default tests will use {@link CheckedCkanClient}
     */
    private String clientClass;

    private String proxy;

    private int timeout = CkanClient.DEFAULT_TIMEOUT;

    private String resourceFile;

    private String alternateResourceFile;

    private TodConfig todConfig;

    private JackanTestConfig() {
        todConfig = TodConfig.of(JackanTestConfig.class);
    }

    /**
     * @throws IllegalStateException
     *             if {@link #loadLogConfig()} didn't succeed.
     */
    public String getOutputCkan() {
        if (initialized) {
            return outputCkan;
        } else {
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
        }
    }

    /**
     * @throws IllegalStateException
     *             if {@link #loadLogConfig()} didn't succeed.
     */
    public String getOutputCkanToken() {
        if (initialized) {
            return outputCkanToken;
        } else {
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
        }
    }

    public String getClientClass() {
        if (initialized) {
            return clientClass;
        } else {
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
        }

    }

    /**
     * @since 0.4.3
     */    
    public String getResourceFile() {
        if (initialized)
            return resourceFile;
        else
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
    }

    /**
     * @since 0.4.3
     */
    public String getAlternateResourceFile() {
        if (initialized)
            return alternateResourceFile;
        else
            throw new IllegalStateException("Jackan testing system was not properly initialized!");
    }

    private static File confDir;
    private static File defaultConfDir;

    private static boolean isDefaultConfDir(File dir) {
        
        return (dir.isDirectory() && dir.listFiles().length == 1 && dir.listFiles()[0].getName()
                                                                 .toLowerCase()
                                                                 .endsWith("readme.txt"));
    }

    /**
     * Walks the parent directories until finds the conf folder.
     *
     * @return the conf folder
     * @throws JackanNotFoundException
     *             if folder is not found.
     */
    private static File findConfDir() {

        if (confDir == null) {

            File directory = new File(System.getProperty("user.dir"));

            boolean reachedFileSystemRoot = false;
            while (!reachedFileSystemRoot) {
                File candidateConf = new File(directory.getAbsolutePath() + File.separator + "conf");

                System.out.println("Trying conf candidate " + candidateConf.getAbsolutePath());

                if (directory.isDirectory() && directory.exists() && candidateConf.isDirectory()
                        && candidateConf.exists() && !isDefaultConfDir(candidateConf)) {

                    System.out.println("Found conf folder at " + candidateConf.getAbsolutePath());
                    confDir = candidateConf;
                    return confDir;
                } else {
                    if (isDefaultConfDir(candidateConf)) {
                        defaultConfDir = candidateConf;
                    }

                    File parent = directory.getParentFile();
                    if (parent != null) {
                        directory = parent;
                    } else {
                        reachedFileSystemRoot = true;
                    }
                }
            }
            String msg = "";
            if (defaultConfDir != null) {
                msg += " (but found default conf directory at " + defaultConfDir + " with only README.txt inside)";
            }
            throw new JackanException("Couldn't find conf folder! " + msg);
        } else {
            return confDir;
        }
    }

    /**
     * First searches in conf/ directory, then continues search in conf
     * directories walking up to file system root.
     *
     * @param filepath
     *            Relative filepath with file name and extension included. i.e.
     *            abc/myfile.xml, which will be first searched in
     *            conf/abc/myfile.xml
     * @throws JackanNotFoundException
     *             if no file is found
     */
    private static File findConfFile(String filepath) {
        checkNotEmpty(filepath, "Invalid filepath!");

        File confDir = findConfDir();
        File candFile = new File(confDir + File.separator + filepath);

        if (candFile.exists()) {
            return candFile;
        } else {
            /*
             * candFile = new File( confDir + File.separator + ".." +
             * File.separator + "conf-template" + File.separator + filepath); if
             * (candFile.exists()) { return candFile; }
             */
            throw new JackanNotFoundException("Can't find file " + filepath + " in conf dir: " + confDir);
        }

    }

    /**
     * Loads file.
     *
     * @param filepath
     *            relative or absolute path with complete file name, i.e.
     *            /etc/bla.xml or ../../bla.xml
     * @throws JackanNotFoundException
     *             if file is not found
     * @throws JackanException
     *             on generic error
     */
    private InputStream loadConfFile(File file) {

        if (!file.exists()) {
            throw new JackanException("Logback filepath " + file + " does not reference a file that exists");
        } else {
            if (!file.isFile()) {
                throw new JackanException("Logback filepath " + file + " exists, but does not reference a file");
            } else {
                if (!file.canRead()) {
                    throw new JackanException(
                            "Logback filepath " + file + " exists and is a file, but cannot be read.");
                } else {

                    try {
                        return new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        throw new JackanNotFoundException("Couldn't find file " + file);
                    }

                }
            }
        }
    }

    /**
     * Loads logging config (see {@link TodConfig#loadLogConfig()}) and
     * configuration for writing tests at path {@link #TEST_PROPERTIES_PATH}.
     * NOTE: The latter config is searched in a smarter way by looking in the
     * first conf/ folder found walking along directory tree.
     */
    public void loadConfig() {
        TodConfig.loadLogConfig(this.getClass());

        Logger logger = Logger.getLogger(JackanTestConfig.class.getName());
        // final InputStream inputStream =
        // JackanTestConfig.class.getResourceAsStream("/" +
        // TEST_PROPERTIES_PATH);

        FileInputStream inputStream = null;

        try {

            File jackanConfFile;
            try {
                jackanConfFile = findConfFile(TEST_PROPERTIES_PATH);
                inputStream = new FileInputStream(jackanConfFile);
                logger.log(Level.INFO, "Loaded test configuration file {0}", jackanConfFile);
            } catch (Exception ex) {
                throw new IOException(
                        "Couldn't load Jackan test config file " + TEST_PROPERTIES_PATH
                                + ", to enable writing tests please copy src/test/resources/jackan.test.properties to conf folder in the project root and edit as needed!",
                        ex);
            }

            properties = new Properties();
            properties.load(inputStream);

            @Nullable
            String timeoutString = properties.getProperty(TIMEOUT_PROPERTY);
            if (timeoutString != null) {
                timeout = Integer.parseInt(timeoutString);
            }

            proxy = properties.getProperty(PROXY_PROPERTY);

            outputCkan = properties.getProperty(OUTPUT_CKAN_PROPERTY);
            if (outputCkan == null || outputCkan.trim()
                                                .isEmpty()) {
                throw new IOException(
                        "Couldn't find property " + OUTPUT_CKAN_PROPERTY + " in configuration file " + jackanConfFile);
            } else {
                logger.log(Level.INFO, "Will use {0} for CKAN write tests", outputCkan);
            }

            outputCkanToken = properties.getProperty(OUTPUT_CKAN_TOKEN_PROPERTY);
            if (outputCkanToken == null || outputCkanToken.trim()
                                                          .isEmpty()) {
                throw new IOException("COULDN'T FIND PROPERTY " + OUTPUT_CKAN_TOKEN_PROPERTY + " IN CONFIGURATION FILE "
                        + jackanConfFile);
            } else {
                logger.log(Level.INFO, "Will use token {0} for CKAN write tests", outputCkanToken);
            }

            clientClass = properties.getProperty(CLIENT_CLASS_PROPERTY);
            if (clientClass == null || clientClass.trim()
                                                  .isEmpty()) {
                clientClass = CheckedCkanClient.class.getName();
                logger.log(Level.INFO, "Will use default client class {0} for writing", clientClass);
            } else {
                clientClass = clientClass.trim();
                logger.log(Level.INFO, "Will use client class {0} for writing", clientClass);
            }

            resourceFile = properties.getProperty(RESOURCE_FILE);
            if (resourceFile == null || resourceFile.trim().isEmpty())
                throw new IOException("COULDN'T FIND PROPERTY " + RESOURCE_FILE + " IN CONFIGURATION FILE "
                    + jackanConfFile);
            else
                logger.log(Level.INFO, "Will use file {0} for CKAN resource write tests", resourceFile);

            alternateResourceFile = properties.getProperty(ALTERNATE_RESOURCE_FILE);
            if (alternateResourceFile == null || alternateResourceFile.trim().isEmpty())
                throw new IOException("COULDN'T FIND PROPERTY " + ALTERNATE_RESOURCE_FILE + " IN CONFIGURATION FILE "
                    + jackanConfFile);
            else
                logger.log(Level.INFO, "Will use alternate file {0} for CKAN resource write tests", alternateResourceFile);

            // let's see if it doesn't explode..
            try {
                makeClientInstanceForWriting(clientClass);
            } catch (Exception ex) {
                throw new Exception("Could not make test instance for client class " + clientClass, ex);
            }

            initialized = true;

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "JACKAN ERROR - COULDN'T INITIALIZE TEST ENVIRONMENT PROPERLY! INTEGRATION TESTS (ESPECIALLY FOR WRITING) MIGHT FAIL BECAUSE OF THIS!!",
                    e);
        }

    }

    /**
     * Returns a default client instance for writing.
     * 
     * @deprecated {@link #makeClientInstanceForWriting(boolean)} instead
     */
    public CkanClient makeClientInstanceForWriting() {
        return makeClientInstanceForWriting(true);
    }

    
    /**
     * Returns a default client instance for writing.
     * 
     * @param includeToken if {@code true} includes the token present in configuration.
     * @since 0.4.3
     */
    public CkanClient makeClientInstanceForWriting(boolean includeToken) {
        return makeClientInstanceForWriting(clientClass, includeToken);
    }

    /**
     * @deprecated use {@link #makeClientInstanceForWriting(String, boolean)} instead
     */
    public CkanClient makeClientInstanceForWriting(String clientClass) {
        return makeClientInstanceForWriting(clientClass, true);
    }
    
    /**
     * @param includeToken if {@code true} includes the token present in configuration. 
     * 
     * @since 0.4.3
     */
    public CkanClient makeClientInstanceForWriting(String clientClass, boolean includeToken) {
        checkNotEmpty(clientClass, "Invalid class string!");

        Class forName;
        try {
            forName = Class.forName(clientClass);
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't find client class " + clientClass + "!", ex);
        }

        Method method;
        CkanClient.Builder builder;
        try {
            method = forName.getMethod("builder");
            builder = (Builder) method.invoke(null);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            throw new RuntimeException("Could not find builder() static method in client class " + clientClass, ex);
        }

        builder.setCatalogUrl(outputCkan)                      
                      .setProxy(proxy)
                      .setTimeout(timeout);
        if (includeToken){
            builder.setCkanToken(outputCkanToken);
        } 

        return builder.build();
            
    }

    /**
     * Returns the singleton
     */
    public static JackanTestConfig of() {
        return INSTANCE;
    }
}
