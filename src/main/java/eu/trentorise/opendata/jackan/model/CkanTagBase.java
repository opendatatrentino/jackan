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
package eu.trentorise.opendata.jackan.model;

/**
 * A Ckan Tag. Tags can be free or belong to a controlled vocabulary.
 *
 * {@link CkanTag} holds fields that can be sent when
 * <a href="http://docs.ckan.org/en/latest/api/index.html?#ckan.logic.action.create.tag_create" target="_blank">creating
 * a tag</a>, while {@link CkanTag} holds more fields that can be returned with
 * searches. Notice free tags can be created by just adding them to a dataset to
 * create.
 *
 * This class initializes nothing to fully preserve all we get from ckan. In
 * practice, all fields of retrieved resources can be null except maybe
 * {@code name}.
 *
 * @author David Leoni
 * @since 0.4.1
 */
public class CkanTagBase {

    private String id;
    private String name;
    private String vocabularyId;

    public CkanTagBase() {
    }

    /**
     * You can use this constructor when adding a free tag to a dataset.
     *
     * @param name the name for the new tag, a string between 2 and 100
     * characters long containing only alphanumeric characters and -, _ and .,
     * e.g. 'Jazz'
     */
    public CkanTagBase(String name) {
        this();
        this.name = name;
    }

    /**
     * You can use this constructor when creating a tag associated to a
     * controlled vocabulary
     *
     * @param name the name for the new tag, a string between 2 and 100
     * characters long containing only alphanumeric characters and -, _ and .,
     * e.g. 'Jazz'
     */
    public CkanTagBase(String name, String vocabularyId) {
        this(name);
        this.vocabularyId = vocabularyId;
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

    public String getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

}
