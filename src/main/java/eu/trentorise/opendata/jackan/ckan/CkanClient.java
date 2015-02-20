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
package eu.trentorise.opendata.jackan.ckan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.SearchResults;
import static eu.trentorise.opendata.traceprov.impl.TraceProvUtils.checkNonEmpty;
import static eu.trentorise.opendata.traceprov.impl.TraceProvUtils.removeTrailingSlash;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Class to access a ckan instance. Threadsafe.
 *
 * @author David Leoni, Ivan Tankoyeu
 *
 */
public class CkanClient {

    public static final String CKAN_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Nullable
    private static ObjectMapper objectMapper;
    private final String catalogURL;
    @Nullable
    private final String ckanToken;
    
    public static Logger logger = Logger.getLogger(CkanClient.class.getName());

    
    
    /**
     * @return a clone of the json object mapper used internally.
     */
    public static ObjectMapper getObjectMapperClone() {
        return getObjectMapper().copy();
    }

    /**
     * Retrieves the Jackson object mapper. Internally, Object mapper is
     * initialized at first call.
     */
    static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper
                    .setPropertyNamingStrategy(
                            PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
                    .configure(
                            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                            false) // let's be tolerant
                    .configure(
                            MapperFeature.USE_GETTERS_AS_SETTERS,
                            false) // not good for unmodifiable collections, if we will ever use any
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            // When reading dates, Jackson defaults to using GMT for all processing unless specifically told otherwise, see http://wiki.fasterxml.com/JacksonFAQDateHandling
            // When writing dates, Jackson would add a Z for timezone by CKAN doesn't use it, i.e.  "2013-11-11T04:12:11.110868"                            so we removed it here
            objectMapper.setDateFormat(new SimpleDateFormat(CKAN_DATE_PATTERN)); // but this only works for Java Dates

            // taken solution from here: http://www.lorrin.org/blog/2013/06/28/custom-joda-time-dateformatter-in-jackson/
            objectMapper.registerModule(new JodaModule());

            objectMapper.registerModule(new SimpleModule() {
                {
                    addSerializer(DateTime.class, new StdSerializer<DateTime>(DateTime.class) {
                        @Override
                        public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
                            jgen.writeString(DateTimeFormat.forPattern(CKAN_DATE_PATTERN).print(value));
                        }

                    });
                }
            });
        }
        return objectMapper;
    }

    /**
     * @param url i.e. http://data.gov.uk
     */
    public CkanClient(String url) {
        this(url, null);
    }

    /**
     * @param URL i.e. http://data.gov.uk
     * @param token the private token string for ckan repository
     */
    public CkanClient(String URL, @Nullable String token) {
        this.catalogURL = removeTrailingSlash(URL);
        this.ckanToken = token;
    }

    /**
     * Method for http GET
     *
     * @param <T>
     * @param responseType a descendant of CkanResponse
     * @param path something like /api/3/package_show
     * @param params list of key, value parameters. They must be not be url
     * encoded. i.e. "id","laghi-monitorati-trento"
     * @throws JackanException on error
     */
    <T extends CkanResponse> T getHttp(Class<T> responseType, String path,
            Object... params) {
        try {

            StringBuilder sb = new StringBuilder().append(catalogURL).append(path);
            for (int i = 0; i < params.length; i += 2) {
                sb.append(i == 0 ? "?" : "&")
                        .append(URLEncoder.encode(params[i].toString(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(params[i + 1].toString(),
                                        "UTF-8"));
            }
            String fullUrl = sb.toString();
            logger.log(Level.FINE, "getting {0}", fullUrl);
            String json = Request.Get(fullUrl).execute().returnContent()
                    .asString();
            T dr = getObjectMapper().readValue(json, responseType);
            if (!dr.success) {
                // monkey patching error type
                throw new JackanException(
                        "Reading from catalog " + catalogURL + " was not successful. Reason: "
                        + CkanError.read(getObjectMapper()
                                .readTree(json).get("error").asText())
                );
            }
            return dr;
        } catch (Exception ex) {
            throw new JackanException("Error while getting dataset.", ex);
        }
    }

    <T extends CkanResponse> T postHttp(Class<T> responseType, String path, String input, ContentType contentType,
            Object... params) {
        try {

            StringBuilder sb = new StringBuilder().append(catalogURL).append(path);
            for (int i = 0; i < params.length; i += 2) {
                sb.append(i == 0 ? "?" : "&")
                        .append(URLEncoder.encode(params[i].toString(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(params[i + 1].toString(),
                                        "UTF-8"));
            }
            String fullUrl = sb.toString();
            
            logger.log(Level.FINE, "posting {0}", fullUrl);
            Response response = Request.Post(fullUrl).bodyString(input, contentType).addHeader("Authorization", ckanToken).execute();
//            HttpResponse entity =response.returnResponse();
//            System.out.println(entity.toString());

            Content out = response.returnContent();
            String json = out.asString();            
            logger.log(Level.FINE, "json {0}", json);

            T dr = getObjectMapper().readValue(json, responseType);
            if (!dr.success) {
                // monkey patching error type
                throw new JackanException(
                        "posting to catalog " + catalogURL + " was not successful. Reason: "
                        + CkanError.read(getObjectMapper()
                                .readTree(json).get("error").asText())
                );
            }
            return dr;
        } catch (Exception ex) {
            throw new JackanException("Error while uploading the semantified dataset.", ex);
        }

    }

    public String getCatalogURL() {
        return catalogURL;
    }

    public String getCkanToken() {
        return ckanToken;
    }

    /**
     * Given some dataset parameters, reconstruct the URL of dataset page in the
     * catalog website.
     *
     * Valid URLs have this format with the name:
     * http://dati.trentino.it/dataset/impianti-di-risalita-vivifiemme-2013
     *
     * but it is preferable to use the id:
     * http://dati.trentino.it/dataset/418d662e-aa94-44bc-b281-d689aeec27ac
     *
     * @param datasetIdentifier either the alphanumerical id of the dataset or
     * the name. Using the id is preferable.
     * @return
     */
    public static String makeDatasetURL(String catalogUrl, String datasetIdentifier) {
        return removeTrailingSlash(catalogUrl) + "/dataset/" + datasetIdentifier;
    }

    /**

     * Given some resource parameters, reconstruct the URL of resource page in the
     * catalog website.
     *
     * Valid URLs have this format with the name:
     * http://dati.trentino.it/dataset/impianti-di-risalita-vivifiemme-2013/resource/779d1d9d-9370-47f4-a194-1b0328c32f02
     *
     * but it is preferable to use the id:
     * http://dati.trentino.it/dataset/418d662e-aa94-44bc-b281-d689aeec27ac/resource/779d1d9d-9370-47f4-a194-1b0328c32f02
     *
     * @param catalogUrl i.e. http://data.gov.uk
     * @param datasetIdentifier alphanumerical id or the dataset name. The id is
     * preferable.
     * @param resourceId the alphanumerical id of the resource
     * @return
     */
    public static String makeResourceURL(String catalogUrl, String datasetIdentifier, String resourceId) {
        checkNonEmpty(catalogUrl, "catalog url");
        checkNonEmpty(datasetIdentifier, "dataset identifier");
        checkNonEmpty(resourceId, "resource id");
        return catalogUrl + "/" + datasetIdentifier + "/resource/" + resourceId;
    }

    /**
     * Creates ckan resource on the server
     *
     * @param resource ckan resource object with theminimal set of parameters
     * @return the newly created resource
     * @throws JackanException
     */
    public synchronized CkanResource createResource(CkanResourceMinimized resource) {

        if (ckanToken == null) {
            throw new JackanException("Tried to create resource" + resource.getName() + ", but ckan token was not set!");
        }

        ObjectMapper objectMapper = CkanClient.getObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(resource);
        } catch (IOException e) {
            throw new JackanException("Couldn't serialize the provided CkanResourceMinimized!", e);
        }
        return postHttp(ResourceResponse.class, "/api/3/action/resource_create", json, ContentType.APPLICATION_JSON).result;
    }

    /**
     * Updates ckan resource on the server
     *
     * @param resource ckan resource object with theminimal set of parameters
     * @return the updated resource
     * @throws JackanException
     */
    public synchronized CkanResource updateResource(CkanResourceMinimized resource) {

        if (ckanToken == null) {
            throw new JackanException("Tried to update resource" + resource.getName() + ", but ckan token was not set!");
        }

        ObjectMapper objectMapper = CkanClient.getObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(resource);
        } catch (IOException e) {
            throw new JackanException("Couldn't serialize the provided CkanResourceMinimized!", e);
        }

        return postHttp(ResourceResponse.class, "/api/3/action/resource_update", json, ContentType.APPLICATION_JSON).result;

    }

    /**
     * Creates CkanDataset on the server
     *
     * @param dataset data set with a given parameters
     * @return the newly created dataset
     * @throws JackanException
     */
    public synchronized CkanDataset createDataset(CkanDatasetMinimized dataset) {

        if (ckanToken == null) {
            throw new JackanException("Tried to create dataset" + dataset.getName() + ", but ckan token was not set!");
        }

        ObjectMapper objectMapper = CkanClient.getObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(dataset);
        } catch (IOException e) {
            throw new JackanException("Couldn't serialize the provided CkanDatasetMinimized!", e);
        }
        DatasetResponse datasetresponse = postHttp(DatasetResponse.class, "/api/3/action/package_create", json, ContentType.APPLICATION_JSON);
        return datasetresponse.result;
    }

    /**
     * @return list of strings like i.e. limestone-pavement-orders
     * @throws JackanException on error
     */
    public synchronized List<String> getDatasetList() {
        return getHttp(DatasetListResponse.class, "/api/3/action/package_list").result;
    }

    /**
     *
     * @param limit
     * @param offset Starts with 0 included. getDatasetList(1,0) will return
     * exactly one dataset, if catalog is not empty.
     * @return list of data names like i.e. limestone-pavement-orders
     * @throws JackanException on error
     */
    public synchronized List<String> getDatasetList(Integer limit,
            Integer offset) {
        return getHttp(DatasetListResponse.class, "/api/3/action/package_list",
                "limit", limit, "offset", offset).result;
    }

    /**
     * @param id should be the alphanumerical id like
     * 96b8aae4e211f3e5a70cdbcbb722264256ae2e7d. Using the mnemonic like
     * laghi-monitorati-trento is discouraged.
     *
     * @throws JackanException on error
     */
    public synchronized CkanDataset getDataset(String id) {
        CkanDataset cd = getHttp(DatasetResponse.class, "/api/3/action/package_show",
                "id", id).result;
        for (CkanResource cr : cd.getResources()) {
            cr.setDatasetId(cd.getId());
        }
        return cd;
    }

    /**
     * @throws JackanException on error
     */
    public synchronized List<CkanUser> getUserList() {
        return getHttp(UserListResponse.class, "/api/3/action/user_list").result;
    }

    /**
     * @param id i.e. 'admin'
     * @throws JackanException on error
     */
    public synchronized CkanUser getUser(String id) {
        return getHttp(UserResponse.class, "/api/3/action/user_show", "id", id).result;
    }

    /**
     * @param id The alphanumerical id of the resource, such as
     * d0892ada-b8b9-43b6-81b9-47a86be126db. It is also possible to pass the
     * name, such
     * @throws JackanException on error
     */
    public synchronized CkanResource getResource(String id) {
        return getHttp(ResourceResponse.class, "/api/3/action/resource_show",
                "id", id).result;
    }

    /**
     * @throws JackanException on error
     */
    public synchronized List<CkanGroup> getGroupList() {
        return getHttp(GroupListResponse.class, "/api/3/action/group_list",
                "all_fields", "True").result;
    }

    /**
     * @throws JackanException on erroR
     */
    public synchronized List<String> getGroupNames() {
        return getHttp(GroupNamesResponse.class, "/api/3/action/group_List").result;
    }

    /**
     * @param id
     * @throws JackanException on error
     */
    public synchronized CkanGroup getGroup(String id) {
        return getHttp(GroupResponse.class, "/api/3/action/group_show", "id",
                id).result;
    }

    /**
     * @throws JackanException on error
     */
    public synchronized List<CkanGroup> getOrganizationList() {
        return getHttp(GroupListResponse.class, "/api/3/action/organization_list",
                "all_fields", "True").result;
    }

    /**
     * @throws JackanException on erroR
     */
    public synchronized List<String> getOrganizationNames() {
        return getHttp(GroupNamesResponse.class, "/api/3/action/organization_List").result;
    }

    /**
     * In Ckan an organization is actually a group
     *
     * @param id
     * @throws JackanException on error
     */
    public synchronized CkanGroup getOrganization(String id) {
        return getHttp(GroupResponse.class, "/api/3/action/organization_show", "id",
                id).result;
    }

    /**
     * Returns a list of tags names, i.e. "gp-practice-earnings","Aid Project
     * Evaluation", "tourism-satellite-account" We think names SHOULD be
     * lowercase with minuses instead of spaces, but in some cases they aren't.
     *
     * @throws JackanException on error
     */
    public synchronized List<CkanTag> getTagList() {
        return getHttp(TagListResponse.class, "/api/3/action/tag_list",
                "all_fields", "True").result;
    }

    /**
     * Only tags containg string given in query will be returned Throws
     * JackanException on error.
     *
     * @param query
     * @throws JackanException on error
     */
    public synchronized List<String> getTagNamesList(String query) {
        return getHttp(TagNamesResponse.class, "/api/3/action/tag_list",
                "query", query).result;
    }

    /**
     * @throws JackanException on error
     */
    public synchronized List<String> getTagNamesList() {
        return getHttp(TagNamesResponse.class, "/api/3/action/tag_list").result;
    }

    /**
     * Search datasets containg param text in the metadata
     *
     * @param text The query string
     * @param limit maximum results to return
     * @param offset search begins from offset
     * @throws JackanException on error
     */
    public synchronized SearchResults<CkanDataset> searchDatasets(String text,
            int limit, int offset) {
        return searchDatasets(CkanQuery.filter().byText(text), limit, offset);
    }

    /**
     * @param fqPrefix either "" or " AND "
     * @param list list of names of ckan objects
     */
    private static String appendNamesList(String fqPrefix, String key, List<String> list, StringBuilder fq) {
        if (list.size() > 0) {
            fq.append(fqPrefix)
                    .append("(");
            String prefix = "";
            for (String n : list) {
                fq.append(prefix).append(key).append(":");
                fq.append('"' + n + '"');
                prefix = " AND ";
            }
            fq.append(")");
            return " AND ";
        } else {
            return "";
        }

    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ex) {
            throw new JackanException("Unsupported encoding", ex);
        }
    }

    /**
     * Search datasets containg param text in the metadata
     *
     * @param query The query object
     * @param limit maximum results to return
     * @param offset search begins from offset
     * @throws JackanException on error
     */
    public synchronized SearchResults<CkanDataset> searchDatasets(
            CkanQuery query,
            int limit,
            int offset
    ) {

        StringBuilder params = new StringBuilder();

        params.append("rows=").append(limit)
                .append("&start=").append(offset);

        if (query.getText().length() > 0) {
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
                    .append(urlEncode(fq.insert(0, "(").append(")").toString()));
        }

        DatasetSearchResponse dsr;
        dsr = getHttp(DatasetSearchResponse.class,
                "/api/3/action/package_search?" + params.toString());

        if (dsr.success) {
            for (CkanDataset ds : dsr.result.getResults()) {
                for (CkanResource cr : ds.getResources()) {
                    cr.setDatasetId(ds.getId());
                }
            }
        }

        return dsr.result;
    }

}

class CkanError {

    private String message;
    /**
     * actually the original is __type
     */
    private String type;

    @Override
    public String toString() {
        return "Ckan error of type: " + getType() + "\t message:" + getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("__type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * To overcome the __type problem. Tried many combinations but Jackson is
     * not collaborating here, even if in Group.isOrganization case setting the
     * JsonProperty("is_organization") did work.
     */
    static CkanError read(String json) {
        try {
            CkanError ce = new CkanError();
            JsonNode jn = CkanClient.getObjectMapper().readTree(json);
            ce.setMessage(jn.get("message").asText());
            ce.setType(jn.get("__type").asText());
            return ce;
        } catch (IOException ex) {
            throw new JackanException("Couldn parse CkanError.", ex);
        }
    }

}

/**
 * @author David Leoni
 */
class CkanResponse {

    public String help;
    public boolean success;
    public CkanError error;

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

class GroupResponse extends CkanResponse {

    public CkanGroup result;
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

class DatasetSearchResponse extends CkanResponse {

    public SearchResults<CkanDataset> result;
}
