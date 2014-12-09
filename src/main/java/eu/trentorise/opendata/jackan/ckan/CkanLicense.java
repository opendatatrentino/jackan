package eu.trentorise.opendata.jackan.ckan;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author David Leoni
 */
public class CkanLicense {

    private String status;

    private String maintainer;

    private String family;

    private String title;

    private boolean domainData;

    private boolean okdCompliant;

    private boolean domainContent;

    private String url;

    private boolean osiCompliant;

    private boolean domainSoftware;

    /**
     * i.e. "cc-zero"
     */
    private String id;

    /** 
     * Returns true if it complies with the OpenDefinition: http://opendefinition.org/
     */
    @JsonProperty("is_okd_compliant")
    public boolean isOkdCompliant() {
        return okdCompliant;
    }

    /** 
     * @param okdCompliant True if it complies with the OpenDefinition: http://opendefinition.org/
     * 
     * @see #setOsiCompliant(boolean) 
     */    
    public void setOkdCompliant(boolean okdCompliant) {
        this.okdCompliant = okdCompliant;
    }    

    /** 
     * Returns true if it complies with the Open Source Initiative? http://opensource.org/licenses
     */    
    @JsonProperty("is_osi_compliant")
    public boolean isOsiCompliant() {
        return osiCompliant;
    }

    /**
     * 
     * @param osiCompliant True if it complies with the Open Source Initiative? http://opensource.org/licenses
     * 
     * @see #setOkdCompliant(boolean) 
     */
    public void setOsiCompliant(boolean osiCompliant) {
        this.osiCompliant = osiCompliant;
    }    
    
    /**
     * Returns true if the license applies to content domain.
     *
     * @see #isDomainSoftware()
     * @see #isDomainData()
     */
    @JsonProperty("domain_content")
    public boolean isDomainContent() {
        return domainContent;
    }

    /**
     * @param domainContent True if the license applies to content domain.
     *
     * @see #setDomainSoftware(boolean) 
     * @see #setDomainData(boolean) 
     */        
    public void setDomainContent(boolean domainContent) {
        this.domainContent = domainContent;
    }
    

    /**
     * True if the license applies to data domain.
     *
     * @see #isDomainContent()
     * @see #isDomainSoftware()
     */
    @JsonProperty("domain_data")
    public boolean isDomainData() {
        return domainData;
    }
    
    /**
     * @param domainData True if the license applies to data domain.
     *
     * @see #setDomainSoftware(boolean) 
     * @see #setDomainContent(boolean) 
     */    
    public void setDomainData(boolean domainData) {
        this.domainData = domainData;
    }
    

    /**
     * True if the license applies to software domain.
     *
     * @see #isDomainContent()
     * @see #isDomainData()
     */
    @JsonProperty("domain_software")
    public boolean isDomainSoftware() {
        return domainSoftware;
    }
    
    /**
     * @param domainSoftware True if the license applies to software domain.
     *
     * @see #setDomainData(boolean) 
     * @see #setDomainContent(boolean) 
     */        
    public void setDomainSoftware(boolean domainSoftware) {
        this.domainSoftware = domainSoftware;
    }         

    /**
     * Gets the status, i.e. "active" todo check possaible status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status, i.e. "active" todo check possaible status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    /**
     * Gets the title, i.e. "Creative Commons CCZero"
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title, i.e. "Creative Commons CCZero"
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

   /**
    * Returns the url of a document describing the license
    * i.e. "http://creativecommons.org/publicdomain/zero/1.0/deed.it",
    */
    public String getUrl() {
        return url;
    }

   /**
    * Sets the url of a document describing the license
    * i.e. "http://creativecommons.org/publicdomain/zero/1.0/deed.it",
    */    
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the id of the license as used by ckan, i.e. "cc-zero"
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the license as used by ckan, i.e. "cc-zero"
     */    
    public void setId(String id) {
        this.id = id;
    }

}
