/* 
 * Copyright 2015 Trento Rise  (trentorise.eu) 
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

import java.util.Date;
import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class CkanTag {

    private String vocabularyId;
    private String displayName;
    private String name;

    @Nullable
    private Date revisionTimestamp;
    @Nullable
    private CkanState state;
    private String id;

    public CkanTag() {
    }

    public String getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    /**
     *
     * @return a human readable name, i.e. "Habitat Quality"
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param displayName a human readable name, i.e. "Habitat Quality"
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @return a human readable name, i.e. "Habitat Quality"
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name a human readable name, i.e. "Habitat Quality"
     */
    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public Date getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(@Nullable Date revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;

    }

    @Nullable
    public CkanState getState() {
        return state;
    }

    public void setState(@Nullable CkanState state) {
        this.state = state;
    }

    /**
     *
     * @return alphanumerical id, i.e. "7f0aa2fe-9733-4ce2-a351-d10278ba44ac"
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id alphanumerical id, i.e. "7f0aa2fe-9733-4ce2-a351-d10278ba44ac"
     */
    public void setId(String id) {
        this.id = id;
    }
}
