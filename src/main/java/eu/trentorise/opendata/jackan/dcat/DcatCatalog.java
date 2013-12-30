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

import java.util.ArrayList;


/**
    
 * @author David Leoni
 */
public class DcatCatalog {

    
    private ArrayList<DcatDataset> datasets;
    
    /**
     * dct:description
     */
    private String description;
    
    /**
     * foaf:homepage
     */
    private String homepage;    
    
    /**
     * i.e.  dct:issued "2011-12-11"^^xsd:date ;
     */
    private String issued;
    
  /**
     *  i.e. dct:language <http://id.loc.gov/vocabulary/iso639-1/en>  ;    
    */
    private String language;    

   /**
     * dct:license
    */
    private String license;
   
    /**
     * dct:modified
     */
    private String modified;
    
    /**
     * foaf:Agent
     */
    private FoafAgent publisher;
    
    private ArrayList<DcatCatalogRecord> records;    
    
   /**
     * dct:rights
     */
    private String rights;    
    
    /**
     * dct:title
     */
    private String title;    

    public ArrayList<DcatDataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(ArrayList<DcatDataset> datasets) {
        this.datasets = datasets;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public FoafAgent getPublisher() {
        return publisher;
    }

    public void setPublisher(FoafAgent publisher) {
        this.publisher = publisher;
    }

    public ArrayList<DcatCatalogRecord> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<DcatCatalogRecord> records) {
        this.records = records;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
