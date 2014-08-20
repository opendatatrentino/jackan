package eu.trentorise.opendata.jackan.ckan;

import java.util.List;

/** The class contains a  minmum set of fields that are used to create dataset
 * Created by Ivan Tankoyeu on 24/05/2014.
 */
public class CkanDatasetMinimized {

    private String name;
    private String url;
    private List<CkanPair> extras;
    private String title;
    private String licenseId;


    /**
     * 
     * @param name
     * 
     * @param url A page URL containg a description of the semantified dataset columns and the trasformations done on the original dataset. This URL will be also displayed as metadata in the catalog under dcat:landingPage
               

     * @param extras
     * @param title
     * @param licenseId 
     */
    public CkanDatasetMinimized(String name, String url, List<CkanPair> extras, String title, String licenseId) {
        this.name = name;
        this.url = url;
        this.title = title;
        this.licenseId = licenseId;
        this.extras = extras;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<CkanPair> getExtras() {
        return extras;
    }

    public void setExtras(List<CkanPair> extras) {
        this.extras = extras;
    }
}
