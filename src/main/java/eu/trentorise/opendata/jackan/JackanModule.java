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
package eu.trentorise.opendata.jackan;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import static eu.trentorise.opendata.jackan.CkanClient.formatTimestamp;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom Jackson module to serialize/deserialize as JSON Ckan objects with
 * fields lower cased (i.e. 'author_email') and also Timestamp, which in Ckan
 * has format like "2013-11-11T04:12:11.110868", see
 * {@link CkanClient#CKAN_TIMESTAMP_PATTERN}. In case there are problems in
 * parsing deserializes them to null.
 *
 * NOTE: We made a custom module because when reading dates, Jackson defaults to
 * using GMT for all processing unless specifically told otherwise, see
 * < href="http://wiki.fasterxml.com/JacksonFAQTimestampHandling" target="_blank">Jackson
 * FAQ</a>. When writing dates, Jackson would also add a Z for timezone and add
 * +1 for GMT, which we don't want.
 *
 * @author David Leoni
 */
public class JackanModule extends SimpleModule {

    private static final Logger LOG = Logger.getLogger(JackanModule.class.getName());

    public JackanModule() {

        setNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        addSerializer(Timestamp.class, new StdSerializer<Timestamp>(Timestamp.class) {
            @Override
            public void serialize(Timestamp value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                try {
                    String str = formatTimestamp(value);
                    jgen.writeString(str);
                }
                catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Couldn't format timestamp " + value + ", writing 'null'", ex);
                    jgen.writeNull();
                }
            }

        });

        addDeserializer(Timestamp.class, new JsonDeserializer<Timestamp>() {

            @Override
            public Timestamp deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                JsonToken t = jp.getCurrentToken();

                if (t == JsonToken.VALUE_STRING) {
                    String str = jp.getText().trim();
                    try {
                        return CkanClient.parseTimestamp(str);
                    }
                    catch (IllegalArgumentException ex) {
                        LOG.log(Level.SEVERE, "Couldn't parse timestamp " + str + ", returning null", ex);
                        return null;
                    }
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
