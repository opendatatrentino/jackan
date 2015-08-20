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

import java.util.List;

/**
 *
 * @author David Leoni
 * @since 0.4.1
 */
public class CkanVocabularyBase {
    
    private String id;
    private String name;
    private List<CkanTag> tags;

    public CkanVocabularyBase() {
    }   
    
    /**
     * Constructor with required fields for vocabulary creation.
     * @param name the name of the new vocabulary, e.g. 'Genre'
     * @param tags 
     */
    public CkanVocabularyBase(String name, List<CkanTag> tags) {
        this();
        this.name = name;
        this.tags = tags;
    }
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The name of the new vocabulary, e.g. 'Genre'     
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the new vocabulary, e.g. 'Genre'     
     */    
    public void setName(String name) {
        this.name = name;
    }

    public List<CkanTag> getTags() {
        return tags;
    }

    public void setTags(List<CkanTag> tags) {
        this.tags = tags;
    }
    
    
    
}
