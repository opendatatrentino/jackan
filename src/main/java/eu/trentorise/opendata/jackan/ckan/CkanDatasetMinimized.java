package eu.trentorise.opendata.jackan.ckan;

import static eu.trentorise.opendata.traceprov.TraceProvUtils.checkNonEmpty;
import static eu.trentorise.opendata.traceprov.TraceProvUtils.checkNonNull;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.Immutable;

/** The class contains a  minimum set of fields that are used to create dataset
 * 
 * @deprecated use {@link eu.trentorise.opendata.jackan.ckan.CkanDataset} instead.
 * Created by Ivan Tankoyeu on 24/05/2014.
 */
@Immutable
public final class CkanDatasetMinimized {

    private String name;
    private String url;
    private List<CkanPair> extras;
    private String title;
    private String licenseId;


    public CkanDatasetMinimized(){
        this.name = "";
        this.url = "";
        this.extras = new ArrayList();
        this.title = "";
        this.licenseId = "";
    }
    
    /**
     * 
     * @param name
     * @param url A page URL containg a description of the semantified dataset columns and the trasformations done on the original dataset. This URL will be also displayed as metadata in the catalog under dcat:landingPage
     * @param extras
     * @param title
     * @param licenseId 
     */
    public CkanDatasetMinimized(String name, String url, List<CkanPair> extras, String title, String licenseId) {
        this();
        checkNonEmpty(name, "ckan dataset name");
        checkNonNull(url, "ckan dataset url to description page");
        checkNonNull(extras, "ckan dataset extras");
        checkNonEmpty(title, "ckan dataset title");
        checkNonNull(licenseId, "ckan dataset license id");
        this.name = name;
        this.url = url;
        this.title = title;
        this.licenseId = licenseId;
        this.extras = extras;
    }

    public String getTitle() {
        return title;
    }

    

    public String getLicenseId() {
        return licenseId;
    }

    
    public String getName() {
        return name;
    }

    

    public String getUrl() {
        return url;
    }

    

    public List<CkanPair> getExtras() {
        return extras;
    }
    
}
