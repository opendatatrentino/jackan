/**
 * *****************************************************************************
 * Copyright 2013-2014 Trento Rise (www.trentorise.eu/)
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License (LGPL)
 * version 2.1 which accompanies this distribution, and is available at
 *
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************
 */
package eu.trentorise.opendata.jackan.ckan;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.SearchResults;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import javax.annotation.Nullable;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * Class to access a ckan instance. Threadsafe.
 *
 * @author David Leoni
 *
 */
public class CkanClient {
    @Nullable
    private static ObjectMapper objectMapper;
    private final String url;
    private static final org.slf4j.Logger logger = LoggerFactory
            .getLogger(CkanClient.class);

    /**
     * @return a clone of the json object mapper used internally.
     */
    public static ObjectMapper getObjectMapperClone() {
        ObjectMapper om = getObjectMapper();
        return new ObjectMapper()
                .setSerializationConfig(om.getSerializationConfig())
                .setDeserializationConfig(om.getDeserializationConfig());
    }

    /**
     * Retrieves the Jackson object mapper. Internally, Object mapper is initialized at first call.
     */
    static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper
                    .setPropertyNamingStrategy(
                            PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
                    .configure(
                            DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                            false) // let's be tolerant
                    .configure(
                            DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS,
                            false) // not good for unmodifiable collections, if we will ever use any
                    .configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);

            // When reading dates, Jackson defaults to using GMT for all processing unless specifically told otherwise, see http://wiki.fasterxml.com/JacksonFAQDateHandling
            // When writing dates, Jackson would add a Z for timezone by CKAN doesn't use it, i.e.  "2013-11-11T04:12:11.110868"                            so we removed it here
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));

        }
        return objectMapper;
    }

    /**
     *
     * @param url i.e. http://data.gov.uk
     */
    public CkanClient(String url) {
        this.url = url;
    }

    /**
     * Method for http GET
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

            StringBuilder sb = new StringBuilder().append(url).append(path);
            for (int i = 0; i < params.length; i += 2) {
                sb.append(i == 0 ? "?" : "&")
                        .append(URLEncoder.encode(params[i].toString(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(params[i + 1].toString(),
                                        "UTF-8"));
            }
            String fullUrl = sb.toString();
            logger.debug("getting " + fullUrl);
            String json = Request.Get(fullUrl).execute().returnContent()
                    .asString();
            T dr = getObjectMapper().readValue(json, responseType);
            if (!dr.success) {
                // monkey patching error type
                throw new JackanException(
                        "Reading from the catalog was not successful. Reason: "
                        + CkanError.read(getObjectMapper()
                                .readTree(json).get("error").asText()));
            }
            return dr;
        } catch (Exception ex) {
            throw new JackanException("Error while getting dataset.", ex);
        }
    }

    /**
     *
     * @return list of strings like i.e. limestone-pavement-orders
     * @throws JackanException on error
     */
    public synchronized List<String> getDatasetList() {
        return getHttp(DatasetListResponse.class, "/api/3/action/package_list").result;
    }

    /**
     *
     * @return list of data names like i.e. limestone-pavement-orders
     * @throws JackanException on error
     */
    public synchronized List<String> getDatasetList(Integer limit,
            Integer offset) {
        return getHttp(DatasetListResponse.class, "/api/3/action/package_list",
                "limit", limit, "offset", offset).result;
    }

    /**
     * id should be the alphanumerical id like
     * 96b8aae4e211f3e5a70cdbcbb722264256ae2e7d. Using the mnemonic like
     * laghi-monitorati-trento is discouraged. 
     *
     * @throws JackanException on error
     */
    public synchronized CkanDataset getDataset(String id) {
        return getHttp(DatasetResponse.class, "/api/3/action/package_show",
                "id", id).result;
    }

    /**
     * @throws JackanException on error
     */
    public synchronized List<CkanUser> getUserList() {
        return getHttp(UserListResponse.class, "/api/3/action/user_list").result;
    }

    /**     
     *
     * @throws JackanException on error
     * @param id i.e. 'admin'
     */
    public synchronized CkanUser getUser(String id) {
        return getHttp(UserResponse.class, "/api/3/action/user_show", "id", id).result;
    }

    /**     
     * @param id The alphanumerical id of the resource, such as d0892ada-b8b9-43b6-81b9-47a86be126db. It is also possible to pass the name, such 
     * @throws JackanException on error     
     */
    public synchronized CkanResource getResource(String id) {
        return getHttp(ResourceResponse.class, "/api/3/action/resource_show",
                "id", id).result;
    }

    /**
     * @throws JackanException on error
     *
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
     * @throws JackanException on error
     *
     * @param id
     */
    public synchronized CkanGroup getGroup(String id) {
        return getHttp(GroupResponse.class, "/api/3/action/group_show", "id",
                id).result;
    }

    /**
     *
     * @throws JackanException on error
     *
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
     * @throws JackanException on error
     *
     * @param id
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
     *
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
     *
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
                fq.append( '"' + n + '"' ) ;
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
     *
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
