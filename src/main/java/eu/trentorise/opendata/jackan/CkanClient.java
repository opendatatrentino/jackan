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
package eu.trentorise.opendata.jackan;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static eu.trentorise.opendata.commons.TodUtils.removeTrailingSlash;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import eu.trentorise.opendata.commons.TodUtils;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.exceptions.CkanNotFoundException;
import eu.trentorise.opendata.jackan.exceptions.CkanValidationException;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanDatasetBase;
import eu.trentorise.opendata.jackan.model.CkanDatasetRelationship;
import eu.trentorise.opendata.jackan.model.CkanError;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanGroupOrgBase;
import eu.trentorise.opendata.jackan.model.CkanLicense;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanPair;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanResourceBase;
import eu.trentorise.opendata.jackan.model.CkanResponse;
import eu.trentorise.opendata.jackan.model.CkanTag;
import eu.trentorise.opendata.jackan.model.CkanTagBase;
import eu.trentorise.opendata.jackan.model.CkanUser;
import eu.trentorise.opendata.jackan.model.CkanUserBase;
import eu.trentorise.opendata.jackan.model.CkanVocabulary;
import eu.trentorise.opendata.jackan.model.CkanVocabularyBase;
import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * Client to access a ckan instance. Threadsafe.
 * <p>
 * The client is a thin wrapper upon Ckan api, thus one method call should
 * correspond to only one web api call. This means sometimes to get a full
 * object from Ckan, you will need to do a second call.
 * </p>
 * <p>
 * You can create clients either with constructors or the
 * {@link CkanClient#builder() builder()} method if you need to set more
 * connection parameters (i.e. proxy, timeout, ..).
 * </p>
 * <p>
 * For writing to Ckan you might want to use {@link CheckedCkanClient} which
 * does additional checks to ensure written content is correct.
 * </p>
 * 
 * @author David Leoni, Ivan Tankoyeu
 *
 */
public class CkanClient {

    /**
     * CKAN uses timestamps like '1970-01-01T01:00:00.000010' in UTC timezone,
     * has precision up to microsecond and doesn't append 'Z' to timestamps. The
     * format respects
     * <a href="https://en.wikipedia.org/wiki/ISO_8601" target="_blank">ISO 8601
     * standard</a>. In Jackan we store it as {@link java.sql.Timestamp} or
     * {@code null} if parse is not successful.
     *
     * @see #parseTimestamp(java.lang.String)
     * @see #formatTimestamp(java.sql.Timestamp)
     *
     * @since 0.4.1
     */
    public static final String CKAN_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    /**
     * Found pattern "2013-12-17T00:00:00" in resource.date_modified in
     * dati.toscana:
     * http://dati.toscana.it/api/3/action/package_show?id=alluvioni_bacreg See
     * also <a href="https://github.com/ckan/ckan/issues/1874"> ckan issue
     * 874 </a> and <a href="https://github.com/ckan/ckan/pull/2519">ckan pull
     * 2519</a>
     * 
     * @since 0.4.1
     */
    public static final String CKAN_NO_MILLISECS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Notice that even for the same api version (at least for versions up to 3
     * included) different CKAN instances can behave quite differently, either
     * for differences in software or custom server permissions.
     */
    public static final ImmutableList<Integer> SUPPORTED_API_VERSIONS = ImmutableList.of(3);

    /** Default timeout in millisecs */
    public static final int DEFAULT_TIMEOUT = 15000;

    /**
     * Sometimes we get back Python "None" as a string instead of proper JSON
     * null
     *
     * @since 0.4.1
     */
    public static final String NONE = "None";

    private static final Logger LOG = Logger.getLogger(CkanClient.class.getName());

    private static final String COULDNT_JSONIZE = "Couldn't jsonize the provided ";

    @Nullable
    private static ObjectMapper objectMapper;

    private static final Map<String, ObjectMapper> OBJECT_MAPPERS_FOR_POSTING = new HashMap();

    private String catalogUrl;

    @Nullable
    private String ckanToken;

    @Nullable
    private HttpHost proxy;

    /** connection timeout in millisecs */
    private int timeout;

    @JsonSerialize(as = CkanResourceBase.class)
    private abstract static class CkanResourceForPosting {
    }

    @JsonSerialize(as = CkanDatasetBase.class)
    private abstract static class CkanDatasetForPosting {
    }

    @JsonSerialize(as = CkanGroupOrgBase.class)
    private abstract static class CkanGroupOrgForPosting {
    }

    @JsonSerialize(as = GroupForDatasetPosting.class)
    abstract static class GroupForDatasetPosting extends CkanGroupOrgBase {

        @JsonIgnore
        @Override
        public List<CkanUser> getUsers() {
            return super.getUsers();
        }
    }

    @JsonSerialize(as = CkanUserBase.class)
    private abstract static class CkanUserForPosting {
    }

    @JsonSerialize(as = CkanTagBase.class)
    private abstract static class CkanTagForPosting {
    }

    /**
     * Configures the provided Jackson ObjectMapper exactly as the internal JSON
     * mapper used for reading operations. If you want to perform
     * create/update/delete operations, use
     * {@link #configureObjectMapperForPosting(com.fasterxml.jackson.databind.ObjectMapper, java.lang.Class) }
     * instead.
     *
     * @param om
     *            a Jackson object mapper
     * @since 0.4.1
     */
    public static void configureObjectMapper(ObjectMapper om) {
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        om.registerModule(new JackanModule());
    }

    /**
     * Configures the provided Jackson ObjectMapper for create/update/delete
     * operations of Ckan objects. For reading and generic
     * serialization/deserialization of Ckan objects, use
     * {@link #configureObjectMapper(com.fasterxml.jackson.databind.ObjectMapper) }
     * instead. For future compatibility you will need a different object mapper
     * for each class you want to post to ckan. <b> DO NOT </b> call
     * {@link #configureObjectMapper(com.fasterxml.jackson.databind.ObjectMapper) }
     * on the mapper prior to this call.
     *
     * @param om
     *            a Jackson object mapper
     * @param clazz
     *            the class of the objects you wish to create/update/delete.
     * @since 0.4.1
     */
    public static void configureObjectMapperForPosting(ObjectMapper om, Class clazz) {
        configureObjectMapper(om);
        om.setSerializationInclusion(Include.NON_NULL);
        om.addMixInAnnotations(CkanResource.class, CkanResourceForPosting.class);
        om.addMixInAnnotations(CkanDataset.class, CkanDatasetForPosting.class);
        om.addMixInAnnotations(CkanOrganization.class, CkanGroupOrgForPosting.class);
        if (CkanDatasetBase.class.isAssignableFrom(clazz)) {
            // little fix for
            // https://github.com/opendatatrentino/jackan/issues/19
            om.addMixInAnnotations(CkanGroup.class, GroupForDatasetPosting.class);
        } else {
            om.addMixInAnnotations(CkanGroup.class, CkanGroupOrgForPosting.class);
        }

        om.addMixInAnnotations(CkanUser.class, CkanUserForPosting.class);
        om.addMixInAnnotations(CkanTag.class, CkanTagForPosting.class);
    }

    /**
     * Retrieves the Jackson object mapper configured for creation/update
     * operations. Internally, Object mapper is initialized at first call.
     *
     * @param clazz
     *            the class you want to post. For generic class, just put
     *            Object.class
     * @since 0.4.1
     */
    static ObjectMapper getObjectMapperForPosting(Class clazz) {
        checkNotNull(clazz, "Invalid class! If you don't know the class just use Object.class");

        if (OBJECT_MAPPERS_FOR_POSTING.get(clazz.getName()) == null) {
            LOG.log(Level.FINE, "Generating ObjectMapper for posting class {0}", clazz);
            ObjectMapper om = new ObjectMapper();
            configureObjectMapperForPosting(om, clazz);
            OBJECT_MAPPERS_FOR_POSTING.put(clazz.getName(), om);
        }

        return OBJECT_MAPPERS_FOR_POSTING.get(clazz.getName());
    }

    /**
     * Retrieves the Jackson object mapper for reading operations. Internally,
     * Object mapper is initialized at first call.
     */
    static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            configureObjectMapper(objectMapper);
        }
        return objectMapper;
    }

    /**
     * The timeout expressed in milliseconds. By default it is
     * {@link #DEFAULT_TIMEOUT}.
     */
    public int getTimeout() {
        return timeout;
    }

    protected CkanClient() {
        this.timeout = DEFAULT_TIMEOUT;
        this.catalogUrl = "";
    }

    /**
     * Creates a Ckan client with null token
     * 
     * @param catalogUrl
     *            the catalog url i.e. http://data.gov.uk. Internally, it will
     *            be stored in a normalized format (to avoid i.e. trailing
     *            slashes).
     */
    public CkanClient(String catalogUrl) {
        this();
        checkNotEmpty(catalogUrl, "invalid ckan catalog url");
        this.catalogUrl = removeTrailingSlash(catalogUrl);
    }

    /**
     * Creates a Ckan client with null token
     * 
     * @param catalogUrl
     *            the catalog url i.e. http://data.gov.uk. Internally, it will
     *            be stored in a normalized format (to avoid i.e. trailing
     *            slashes).
     * @param ckanToken
     *            the token used for authorization in ckan api
     * 
     */
    public CkanClient(String catalogUrl, @Nullable String ckanToken) {
        this(catalogUrl);
        this.ckanToken = ckanToken;
    }

    /**
     * Returns a new client builder.
     * 
     * The builder is not threadsafe and you can use one builder instance to
     * build only one client instance.
     * 
     */
    public static CkanClient.Builder builder() {
        return new Builder(new CkanClient());
    }

    /**
     * Builder for the client. The builder is not threadsafe and you can use one
     * builder instance to build only one client instance.
     * 
     * @author David Leoni
     *
     */
    public static class Builder {
        private CkanClient client;
        private boolean created;

        protected CkanClient getClient() {
            return client;
        }

        protected boolean getCreated() {
            return created;
        }

        protected void checkNotCreated() {
            if (created) {
                throw new IllegalStateException("Builder was already used to create a client!");
            }
        }

        protected Builder(CkanClient client) {
            checkNotNull(client);
            this.client = client;
            this.created = false;
        }

        /**
         * Sets the catalog url i.e. http://data.gov.uk.
         * 
         * Internally, it will be stored in a normalized format (to avoid i.e.
         * trailing slashes).
         */
        public Builder setCatalogUrl(String catalogUrl) {
            checkNotCreated();
            checkNotEmpty(catalogUrl, "invalid ckan catalog url");
            this.client.catalogUrl = removeTrailingSlash(catalogUrl);
            return this;
        }

        /**
         * Sets the private token string for ckan repository
         */
        public Builder setCkanToken(@Nullable String ckanToken) {
            checkNotCreated();
            this.client.ckanToken = ckanToken;
            return this;
        }

        /**
         * Sets the proxy used to perform GET and POST calls
         */
        public Builder setProxy(@Nullable String proxyUrl) {
            checkNotCreated();

            if (proxyUrl == null) {
                this.client.proxy = null;
            } else {
                URI uri;
                try {
                    uri = new URI(proxyUrl.trim());
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Invalid proxy url!", e);
                }
                if (!uri.getPath()
                        .isEmpty()) {
                    throw new IllegalArgumentException("Proxy host shouldn't have context path! Found instead: "
                            + uri.toString() + " with path " + uri.getPath());
                }
                this.client.proxy = URIUtils.extractHost(uri);

            }
            return this;
        }

        /**
         * Sets the connection timeout expressed as number of milliseconds. Must
         * be greater than zero, otherwise IllegalArgumentException is thrown.
         *
         * @throws IllegalArgumentException
         *             is value is less than 1.
         */
        public Builder setTimeout(int timeout) {
            checkNotCreated();
            checkArgument(timeout > 0, "Timeout must be > 0 ! Found instead %s", timeout);
            this.client.timeout = timeout;
            return this;
        }

        public CkanClient build() {
            checkNotEmpty(this.client.catalogUrl, "Invalid catalog url!");
            this.created = true;
            return this.client;
        }
    }

    @Override
    public String toString() {
        String maskedToken = ckanToken == null ? null : "*****MASKED_TOKEN*******";
        return "CkanClient{" + "catalogURL=" + catalogUrl + ", ckanToken=" + maskedToken + '}';
    }

    /**
     * Calculates a full url out of the provided params
     *
     * @param path
     *            something like /api/3/package_show
     * @param params
     *            list of key, value parameters. They must be not be url
     *            encoded. i.e. "id","laghi-monitorati-trento"
     * @return the full url to be called.
     * @throws JackanException
     *             if there is any error building the url
     */
    private String calcFullUrl(String path, Object[] params) {
        checkNotNull(path);

        try {
            StringBuilder sb = new StringBuilder().append(catalogUrl)
                                                  .append(path);
            for (int i = 0; i < params.length; i += 2) {
                sb.append(i == 0 ? "?" : "&")
                  .append(URLEncoder.encode(params[i].toString(), "UTF-8"))
                  .append("=")
                  .append(URLEncoder.encode(params[i + 1].toString(), "UTF-8"));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new JackanException("Error while building url to perform GET! \n path: " + path + " \n params: "
                    + Arrays.toString(params), ex);
        }
    }

    /**
     * Configures the request. Should work both for GETs and POSTs.
     */
    protected Request configureRequest(Request request) {
        if (ckanToken != null) {
            request.addHeader("Authorization", ckanToken);
        }
        if (proxy != null) {
            request.viaProxy(proxy);
        }
        request.socketTimeout(this.timeout)
               .connectTimeout(this.timeout);

        return request;
    }

    /**
     * Performs HTTP GET on server. If {@link CkanResponse#isSuccess()} is false
     * throws {@link CkanException}.
     *
     * @param <T>
     * @param responseType
     *            a descendant of CkanResponse
     * @param path
     *            something like /api/3/package_show
     * @param params
     *            list of key, value parameters. They must be not be url
     *            encoded. i.e. "id","laghi-monitorati-trento"
     * @throws CkanException
     *             on error
     */
    private <T extends CkanResponse> T getHttp(Class<T> responseType, String path, Object... params) {

        checkNotNull(responseType);
        checkNotNull(path);

        String fullUrl = calcFullUrl(path, params);

        T ckanResponse;
        String returnedText;

        try {
            LOG.log(Level.FINE, "getting {0}", fullUrl);
            Request request = Request.Get(fullUrl);

            configureRequest(request);

            Response response = request.execute();

            InputStream stream = response.returnResponse()
                                         .getEntity()
                                         .getContent();

            try (InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8)) {
                returnedText = CharStreams.toString(reader);
            }
        } catch (Exception ex) {
            throw new CkanException("Error while performing GET. Request url was: " + fullUrl, this, ex);
        }
        try {
            ckanResponse = getObjectMapper().readValue(returnedText, responseType);
        } catch (Exception ex) {
            throw new CkanException(
                    "Couldn't interpret json returned by the server! Returned text was: " + returnedText, this, ex);
        }

        if (!ckanResponse.isSuccess()) {
            throwCkanException("Error while performing GET. Request url was: " + fullUrl, ckanResponse);
        }
        return ckanResponse;
    }

    /**
     * Throws CkanException or a subclass of it according to CkanError#getType()
     *
     * @throws CkanException
     * @since 0.4.1
     */
    protected <T extends CkanResponse> void throwCkanException(String msg, T ckanResponse) {
        if (ckanResponse.getError() != null && ckanResponse.getError()
                                                           .getType() != null) {
            switch (ckanResponse.getError()
                                .getType()) {
            case CkanError.NOT_FOUND_ERROR:
                throw new CkanNotFoundException(msg, ckanResponse, this);
            case CkanError.VALIDATION_ERROR:
                throw new CkanValidationException(msg, ckanResponse, this);
            }
        }

        throw new CkanException(msg, ckanResponse, this);
    }

    /**
     *
     * POSTs a body via HTTP. If {@link CkanResponse#isSuccess()} is false
     * throws {@link CkanException}.
     *
     * @param responseType
     *            a descendant of CkanResponse
     * @param path
     *            something like /api/3/action/package_create
     * @param body
     *            the body of the POST
     * @param contentType
     * @param params
     *            list of key, value parameters. They must be not be url
     *            encoded. i.e. "id","laghi-monitorati-trento"
     * @throws CkanException
     *             on error
     */
    private <T extends CkanResponse> T postHttp(Class<T> responseType, String path, String body,
            ContentType contentType, Object... params) {
        checkNotNull(responseType);
        checkNotNull(path);
        checkNotNull(body);
        checkNotNull(contentType);

        String fullUrl = calcFullUrl(path, params);

        T ckanResponse;
        String returnedText;

        try {
            LOG.log(Level.FINE, "Posting to url {0}", fullUrl);
            LOG.log(Level.FINE, "Sending body:{0}", body);
            Request request = Request.Post(fullUrl);

            configureRequest(request);

            Response response = request.bodyString(body, contentType)
                                       .execute();

            InputStream stream = response.returnResponse()
                                         .getEntity()
                                         .getContent();

            try (InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8)) {
                returnedText = CharStreams.toString(reader);
            }
        } catch (Exception ex) {
            throw new CkanException("Error while performing a POST! Request url is:" + fullUrl, this, ex);
        }

        try {
            ckanResponse = getObjectMapper().readValue(returnedText, responseType);
        } catch (Exception ex) {
            throw new CkanException(
                    "Couldn't interpret json returned by the server! Returned text was: " + returnedText, this, ex);
        }

        if (!ckanResponse.isSuccess()) {
            throwCkanException("Error while performing a POST! Request url is:" + fullUrl, ckanResponse);
        }
        return ckanResponse;

    }

    /**
     * Update the data associated to the given resource.
     *
     * @param responseType
     *            a descendant of CkanResponse
     * @param path
     *            something like /api/3/action/package_create
     * @param resource
     *            a {@link CkanResourceBase} object with a data file set in the {@link CkanResourceBase#upload} field
     *
     */
    private <T extends CkanResponse> T postHttpResourceFile(Class<T> responseType, String path, CkanResourceBase resource) {
        checkNotNull(responseType);
        checkNotNull(path);
        checkNotNull(resource);
        checkNotNull(resource.getUpload());

        String fullUrl = calcFullUrl(path, new Object[] {});

        T ckanResponse;
        String returnedText;

        try {
            LOG.log(Level.FINE, "Posting to url {0}", fullUrl);
            Request request = Request.Post(fullUrl);

            configureRequest(request);

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .addBinaryBody("upload", resource.getUpload(),
                    ContentType.create("application/octet-stream", Charset.forName("UTF-8")), resource.getUpload().getName())
                .addTextBody("id", resource.getId(), ContentType.TEXT_PLAIN)
                .addTextBody("url", "upload", ContentType.TEXT_PLAIN)
                .addTextBody("package_id", resource.getPackageId(), ContentType.TEXT_PLAIN)
                .addTextBody("format", resource.getFormat(), ContentType.TEXT_PLAIN)
                .addTextBody("mimetype", resource.getMimetype(), ContentType.TEXT_PLAIN)
                .addTextBody("size", resource.getSize(), ContentType.TEXT_PLAIN);
            if (resource.getLastModified() != null)
                entityBuilder.addTextBody("last_modified", resource.getLastModified(), ContentType.TEXT_PLAIN);
            entityBuilder.setCharset(Charset.forName("UTF-8"));

            Response response = request.body(entityBuilder.build()).execute();

            InputStream stream = response.returnResponse()
                .getEntity()
                .getContent();

            try (InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8)) {
                returnedText = CharStreams.toString(reader);
            }
        } catch (Exception ex) {
            throw new CkanException("Error while performing a POST! Request url is:" + fullUrl, this, ex);
        }

        try {
            ckanResponse = getObjectMapper().readValue(returnedText, responseType);
        } catch (Exception ex) {
            throw new CkanException(
                "Couldn't interpret json returned by the server! Returned text was: " + returnedText, this, ex);
        }

        if (!ckanResponse.isSuccess()) {
            throwCkanException("Error while performing a POST! Request url is:" + fullUrl, ckanResponse);
        }
        return ckanResponse;

    }

    /**
     * Returns the catalog URL (normalized).
     */
    public String getCatalogUrl() {
        return catalogUrl;
    }

    /**
     * Returns the private CKAN token.
     */
    public String getCkanToken() {
        return ckanToken;
    }

    private static void checkCatalogUrl(String catalogUrl) {
        checkNotEmpty(catalogUrl, "invalid catalog url");
    }

    /**
     * Returns the URL of dataset page in the catalog website.
     *
     * Valid URLs have this format with the name: <a href=
     * "http://dati.trentino.it/dataset/impianti-di-risalita-vivifiemme-2013"
     * target="_blank"> http://dati.trentino.it/dataset/impianti-di-risalita-
     * vivifiemme-2013 </a>
     *
     * @param datasetIdOrName
     *            either the dataset's {@link CkanDataset#getId() alphanumerical
     *            id} (preferred as it is more stable) or the
     *            {@link CkanDataset#getName() dataset name}
     *
     * @param catalogUrl
     *            i.e. http://dati.trentino.it
     */
    public static String makeDatasetUrl(String catalogUrl, String datasetIdOrName) {
        checkCatalogUrl(catalogUrl);
        checkNotEmpty(datasetIdOrName, "invalid dataset identifier");
        return removeTrailingSlash(catalogUrl) + "/dataset/" + datasetIdOrName;
    }

    /**
     *
     * Returns the URL of resource page in the catalog website.
     *
     * Valid URLs have this format with the dataset name
     * 'impianti-di-risalita-vivifiemme-2013': <a href=
     * "http://dati.trentino.it/dataset/impianti-di-risalita-vivifiemme-2013/resource/779d1d9d-9370-47f4-a194-1b0328c32f02"
     * target="_blank"> http://dati.trentino.it/dataset/impianti-di-risalita-
     * vivifiemme-2013/resource/779d1d9d-9370-47f4-a194-1b0328c32f02</a>
     *
     * @param catalogUrl
     *            i.e. http://dati.trentino.it
     * @param datasetIdOrName
     *            either the dataset's alphanumerical {@link CkanDataset#getId()
     *            id} (preferred as it is more stable) or the
     *            {@link CkanDataset#getName() dataset name}
     *
     * @param resourceId
     *            the {@link CkanResource#getId() alphanumerical id} of the
     *            resource (DON'T use {@link CkanResource#getName() resource
     *            name})
     */
    public static String makeResourceUrl(String catalogUrl, String datasetIdOrName, String resourceId) {
        checkCatalogUrl(catalogUrl);
        checkNotEmpty(datasetIdOrName, "invalid dataset identifier");
        checkNotEmpty(resourceId, "invalid resource id");
        return makeDatasetUrl(catalogUrl, datasetIdOrName) + "/resource/" + resourceId;
    }

    /**
     *
     * Reconstructs the URL of group page in the catalog website.
     *
     * Valid URLs have this format with the group name
     * 'gestione-del-territorio':
     *
     * <a href="http://dati.trentino.it/group/gestione-del-territorio" target=
     * "_blank"> http://dati.trentino.it/group/gestione-del-territorio </a>
     *
     * @param catalogUrl
     *            i.e. http://dati.trentino.it
     * @param groupIdOrName
     *            the group's alphanumerical id (preferred as more stable) or
     *            the group name as in {@link CkanGroup#getName()}.
     */
    public static String makeGroupUrl(String catalogUrl, String groupIdOrName) {
        checkCatalogUrl(catalogUrl);
        checkNotEmpty(groupIdOrName, "invalid group identifier");
        return TodUtils.removeTrailingSlash(catalogUrl) + "/group/" + groupIdOrName;
    }

    /**
     *
     * Given some organization parameters, reconstruct the URL of organization
     * page in the catalog website.
     *
     * Valid URLs have this format with the organization name
     * 'comune-di-trento':
     *
     * <a href="http://dati.trentino.it/organization/comune-di-trento" target=
     * "_blank"> http://dati.trentino.it/organization/comune-di-trento </a>
     *
     * @param catalogUrl
     *            i.e. http://dati.trentino.it
     * @param orgIdOrName
     *            the organization's alphanumerical id (preferred as more stable), 
     *            or the name as in {@link CkanOrganization#getName()}            
     */
    public static String makeOrganizationUrl(String catalogUrl, String orgIdOrName) {
        checkCatalogUrl(catalogUrl);
        checkNotEmpty(orgIdOrName, "invalid organization identifier");
        return TodUtils.removeTrailingSlash(catalogUrl) + "/organization/" + orgIdOrName;
    }

    /**
     * Returns list of dataset names like i.e. limestone-pavement-orders
     *
     * @throws JackanException
     *             on error
     */
    public synchronized List<String> getDatasetList() {
        return getHttp(DatasetListResponse.class, "/api/3/action/package_list").result;
    }

    /**
     *
     * @param limit
     * @param offset
     *            Starts with 0 included. getDatasetList(1,0) will return
     *            exactly one dataset, if catalog is not empty.
     * @return list of data names like i.e. limestone-pavement-orders
     * @throws JackanException
     *             on error
     */
    public synchronized List<String> getDatasetList(int limit, int offset) {
        return getHttp(DatasetListResponse.class, "/api/3/action/package_list", "limit", limit, "offset",
                offset).result;
    }

    /**
     * Returns the list of available licenses in the ckan catalog.
     */
    public synchronized List<CkanLicense> getLicenseList() {
        return getHttp(LicenseListResponse.class, "/api/3/action/license_list").result;
    }

    /**
     * Returns the latest api version supported by the catalog
     *
     * @throws JackanException
     *             on error
     */
    public synchronized int getApiVersion() {
        for (int i = 5; i >= 1; i--) { // this is demential. But /api always
                                       // gives { "version": 1} ....
            try {
                return getApiVersion(i);
            } catch (Exception ex) {

            }
        }
        throw new CkanException("Error while getting api version!", this);
    }

    /**
     * Returns the given api number
     *
     * @throws JackanException
     *             on error
     */
    private synchronized int getApiVersion(int number) {
        String fullUrl = catalogUrl + "/api/" + number;
        LOG.log(Level.FINE, "getting {0}", fullUrl);
        try {
            Request request = Request.Get(fullUrl);
            configureRequest(request);
            String json = request.execute()
                                 .returnContent()
                                 .asString();

            return getObjectMapper().readValue(json, ApiVersionResponse.class).version;
        } catch (Exception ex) {
            throw new CkanException("Error while fetching api version!", this, ex);
        }

    }

    /**
     * Fetches the dataset from ckan. Returned dataset will have resources with
     * at least all of the fields of {@link CkanResourceBase}
     *
     * @param idOrName
     *            either the dataset name (i.e. certified-products) or the
     *            alphanumerical id (i.e. 22eea137-9fc3-4222-a716-bac22cc2039a)
     *
     * @throws JackanException
     *             on error
     */
    public synchronized CkanDataset getDataset(String idOrName) {
        checkNotNull(idOrName, "Need a valid id or name!");

        CkanDataset cd = getHttp(DatasetResponse.class, "/api/3/action/package_show", "id", idOrName).result;
        for (CkanResource cr : cd.getResources()) {
            cr.setPackageId(cd.getId());
        }
        return cd;
    }

    /**
     * @throws JackanException
     *             on error
     */
    public synchronized List<CkanUser> getUserList() {
        return getHttp(UserListResponse.class, "/api/3/action/user_list").result;
    }

    /**
     * @param id
     *            i.e. 'admin'
     * @throws JackanException
     *             on error
     */
    public synchronized CkanUser getUser(String id) {
        checkNotNull(id, "Need a valid id!");

        return getHttp(UserResponse.class, "/api/3/action/user_show", "id", id).result;
    }

    /**
     * Creates ckan user on the server.
     *
     * @param user
     *            ckan user object with the minimal set of parameters required.
     *            See
     *            {@link CkanUserBase#CkanUserBase(java.lang.String, java.lang.String, java.lang.String)
     *            this constructor}
     * @return the newly created user
     * @throws JackanException
     */
    public synchronized CkanUser createUser(CkanUserBase user) {
        checkNotNull(user, "Need a valid user!");

        checkToken("Tried to create user" + user.getName());

        ObjectMapper om = CkanClient.getObjectMapperForPosting(CkanUserBase.class);
        String json = null;

        try {
            json = om.writeValueAsString(user);
        } catch (IOException e) {
            throw new CkanException(COULDNT_JSONIZE + user.getClass()
                                                          .getSimpleName(),
                    this, e);

        }

        return postHttp(UserResponse.class, "/api/3/action/user_create", json, ContentType.APPLICATION_JSON).result;
    }

    /**
     * @param id
     *            The alphanumerical id of the resource, such as
     *            d0892ada-b8b9-43b6-81b9-47a86be126db.
     *
     * @throws JackanException
     *             on error
     */
    public synchronized CkanResource getResource(String id) {
        checkNotNull(id, "Need a valid id!");
        return getHttp(ResourceResponse.class, "/api/3/action/resource_show", "id", id).result;
    }

    /**
     * Creates ckan resource on the server.
     *
     * @param resource
     *            ckan resource object with the minimal set of parameters
     *            required. See
     *            {@link CkanResource#CkanResource(String, String)}
     * @return the newly created resource
     * @throws JackanException
     * @since 0.4.1
     */
    public synchronized CkanResource createResource(CkanResourceBase resource) {
        checkNotNull(resource, "Need a valid resource!");

        checkToken("Tried to create resource " + resource.getName());

        ObjectMapper om = CkanClient.getObjectMapperForPosting(CkanResourceBase.class);
        String json = null;
        try {
            json = om.writeValueAsString(resource);
        } catch (IOException e) {
            throw new CkanException(COULDNT_JSONIZE + resource.getClass()
                                                              .getSimpleName(),
                    this, e);

        }

        if (resource.getUpload() == null) {
            return postHttp(ResourceResponse.class, "/api/3/action/resource_create", json,
                ContentType.APPLICATION_JSON).result;
        } else {
            // Could not find a way to create a resource with an attached file without enumerating all the resource
            // fields so doing it in two steps :
            //   First, create the resource as usual
            //   Then, update it with the file
            CkanResource resourceResponse = postHttp(ResourceResponse.class, "/api/3/action/resource_create", json,
                ContentType.APPLICATION_JSON).result;
            resource.setId(resourceResponse.getId());
            return postHttpResourceFile(ResourceResponse.class, "/api/3/action/resource_update", resource).result;
        }
    }

    /**
     * Updates a resource on the server using a straight {@code resource_update}
     * call. Null fields will not be sent and thus won't get updated, but be
     * careful about custom fields of {@link CkanResourceBase#getOthers()}, if
     * not sent they will be erased on the server! To prevent this behaviour,
     * see {@link #patchUpdateResource(CkanResourceBase) }
     *
     * @throws CkanException
     *             on error
     * @since 0.4.1
     */
    public synchronized CkanResource updateResource(CkanResourceBase resource) {
        checkNotNull(resource, "Need a valid resource!");
        checkToken("Tried to update resource" + resource.getName());

        String json = null;
        try {
            json = getObjectMapperForPosting(CkanResourceBase.class).writeValueAsString(resource);
        } catch (IOException ex) {
            throw new CkanException(COULDNT_JSONIZE + resource.getClass()
                                                              .getSimpleName(),
                    this, ex);

        }

        return postHttp(ResourceResponse.class, "/api/3/action/resource_update", json,
                ContentType.APPLICATION_JSON).result;

    }

    /**
     * Jackan specific. Patches a resource on the ckan server using a
     * {@code resource_update} call. Todo: this is a temporary solution until we
     * implement new {@code patch} api of CKAN 2.3
     *
     * @param resource
     *            ckan resource object. Fields set to {@code null} won't be
     *            updated on the server. Items present in lists such as
     *            {@link CkanResourceBase#getOthers() others} will be added to
     *            existing ones on the server. To support this behaviour
     *            provided {@code resource} might be patched with latest
     *            metadata from the server prior sending it for update.
     *
     * @see #updateResource(CkanResourceBase)
     * @throws CkanException
     *             on error
     * @since 0.4.1
     */
    public synchronized CkanResource patchUpdateResource(CkanResourceBase resource) {
        checkNotNull(resource, "Need a valid resource!");
        checkToken("Tried to update resource" + resource.getName());

        CkanResource origResource = getResource(resource.getId());
        // others
        Map<String, Object> newOthers = new HashMap();

        if (origResource.getOthers() != null) {
            newOthers.putAll(origResource.getOthers());
        }
        if (resource.getOthers() != null) {
            newOthers.putAll(resource.getOthers());
        }
        resource.setOthers(newOthers);

        String json = null;
        try {
            json = getObjectMapperForPosting(CkanResourceBase.class).writeValueAsString(resource);
        } catch (IOException ex) {
            throw new CkanException(COULDNT_JSONIZE + resource.getClass()
                                                              .getSimpleName(),
                    this, ex);

        }

        return postHttp(ResourceResponse.class, "/api/3/action/resource_update", json,
                ContentType.APPLICATION_JSON).result;

    }

    /**
     * Update the data file associated to this resource.
     *
     * @param resource
     *            a {@link CkanResourceBase} object with a data file set in the {@link CkanResourceBase#upload} field
     */
    public synchronized CkanResource updateResourceData(CkanResourceBase resource) {
        checkNotNull(resource);
        checkNotNull(resource.getUpload(), "Need a non empty file");
        checkToken("Tried to update resource" + resource.getName());

        return postHttpResourceFile(ResourceResponse.class, "/api/3/action/resource_update", resource).result;
    }

    /**
     * 
     * Marks a resource as 'deleted'.
     *
     * Note this will just set resource state to
     * {@link eu.trentorise.opendata.jackan.model.CkanState#deleted} and make it
     * inaccessible from the website, but you will still be able to get the
     * resource with the web api.
     *
     * @param id
     *            The alphanumerical id of the resource, such as
     *            d0892ada-b8b9-43b6-81b9-47a86be126db.
     *
     * @throws CkanException
     *             on error
     * @since 0.4.1
     */
    public synchronized void deleteResource(String id) {
        checkNotNull(id, "Need a valid id!");
        checkToken("Tried to delete resource with id " + id);

        String json = "{\"id\":\"" + id + "\"}";
        postHttp(ResourceResponse.class, "/api/3/action/resource_delete", json, ContentType.APPLICATION_JSON);
    }

    /**
     * Returns the groups present in Ckan.
     *
     * Notice that organizations will <i>not</i> be returned by this method. To
     * get them, use {@link #getOrganizationList() } instead.
     *
     * @throws JackanException
     *             on error
     */
    public synchronized List<CkanGroup> getGroupList() {
        return getHttp(GroupListResponse.class, "/api/3/action/group_list", "all_fields", "True").result;
    }

    /**
     * Return group names, like i.e. management-of-territory
     *
     * @throws JackanException
     *             on error
     */
    public synchronized List<String> getGroupNames() {
        return getHttp(GroupNamesResponse.class, "/api/3/action/group_list").result;
    }

    /**
     * Returns a Ckan group. Do not pass an organization id, to get organization
     * use {@link #getOrganization(java.lang.String) } instead.
     *
     * @param idOrName
     *            either the group name (i.e. hospitals-in-trento-district) or
     *            the group alphanumerical id (i.e.
     *            55bb5fbd-7a7c-4eb8-8b1a-1192a5504421)
     * @throws JackanException
     *             on error
     */
    public synchronized CkanGroup getGroup(String idOrName) {
        checkNotNull(idOrName, "Need a valid id or name!");
        return getHttp(GroupResponse.class, "/api/3/action/group_show", "id", idOrName, "include_datasets",
                "false").result;
    }

    /**
     * Returns the organizations present in CKAN.
     *
     * @see #getGroupList()
     *
     * @throws JackanException
     *             on error
     */
    public synchronized List<CkanOrganization> getOrganizationList() {
        return getHttp(OrganizationListResponse.class, "/api/3/action/organization_list", "all_fields", "True").result;
    }

    /**
     * Returns all the resource formats available in the catalog.
     *
     * @throws JackanException
     *             on error
     */
    public synchronized Set<String> getFormats() {
        return getHttp(FormatListResponse.class, "/api/3/action/format_autocomplete", "q", "", "limit", "1000").result;
    }

    /**
     * @throws JackanException
     *             on error
     */
    public synchronized List<String> getOrganizationNames() {
        return getHttp(GroupNamesResponse.class, "/api/3/action/organization_list").result;
    }

    /**
     * Returns a Ckan organization.
     *
     * @param idOrName
     *            either the name of organization (i.e. culture-and-education)
     *            or the alphanumerical id (i.e.
     *            232cad97-ecf2-447d-9656-63899023887f). Do not pass it a group
     *            id.
     * @throws JackanException
     *             on error
     */
    public synchronized CkanOrganization getOrganization(String idOrName) {
        checkNotNull(idOrName, "Need a valid id or name!");
        return getHttp(OrganizationResponse.class, "/api/3/action/organization_show", "id", idOrName,
                "include_datasets", "false").result;
    }

    /**
     * Creates CkanTag on the server.
     *
     * @param tag
     *            Ckan tag without id
     * @return the newly created tag
     * @throws JackanException
     */
    public synchronized CkanTag createTag(CkanTagBase tag) {
        checkNotNull(tag, "Need a valid tag!");

        checkToken("Tried to create tag" + tag.getName());

        String json = null;
        try {
            json = getObjectMapperForPosting(CkanTagBase.class).writeValueAsString(tag);
        } catch (IOException e) {
            throw new CkanException(COULDNT_JSONIZE + tag.getClass()
                                                         .getSimpleName(),
                    this, e);

        }
        TagResponse response = postHttp(TagResponse.class, "/api/3/action/tag_create", json,
                ContentType.APPLICATION_JSON);
        return response.result;
    }

    /**
     * Returns a list of tags names, i.e. "gp-practice-earnings","Aid Project
     * Evaluation", "tourism-satellite-account". We think names SHOULD be
     * lowercase with minuses instead of spaces, but in some cases they aren't.
     *
     * @throws JackanException
     *             on error
     */
    public synchronized List<CkanTag> getTagList() {
        return getHttp(TagListResponse.class, "/api/3/action/tag_list", "all_fields", "True").result;
    }

    /**
     * Returns tags containing the string given in query.
     *
     * @param query
     * @throws JackanException
     *             on error
     */
    public synchronized List<String> getTagNamesList(String query) {
        checkNotNull(query, "Need a valid query!");
        return getHttp(TagNamesResponse.class, "/api/3/action/tag_list", "query", query).result;
    }

    /**
     * @throws JackanException
     *             on error
     */
    public synchronized List<String> getTagNamesList() {
        return getHttp(TagNamesResponse.class, "/api/3/action/tag_list").result;
    }

    /**
     * Creates CkanVocabulary on the server.
     *
     * @param vocabulary
     *            Ckan vocabulary without id
     * @return the newly created vocabulary
     * @throws JackanException
     */
    public synchronized CkanVocabulary createVocabulary(CkanVocabularyBase vocabulary) {
        checkNotNull(vocabulary, "Need a valid vocabulary!");

        checkToken("Tried to create vocabulary" + vocabulary.getName());

        String json = null;
        try {
            json = getObjectMapperForPosting(CkanVocabularyBase.class).writeValueAsString(vocabulary);
        } catch (IOException e) {
            throw new CkanException(COULDNT_JSONIZE + vocabulary.getClass()
                                                                .getSimpleName(),
                    this, e);

        }
        VocabularyResponse response = postHttp(VocabularyResponse.class, "/api/3/action/vocabulary_create", json,
                ContentType.APPLICATION_JSON);
        return response.result;
    }

    /**
     * Search datasets containing provided text in the metadata
     *
     * @param text
     *            The query string
     * @param limit
     *            maximum results to return
     * @param offset
     *            search begins from offset. Starts from 0, so that offset 0
     *            limit 1 returns exactly 1 result, if there is a matching
     *            dataset)
     * @throws JackanException
     *             on error
     */
    public synchronized SearchResults<CkanDataset> searchDatasets(String text, int limit, int offset) {
        return searchDatasets(CkanQuery.filter()
                                       .byText(text),
                limit, offset);
    }

    /**
     * @param fqPrefix
     *            either "" or " AND "
     * @param list
     *            list of names of ckan objects
     */
    private static String appendNamesList(String fqPrefix, String key, List<String> list, StringBuilder fq) {
        checkNotNull(fqPrefix, "Need a valid prefix!");
        checkNotNull(key, "Need a valid key!");
        checkNotNull(list, "Need a valid list!");
        checkNotNull(fq, "Need a valid string builder!");

        if (list.size() > 0) {
            fq.append(fqPrefix)
              .append("(");
            String prefix = "";
            for (String n : list) {
                fq.append(prefix)
                  .append(key)
                  .append(":");
                fq.append('"' + n + '"');
                prefix = " AND ";
            }
            fq.append(")");
            return " AND ";
        } else {
            return "";
        }

    }

    /**
     * Parses a Ckan timestamp into a Java Timestamp.
     *
     * @throws IllegalArgumentException
     *             if timestamp can't be parsed.
     * @see #formatTimestamp(java.sql.Timestamp) for the inverse process.
     * @since 0.4.1
     */
    public static Timestamp parseTimestamp(String timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Found null timestamp!");
        }

        if (NONE.equals(timestamp)) {
            throw new IllegalArgumentException("Found timestamp with 'None' inside!");
        }

        return Timestamp.valueOf(timestamp.replace("T", " "));
    }

    /**
     * Formats a timestamp according to {@link #CKAN_TIMESTAMP_PATTERN}, with
     * precision up to microseconds.
     *
     * @see #parseTimestamp(java.lang.String) for the inverse process.
     * @since 0.4.1
     */
    @Nullable
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Found null timestamp!");
        }
        Timestamp ret = Timestamp.valueOf(timestamp.toString());
        ret.setNanos((timestamp.getNanos() / 1000) * 1000);
        return Strings.padEnd(ret.toString()
                                 .replace(" ", "T"),
                "1970-01-01T01:00:00.000001".length(), '0');
    }

    /**
     * @params s a string to encode in a format suitable for URLs.
     */
    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8")
                             .replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ex) {
            throw new JackanException("Unsupported encoding", ex);
        }
    }

    /**
     * Search datasets according to the provided query.
     *
     * @param query
     *            The query object
     * @param limit
     *            maximum results to return
     * @param offset
     *            search begins from offset
     * @throws CkanException
     *             on error
     */
    public synchronized SearchResults<CkanDataset> searchDatasets(CkanQuery query, int limit, int offset) {
        checkNotNull(query, "Need a valid query!");

        StringBuilder params = new StringBuilder();

        params.append("rows=")
              .append(limit)
              .append("&start=")
              .append(offset);

        if (query.getText()
                 .length() > 0) {
            params.append("&q=");
            params.append(urlEncode(query.getText()));
        }

        StringBuilder fq = new StringBuilder();
        String fqPrefix = "";

        fqPrefix = appendNamesList(fqPrefix, "groups", query.getGroupNames(), fq);
        fqPrefix = appendNamesList(fqPrefix, "organization", query.getOrganizationNames(), fq);
        fqPrefix = appendNamesList(fqPrefix, "tags", query.getTagNames(), fq);
        fqPrefix = appendNamesList(fqPrefix, "license_id", query.getLicenseIds(), fq);

        if (fq.length() > 0) {
            params.append("&fq=")
                  .append(urlEncode(fq.insert(0, "(")
                                      .append(")")
                                      .toString()));
        }

        DatasetSearchResponse dsr;
        dsr = getHttp(DatasetSearchResponse.class, "/api/3/action/package_search?" + params.toString());

        for (CkanDataset ds : dsr.result.getResults()) {
            for (CkanResource cr : ds.getResources()) {
                cr.setPackageId(ds.getId());
            }
        }

        return dsr.result;
    }

    /**
     * @msg the prepended error message.
     * @throws CkanException
     */
    private void checkToken(@Nullable String prependedErrorMessage) {
        if (ckanToken == null) {
            throw new CkanException(String.valueOf(prependedErrorMessage) + ", but ckan token was not set!", this);
        }
    }

    /**
     * Creates CkanDataset on the server. Will also create eventual resources
     * present in the dataset.
     *
     * @param dataset
     *            Ckan dataset without id
     * @return the newly created dataset
     * @throws CkanException
     * @since 0.4.1
     */
    public synchronized CkanDataset createDataset(CkanDatasetBase dataset) {
        checkNotNull(dataset, "Need a valid dataset!");

        checkToken("Tried to create dataset" + dataset.getName());

        String json = null;
        try {

            json = getObjectMapperForPosting(CkanDatasetBase.class).writeValueAsString(dataset);
        } catch (IOException e) {
            throw new CkanException(COULDNT_JSONIZE + dataset.getClass()
                                                             .getSimpleName(),
                    this, e);
        }

        DatasetResponse response = postHttp(DatasetResponse.class, "/api/3/action/package_create", json,
                ContentType.APPLICATION_JSON);

        return response.result;
    }

    /**
     * Updates a dataset on the ckan server using a straight
     * {@code package_update} call. Null fields will not be sent and thus won't
     * get updated, but be careful about list fields, if not sent they will be
     * erased on the server! To prevent this behaviour, see
     * {@link #patchUpdateDataset(CkanDatasetBase)}
     *
     * @throws CkanException
     *             on error
     * @since 0.4.1
     */
    public synchronized CkanDataset updateDataset(CkanDatasetBase dataset) {
        checkNotNull(dataset, "Need a valid dataset!");

        checkToken("Tried to update dataset" + dataset.getName());

        String json = null;
        try {
            json = getObjectMapperForPosting(CkanDatasetBase.class).writeValueAsString(dataset);
        } catch (IOException ex) {
            throw new CkanException(COULDNT_JSONIZE + dataset.getClass()
                                                             .getSimpleName(),
                    this, ex);

        }

        return postHttp(DatasetResponse.class, "/api/3/action/package_update", json,
                ContentType.APPLICATION_JSON).result;

    }

    public static List<CkanPair> extrasMapToList(Map<String, String> map) {
        ArrayList ret = new ArrayList();

        for (String key : map.keySet()) {
            ret.add(new CkanPair(key, map.get(key)));
        }
        return ret;
    }

    private void mergeResources(@Nullable List<CkanResource> resourcesToMerge, List<CkanResource> targetResources) {
        if (resourcesToMerge != null) {
            for (CkanResource resourceToMerge : resourcesToMerge) {
                boolean replaced = false;
                for (int i = 0; i < targetResources.size(); i++) {
                    CkanResource targetRes = targetResources.get(i);
                    if (resourceToMerge.getId() != null && resourceToMerge.getId()
                                                                          .equals(targetRes.getId())) {
                        targetResources.set(i, resourceToMerge);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    targetResources.add(resourceToMerge);
                }
            }
        }
    }

    private void mergeGroups(@Nullable List<CkanGroup> groupsToMerge, List<CkanGroup> targetGroups) {
        if (groupsToMerge != null) {
            for (CkanGroup groupToMerge : groupsToMerge) {
                boolean replaced = false;
                for (int i = 0; i < targetGroups.size(); i++) {
                    CkanGroup targetRes = targetGroups.get(i);
                    if (groupToMerge.getId() != null && groupToMerge.getId()
                                                                    .equals(targetRes.getId())) {
                        targetGroups.set(i, groupToMerge);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    targetGroups.add(groupToMerge);
                }
            }
        }
    }

    private void mergeRelationships(@Nullable List<CkanDatasetRelationship> relationshipsToMerge,
            List<CkanDatasetRelationship> targetDatasetRelationships) {
        if (relationshipsToMerge != null) {
            for (CkanDatasetRelationship relationshipToMerge : relationshipsToMerge) {
                boolean replaced = false;
                for (int i = 0; i < targetDatasetRelationships.size(); i++) {
                    CkanDatasetRelationship targetRes = targetDatasetRelationships.get(i);
                    if (relationshipToMerge.getId() != null && relationshipToMerge.getId()
                                                                                  .equals(targetRes.getId())) {
                        targetDatasetRelationships.set(i, relationshipToMerge);
                        replaced = true;
                        break;

                    }
                }
                if (!replaced) {
                    targetDatasetRelationships.add(relationshipToMerge);
                }
            }
        }
    }

    private void mergeTags(@Nullable List<CkanTag> tagsToMerge, List<CkanTag> targetTags) {
        if (tagsToMerge != null) {
            for (CkanTag tagToMerge : tagsToMerge) {
                boolean replaced = false;
                for (int i = 0; i < targetTags.size(); i++) {
                    CkanTag targetRes = targetTags.get(i);
                    if (tagToMerge.getId() != null && tagToMerge.getId()
                                                                .equals(targetRes.getId())) {
                        targetTags.set(i, tagToMerge);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    targetTags.add(tagToMerge);
                }
            }
        }
    }

    /**
     * Jackan specific. Patches a dataset on the ckan server using a
     * {@code package_update} call. Todo: this is a temporary solution until we
     * implement new {@code patch} api of CKAN 2.3
     *
     * @param dataset
     *            ckan dataset object. Fields set to {@code null} won't be
     *            updated on the server. Items present in lists such as
     *            {@code resources} or {@code extras} will be added to existing
     *            ones on the server. To support this behaviour provided
     *            {@code dataset} might be patched with latest metadata from the
     *            server prior sending it for update.
     *
     * @throws CkanException
     *             on error
     * @since 0.4.1
     */
    public synchronized CkanDataset patchUpdateDataset(CkanDatasetBase dataset) {
        checkNotNull(dataset, "Need a valid dataset!");

        checkToken("Tried to patch update dataset" + dataset.getName());

        CkanDataset origDataset = getDataset(dataset.idOrName());

        // others
        Map<String, Object> newOthers = new HashMap();

        if (origDataset.getOthers() != null) {
            newOthers.putAll(origDataset.getOthers());
        }
        if (dataset.getOthers() != null) {
            newOthers.putAll(dataset.getOthers());
        }
        dataset.setOthers(newOthers);

        // extras
        if (dataset.getExtras() == null) {
            dataset.setExtras(origDataset.getExtras());
        } else {
            Map<String, String> newExtras = new HashMap();

            if (origDataset.getExtras() != null) {
                newExtras.putAll(origDataset.getExtrasAsHashMap());
            }
            if (dataset.getExtras() != null) {
                newExtras.putAll(dataset.getExtrasAsHashMap());
            }
            dataset.setExtras(extrasMapToList(newExtras));
        }

        // resources
        List<CkanResource> newResources = new ArrayList();
        mergeResources(origDataset.getResources(), newResources);
        mergeResources(dataset.getResources(), newResources);
        dataset.setResources(newResources);

        // groups
        List<CkanGroup> newGroups = new ArrayList();
        mergeGroups(origDataset.getGroups(), newGroups);
        mergeGroups(dataset.getGroups(), newGroups);
        dataset.setGroups(newGroups);

        // tags
        List<CkanTag> newTags = new ArrayList();
        mergeTags(origDataset.getTags(), newTags);
        mergeTags(dataset.getTags(), newTags);
        dataset.setTags(newTags);

        // relationships as subject
        List<CkanDatasetRelationship> newRelationshipsAsSubject = new ArrayList();
        mergeRelationships(origDataset.getRelationshipsAsSubject(), newRelationshipsAsSubject);
        mergeRelationships(dataset.getRelationshipsAsSubject(), newRelationshipsAsSubject);
        dataset.setRelationshipsAsSubject(newRelationshipsAsSubject);

        // relationships as object
        List<CkanDatasetRelationship> newRelationshipsAsObject = new ArrayList();
        mergeRelationships(origDataset.getRelationshipsAsObject(), newRelationshipsAsObject);
        mergeRelationships(dataset.getRelationshipsAsObject(), newRelationshipsAsObject);
        dataset.setRelationshipsAsObject(newRelationshipsAsObject);

        String json = null;
        try {
            json = getObjectMapperForPosting(CkanDatasetBase.class).writeValueAsString(dataset);
        } catch (IOException ex) {
            throw new JackanException(COULDNT_JSONIZE + dataset.getClass()
                                                               .getSimpleName(),
                    ex);

        }

        return postHttp(DatasetResponse.class, "/api/3/action/package_update", json,
                ContentType.APPLICATION_JSON).result;

    }

    /**
     * Marks a dataset as 'deleted'.
     *
     * Note this will just set dataset state to
     * {@link eu.trentorise.opendata.jackan.model.CkanState#deleted} and make it
     * inaccessible from the website, but you will still be able to get the
     * dataset with the web api. Resources contained within will still be
     * 'active'.
     *
     * @param nameOrId
     *            either the dataset name (i.e. apple-production) or the the
     *            alphanumerical id (i.e. fe507a10-4c49-4b18-8bf6-6705198cfd42)
     *
     * @throws CkanException
     *             on error
     */
    public synchronized void deleteDataset(String nameOrId) {
        checkNotNull(nameOrId, "Need a valid name or id!");

        checkToken("Tried to delete dataset" + nameOrId);

        String json = "{\"id\":\"" + nameOrId + "\"}";
        postHttp(CkanResponse.class, "/api/3/action/package_delete", json, ContentType.APPLICATION_JSON);
    }

    /**
     * Creates CkanOrganization on the server.
     *
     * @param organization
     *            requires at least the name or id. Only non-null fields of
     *            {@link CkanGroupOrgBase} will be sent to server.
     * @return a new object with the created organization.
     * @throws CkanException
     *             on error.
     * @since 0.4.1
     */
    public synchronized CkanOrganization createOrganization(CkanOrganization organization) {
        checkNotNull(organization, "Need a valid " + organization + "!");

        checkToken("Tried to create organization " + organization.getName());

        String json = null;
        try {
            json = getObjectMapperForPosting(CkanOrganization.class).writeValueAsString(organization);
        } catch (IOException e) {
            throw new CkanException(COULDNT_JSONIZE + organization.getClass()
                                                                  .getSimpleName(),
                    this, e);

        }
        return postHttp(OrganizationResponse.class, "/api/3/action/organization_create", json,
                ContentType.APPLICATION_JSON).result;
    }

    /**
     * Creates CkanGroup on the server.
     *
     * @param group
     *            requires at least the name or id. Only non-null fields of
     *            {@link CkanGroupOrgBase} will be sent to server.
     * @return a new object with the created group.
     * @throws CkanException
     *             on error.
     * @since 0.4.1
     */
    public synchronized CkanGroup createGroup(CkanGroup group) {
        checkNotNull(group, "Need a valid " + group + "!");

        checkToken("Tried to create group " + group.idOrName());

        String json = null;
        try {
            json = getObjectMapperForPosting(CkanGroup.class).writeValueAsString(group);
        } catch (IOException e) {
            throw new CkanException(COULDNT_JSONIZE + group.getClass()
                                                           .getSimpleName(),
                    this, e);

        }
        return postHttp(GroupResponse.class, "/api/3/action/group_create", json, ContentType.APPLICATION_JSON).result;
    }

    /**
     * Returns the proxy used by the client.
     *
     * @since 0.4.1
     */
    @Nullable
    public String getProxy() {
        return proxy.toURI();
    }

    /**
     * Convenience method to create a Builder with provided client to modify.
     * <p>
     * <strong>WARNING:</strong> The passed client will be modified, so
     * <strong> DO NOT </strong> pass an already built client.
     * </p>
     * <p>
     * The builder is not threadsafe and you can use one builder instance to
     * build only one client instance.
     * </p>
     */
    protected static CkanClient.Builder newBuilder(CkanClient client) {
        return new Builder(client);
    }

}

class DatasetResponse extends CkanResponse {

    public CkanDataset result;
}

class ResourceResponse extends CkanResponse {

    public CkanResource result;
}

class DatasetListResponse extends CkanResponse {

    public List<String> result;
}

class UserListResponse extends CkanResponse {

    public List<CkanUser> result;
}

class UserResponse extends CkanResponse {

    public CkanUser result;
}

class TagListResponse extends CkanResponse {

    public List<CkanTag> result;
}

class OrganizationResponse extends CkanResponse {

    public CkanOrganization result;
}

class GroupResponse extends CkanResponse {

    public CkanGroup result;
}

class OrganizationListResponse extends CkanResponse {

    public List<CkanOrganization> result;
}

class GroupListResponse extends CkanResponse {

    public List<CkanGroup> result;
}

class GroupNamesResponse extends CkanResponse {

    public List<String> result;
}

class TagNamesResponse extends CkanResponse {

    public List<String> result;
}

class TagResponse extends CkanResponse {

    public CkanTag result;
}

class VocabularyResponse extends CkanResponse {

    public CkanVocabulary result;
}

class DatasetSearchResponse extends CkanResponse {

    public SearchResults<CkanDataset> result;
}

class LicenseListResponse extends CkanResponse {

    public List<CkanLicense> result;
}

class FormatListResponse extends CkanResponse {

    public Set<String> result;
}

class ApiVersionResponse {

    public int version;
}
