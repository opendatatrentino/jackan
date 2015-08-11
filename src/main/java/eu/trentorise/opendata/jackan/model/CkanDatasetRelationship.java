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
 *
 * @author David Leoni
 */
public class CkanDatasetRelationship {

    private String comment;
    private String id;
    private String object;
    private String subject;
    private String type;

    public CkanDatasetRelationship() {
    }

    /**
     * Constructor with the minal amount of fields required for creation
     */
    public CkanDatasetRelationship(String subject, String object, String type) {
        this.object = object;
        this.subject = subject;
        this.type = type;
    }
    

    /**
     * A comment about the relationship
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The id or name of the dataset that is the object of the relationship
     *
     */
    public String getObject() {
        return object;
    }

    /**
     * The id or name of the dataset that is the object of the relationship
     *
     */
    public void setObject(String object) {
        this.object = object;
    }

    /**
     * The id or name of the dataset that is the subject of the relationship
     */
    public String getSubject() {
        return subject;
    }

    /**
     * The id or name of the dataset that is the subject of the relationship
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * The type of the relationship, one of 'depends_on', 'dependency_of',
     * 'derives_from', 'has_derivation', 'links_to', 'linked_from', 'child_of'
     * or 'parent_of'
     *
     */
    public String getType() {
        return type;
    }

    /**
     * The type of the relationship, one of 'depends_on', 'dependency_of',
     * 'derives_from', 'has_derivation', 'links_to', 'linked_from', 'child_of'
     * or 'parent_of'
     *
     */
    public void setType(String type) {
        this.type = type;
    }
}
