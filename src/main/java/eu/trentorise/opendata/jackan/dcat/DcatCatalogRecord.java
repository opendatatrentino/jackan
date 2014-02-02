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

package eu.trentorise.opendata.jackan.dcat;

/**
 * If the catalog publisher decides to keep metadata describing its records (i.e. the records containing metadata describing the datasets), dcat:CatalogRecord can be used. For example, while  :dataset-001 was issued on 2011-12-05, its description on Imaginary Catalog was added on 2011-12-11. This can be represented by DCAT as in the following:

   :catalog  dcat:record :record-001  .
   :record-001
       a dcat:CatalogRecord ;
       foaf:primaryTopic :dataset-001 ;
       dct:issued "2011-12-11"^^xsd:date ;
       .  
 * @author David Leoni
 */
public class DcatCatalogRecord {
    
    private String URI;
    
    /**
     * dct:title
     */
    private String title;
    
    /**
     * dct:description
     */
    private String description;
    /**
     * i.e.  dct:issued "2011-12-11"^^xsd:date ;
     */
    private String issued;
    /**
     * dct:modified
     */
    private String modified;
    private DcatDataset primaryTopic;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public DcatDataset getPrimaryTopic() {
        return primaryTopic;
    }

    public void setPrimaryTopic(DcatDataset primaryTopic) {
        this.primaryTopic = primaryTopic;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }
}
