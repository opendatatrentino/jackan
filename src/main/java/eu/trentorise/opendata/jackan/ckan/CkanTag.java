/**
* *****************************************************************************
* Copyright 2013-2014 Trento Rise (www.trentorise.eu/)
*
* All rights reserved. This program and the accompanying materials are made
* available under the terms of the GNU Lesser General Public License (LGPL)
* version 2.1 which accompanies this distribution, and is available at
*
* http://www.gnu.org/licenses/lgpl-2.1.html
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*
*******************************************************************************
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

    public CkanTag(){}
    
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
