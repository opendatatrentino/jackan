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
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import static eu.trentorise.opendata.jackan.ckan.CkanClient.NONE;
import java.io.IOException;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;

/**
 * Super custom deserializer to handle dates with 'None' inside.
 *
 * @author David Leoni
 */
class CkanDateDeserializer extends DateTimeDeserializer {

    public CkanDateDeserializer() {
        super(DateTime.class);
    }

    @Override
    public ReadableDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();

        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (NONE.equals(str)) {
                return null;
            }
        }

        return super.deserialize(jp, ctxt);
    }

    /**
     * Jeez... Using this crap copied from DateTimeDeserializer to overcome Java
     * mysterious type errors.
     */
    @SuppressWarnings("unchecked")
    public static <T extends ReadableInstant> JsonDeserializer<T> forType(Class<T> cls) {
        return (JsonDeserializer<T>) new CkanDateDeserializer();
    }

};
