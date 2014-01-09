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
import java.util.ArrayList;

import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.SearchResults;

/**
 * Threadsafe
 * 
 * @author David Leoni
 * 
 */
public class CkanClient {
	private static ObjectMapper objectMapper;
	private final String url;
	private static final org.slf4j.Logger logger = LoggerFactory
			.getLogger(CkanClient.class);

	public static ObjectMapper getObjectMapper() {
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
							false); // not good for unmodifiable collections, if
									// we will ever use any

		}
		return objectMapper;
	}

	/**
	 * 
	 * @param url
	 *            i.e. http://data.gov.uk
	 */
	public CkanClient(String url) {
		this.url = url;
	}

	/**
	 * Closes all the connections.
	 */
	void close() {
		// doing nothing for now
	}

	/**
	 * 
	 * @param <T>
	 * @param responseType
	 *            a descendant of CkanResponse
	 * @param path
	 *            something like /api/3/package_show
	 * @param params
	 *            list of key, value parameters. They must be not be url
	 *            encoded. i.e. "id","laghi-monitorati-trento"
	 * @return
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
	 * Throws JackanException on error.
	 * 
	 * @return list of strings like i.e. limestone-pavement-orders
	 */
	public synchronized ArrayList<String> getDatasetList() {
		return getHttp(DatasetListResponse.class, "/api/3/action/package_list").result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @return list of data names like i.e. limestone-pavement-orders
	 */
	public synchronized ArrayList<String> getDatasetList(Integer limit,
			Integer offset) {
		return getHttp(DatasetListResponse.class, "/api/3/action/package_list",
				"limit", limit, "offset", offset).result;
	}

	/**
	 * id should be the alphanumerical id like
	 * 96b8aae4e211f3e5a70cdbcbb722264256ae2e7d. Using the mnemonic like
	 * laghi-monitorati-trento is discouraged. Throws JackanException on error.
	 */
	public synchronized CkanDataset getDataset(String id) {
		return getHttp(DatasetResponse.class, "/api/3/action/package_show",
				"id", id).result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @return
	 */
	public synchronized ArrayList<CkanUser> getUserList() {
		return getHttp(UserListResponse.class, "/api/3/action/user_list").result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @param id
	 *            i.e. 'admin'
	 * @return
	 */
	public synchronized CkanUser getUser(String id) {
		return getHttp(UserResponse.class, "/api/3/action/user_show", "id", id).result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @param id
	 * @return
	 */
	public synchronized CkanResource getResource(String id) {
		return getHttp(ResourceResponse.class, "/api/3/action/resource_show",
				"id", id).result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @return
	 */
	public synchronized ArrayList<CkanGroup> getGroupList() {
		return getHttp(GroupListResponse.class, "/api/3/action/group_list",
				"all_fields", "True").result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @return
	 */
	public synchronized ArrayList<String> getGroupNames() {
		return getHttp(GroupNamesResponse.class, "/api/3/action/group_List").result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @param id
	 * @return
	 */
	public synchronized CkanGroup getGroup(String id) {
		return getHttp(GroupResponse.class, "/api/3/action/group_show", "id",
				id).result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @param vocabularyId
	 * @return
	 */
	public synchronized ArrayList<CkanTag> getTagList() {
		return getHttp(TagListResponse.class, "/api/3/action/tag_list",
				"all_fields", "True").result;
	}

	/**
	 * Only tags containg string given in query will be returned Throws
	 * JackanException on error.
	 * 
	 * @param query
	 * @param vocabularyId
	 * @return
	 */
	public synchronized ArrayList<String> getTagNamesList(String query) {
		return getHttp(TagNamesResponse.class, "/api/3/action/tag_list",
				"query", query).result;
	}

	/**
	 * Throws JackanException on error.
	 * 
	 * @return
	 */
	public synchronized ArrayList<String> getTagNamesList() {
		return getHttp(TagNamesResponse.class, "/api/3/action/tag_list").result;
	}

	/**
	 * Search datasets containg param text in the metadata Throws
	 * JackanException on error.
	 * 
	 * @param text
	 * @param limit
	 *            maximum results to return
	 * @param offset
	 *            search begins from offset
	 * @return
	 */
	public synchronized SearchResults<CkanDataset> searchDatasets(String text,
			int limit, int offset) {
		DatasetSearchResponse dsr = getHttp(DatasetSearchResponse.class,
				"/api/3/action/package_search", "q", text, "rows", limit,
				"start", offset);
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
		return "";// Ckan error of type: " + getType() + "\t message:" +
					// getMessage() ;
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
	 * @return
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
	public ArrayList<String> result;
}

class UserListResponse extends CkanResponse {
	public ArrayList<CkanUser> result;
}

class UserResponse extends CkanResponse {
	public CkanUser result;
}

class TagListResponse extends CkanResponse {
	public ArrayList<CkanTag> result;
}

class GroupResponse extends CkanResponse {
	public CkanGroup result;
}

class GroupListResponse extends CkanResponse {
	public ArrayList<CkanGroup> result;
}

class GroupNamesResponse extends CkanResponse {
	public ArrayList<String> result;
}

class TagNamesResponse extends CkanResponse {
	public ArrayList<String> result;
}

class DatasetSearchResponse extends CkanResponse {
	public SearchResults<CkanDataset> result;
}
