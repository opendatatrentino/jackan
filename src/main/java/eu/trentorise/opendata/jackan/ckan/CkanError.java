/*
 * Copyright 2015 Trento Rise.
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
import com.fasterxml.jackson.databind.JsonNode;
import eu.trentorise.opendata.jackan.JackanException;
import java.io.IOException;

/**
 *
 * @author David Leoni
 */
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

    /**
     * todo what are possible types?
     */
    @JsonProperty("__type")
    public String getType() {
        return type;
    }

    /**
     * todo what are possible types?
     */
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
        }
        catch (IOException ex) {
            throw new JackanException("Couldn parse CkanError.", ex);
        }
    }

}