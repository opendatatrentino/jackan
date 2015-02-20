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

import javax.annotation.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author David Leoni
 */
public class CkanTag {
    private String vocabularyId;
    private String displayName;
    private String name;
    
    @Nullable private DateTime revisionTimestamp;
    @Nullable private CkanState state;
    private String id;

    public String getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable public DateTime getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(@Nullable DateTime revisionTimestamp) {
        if (revisionTimestamp != null) {
            this.revisionTimestamp = revisionTimestamp.toDateTime(DateTimeZone.UTC);
        } else {
            this.revisionTimestamp = null;
        }
    }

    @Nullable public CkanState getState() {
        return state;
    }

    public void setState(@Nullable CkanState state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
