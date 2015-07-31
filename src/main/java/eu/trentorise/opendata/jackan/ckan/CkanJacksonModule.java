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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import static eu.trentorise.opendata.jackan.ckan.CkanClient.formatTimestamp;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * So we can serialize/deserialize to Timestamp. In case there are problems in parsing deserializes to null.
 * @author David Leoni
 */
public class CkanJacksonModule extends SimpleModule {

    private static final Logger LOG = Logger.getLogger(CkanJacksonModule.class.getName());

    public CkanJacksonModule() {
        
        addSerializer(Timestamp.class, new StdSerializer<Timestamp>(Timestamp.class) {
            @Override
            public void serialize(Timestamp value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeString(formatTimestamp(value));
            }

        });

        addDeserializer(Timestamp.class, new JsonDeserializer<Timestamp>() {

            @Override
            public Timestamp deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                JsonToken t = jp.getCurrentToken();

                if (t == JsonToken.VALUE_STRING) {
                    String str = jp.getText().trim();                    
                    return CkanClient.parseTimestamp(str);                    
                }

                if (t == JsonToken.VALUE_NULL) {
                    return null;
                }

                LOG.log(Level.SEVERE, "Unrecognized json token for timestamp {0}, returning null", t.asString());
                return null;

            }

        });
    }
}
