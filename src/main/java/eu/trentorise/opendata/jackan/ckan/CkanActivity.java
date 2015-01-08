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

import java.util.List;
import javax.annotation.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author David Leoni
 */
public class CkanActivity {

    /**
     * i.e. "ddb21e57-da76-4dc1-a815-4edd0e9e332e"
     */
    private String id;
    /**
     * i.e. "2013-03-08T09:31:20.833590"
     */
    private DateTime timestamp;
    /**
     * i.e. "Impostazioni modificate."
     */
    private String message;
    /**
     * i.e. "admin"
     */
    private String author;
    @Nullable
    private DateTime approvedTimestamp;
    private List<CkanDataset> packages;
    private List<CkanGroup> groups;
    private CkanState state;

    public CkanActivity(){
        
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * internally date is stored with UTC timezone
     */
    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Nullable
    public DateTime getApprovedTimestamp() {
        return approvedTimestamp;
    }

    /**
     * internally date is stored with UTC timezone
     *
     * @param approvedTimestamp
     */
    public void setApprovedTimestamp(@Nullable DateTime approvedTimestamp) {
        if (approvedTimestamp != null) {
            this.approvedTimestamp = approvedTimestamp.toDateTime(DateTimeZone.UTC);
        } else {
            this.approvedTimestamp = null;
        }
    }

    public List<CkanDataset> getPackages() {
        return packages;
    }

    public void setPackages(List<CkanDataset> packages) {
        this.packages = packages;
    }

    public List<CkanGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CkanGroup> groups) {
        this.groups = groups;
    }

    public CkanState getState() {
        return state;
    }

    public void setState(CkanState state) {
        this.state = state;
    }
}
