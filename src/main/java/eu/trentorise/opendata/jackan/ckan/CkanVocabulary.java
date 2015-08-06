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

import java.util.List;

/**
 *
 * @author David Leoni
 */
public class CkanVocabulary extends CkanVocabularyBase {

    
    public CkanVocabulary() {
        super();
    }   
    
    /**
     * Constructor with required fields for vocabulary creation.
     * @param name the name of the new vocabulary, e.g. 'Genre'
     * @param tags 
     */
    public CkanVocabulary(String name, List<CkanTag> tags) {
        super(name, tags);
    }
    
}
