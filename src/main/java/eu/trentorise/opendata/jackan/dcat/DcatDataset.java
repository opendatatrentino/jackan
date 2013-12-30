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

import java.util.ArrayList;

/**
 *
 * @author David Leoni
 */
public class DcatDataset {
    /**
    i.e. dct:accrualPeriodicity <http://purl.org/linked-data/sdmx/2009/code#freq-W>  ;
    * 
    */
    private String accrualPeriodicity;
    
    /**
     * dcat:contactPoint <http://example.org/transparency-office/contact> ;
     * mmm should be in vcard-rdf format: http://www.w3.org/TR/vcard-rdf/
     */
    private String contactPoint;    
    private String description;
        
    private ArrayList<DcatDistribution> distributions;
    /**
     * Let's keep it a String for now
     * i.e. dct:issued "2011-12-05"^^xsd:date ;
     */
    private String issued;
    private String identifier;
    /**
     * i.e. dcat:keyword "accountability","transparency" ,"payments" ;
     */
    private ArrayList<String> keywords;
    
    /**
     * For relation with Distribution.accessURL and downloadURL see http://www.w3.org/TR/vocab-dcat/#a-dataset-available-only-behind-some-web-page
     */
    private String landingPage;
    
    /**
     *  i.e. dct:language <http://id.loc.gov/vocabulary/iso639-1/en>  ;    
    */
    private String language;    
    /**
     * Let's keep it a String for now
     * i.e. dct:modified "2011-12-05"^^xsd:date ;
     */
    private String modified;
    
    /**
     * i.e.  dct:publisher :finance-ministry ;
     */
    private FoafAgent publisher;
    
    /**
     * i.e. dct:spatial <http://www.geonames.org/6695072> ;
     */
    private String spatial;    
    
    /**
     * i.e. "Imaginary dataset"
     */ 
    private String title;
    
    /**
     * i.e. dct:temporal <http://reference.data.gov.uk/id/quarter/2006-Q1> ;
     */
    private String temporal;
    
    /**
     * 
     */
    private SkosConcept theme;

    public String getAccrualPeriodicity() {
        return accrualPeriodicity;
    }

    public void setAccrualPeriodicity(String accrualPeriodicity) {
        this.accrualPeriodicity = accrualPeriodicity;
    }

    public String getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(String contactPoint) {
        this.contactPoint = contactPoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<DcatDistribution> getDistributions() {
        return distributions;
    }

    public void setDistributions(ArrayList<DcatDistribution> distributions) {
        this.distributions = distributions;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getSpatial() {
        return spatial;
    }

    public void setSpatial(String spatial) {
        this.spatial = spatial;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemporal() {
        return temporal;
    }

    public void setTemporal(String temporal) {
        this.temporal = temporal;
    }

    public SkosConcept getTheme() {
        return theme;
    }

    public void setTheme(SkosConcept theme) {
        this.theme = theme;
    }

    
}
