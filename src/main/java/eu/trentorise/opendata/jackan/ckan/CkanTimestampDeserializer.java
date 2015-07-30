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
import java.io.IOException;
import java.sql.Timestamp;


/**
 * Super custom deserializer to handle dates with 'None' inside, inconsistent
 * patterns and other crap.
 *
 * @author David Leoni
 */
class CkanTimestampDeserializer extends JsonDeserializer<Timestamp> {
   

    @Override
    public Timestamp deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();

        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            try {
                return CkanClient.parseTimestamp(str);
            } catch(Exception ex){            
                throw new JackanTimestampException("Unrecognized timestamp " + str, ex);
            }

        }

        if (t == JsonToken.VALUE_NULL) {
            return null;
        }
        throw new JackanTimestampException("Couldn't parse timestamp!");
    }

}

class JackanTimestampException extends JsonProcessingException {

    JackanTimestampException(String msg) {
        super(msg);
    }

    JackanTimestampException(String msg, Throwable tr) {
        super(msg, tr);
    }

}
