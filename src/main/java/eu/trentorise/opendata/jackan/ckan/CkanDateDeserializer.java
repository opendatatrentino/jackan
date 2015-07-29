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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import static eu.trentorise.opendata.jackan.ckan.CkanClient.CKAN_TIMESTAMP_PATTERN;
import static eu.trentorise.opendata.jackan.ckan.CkanClient.NONE;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import org.apache.http.client.utils.DateUtils;


/**
 * Super custom deserializer to handle dates with 'None' inside, inconsistent
 * patterns and other crap.
 *
 * @author David Leoni
 */
class CkanDateDeserializer extends JsonDeserializer<Date> {

    /**
     * Found pattern "2013-12-17T00:00:00" in resource.date_modified in
     * dati.toscana:
     * http://dati.toscana.it/api/3/action/package_show?id=alluvioni_bacreg See
     * also  <a href="https://github.com/ckan/ckan/issues/1874"> ckan issue 874
     * </a> and
     * <a href="https://github.com/ckan/ckan/pull/2519">ckan pull 2519</a>
     */
    public static final String CKAN_NO_MILLISECS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String[] ACCEPTED_FORMATS = {CKAN_TIMESTAMP_PATTERN, CKAN_NO_MILLISECS_PATTERN};

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();

        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (NONE.equals(str)) {
                return null;
            }
            try {
                return DateUtils.parseDate(str, ACCEPTED_FORMATS);
            }
            catch (Exception ex) {
                throw new JackanDateException("Unrecognized date " + str + ", supported formats are " + Arrays.toString(ACCEPTED_FORMATS), ex);
            }

        }

        if (t == JsonToken.VALUE_NULL) {
            return null;
        }
        throw new JackanDateException("Unsupported json token " + t.toString());

    }

}

class JackanDateException extends JsonProcessingException {

    JackanDateException(String msg) {
        super(msg);
    }

    JackanDateException(String msg, Throwable tr) {
        super(msg, tr);
    }

}