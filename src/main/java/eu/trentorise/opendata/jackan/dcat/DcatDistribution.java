/** *****************************************************************************
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
 *
 * @author David Leoni
 */
public class DcatDistribution {
    
    private String URI;    
    /**
     * i.e. dcat:byteSize "5120"^^xsd:decimal ;
     */
    private int byteSize;
    /**
     * dcat:accessURL . For relation with Dataset.landingPage see http://www.w3.org/TR/vocab-dcat/#a-dataset-available-only-behind-some-web-page
     */
    private String accessURL;
    
    /**
     * not in standard, added for convenience
    */
    private String datasetIdentifier;
    
    /**
     * dct:description
     */
    private String description;
    /**
     * i.e. dcat:downloadURL <http://www.example.org/files/001.csv> ;
     * For relation with Dataset.landingPage and see http://www.w3.org/TR/vocab-dcat/#a-dataset-available-only-behind-some-web-page
     */
    private String downloadURL;
    /**
     * dct:format
     */
    private String format;
    /**
     * i.e. dct:issued "2011-12-05"^^xsd:date ;
     */
    private String issued;
    
    
    /**
     * dct:license
    */
    private String license;
    /**
     * i.e. dcat:mediaType "text/csv" ;
     */
    private String mediaType;
    /**
     * i.e. dct:modified "2011-12-05"^^xsd:date ;
     */
    private String modified;  
   
    
    /**
     * dct:rights
     */
    private String rights;
    
  /**
     * i.e. dct:spatial <http://www.geonames.org/6695072> ;
     */
    private String spatial;   
    
    /**
     * Human readable name
     * i.e. dct:title "CSV distribution of imaginary dataset 001" ;
     */
    private String title;

    public int getByteSize() {
        return byteSize;
    }
    
    
    
    public void setByteSize(int byteSize) {
        this.byteSize = byteSize;
    }

    public String getAccessURL() {
        return accessURL;
    }

    public void setAccessURL(String accessURL) {
        this.accessURL = accessURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
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

    /**
     * Property not in Dcat standard, added for convenience
    */
    public String getDatasetIdentifier() {
        return datasetIdentifier;
    }

    /**
     * Property not in Dcat standard, added for convenience
    */
    public void setDatasetIdentifier(String datasetIdentifier) {
        this.datasetIdentifier = datasetIdentifier;
    }

    public String getSpatial() {
        return spatial;
    }

    public void setSpatial(String spatial) {
        this.spatial = spatial;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }
    
}
