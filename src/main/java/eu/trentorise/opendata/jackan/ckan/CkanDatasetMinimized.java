package eu.trentorise.opendata.jackan.ckan;

import java.util.List;

/** The class contains a  minmum set of fields that are used to create dataset
 * Created by Ivan Tankoyeu on 24/05/2014.
 */
public class CkanDatasetMinimized {

    private String name;
    private String url;
    private List<CkanPair> extras;


    public CkanDatasetMinimized(String name, String url, List<CkanPair> extras) {
        this.name = name;
        this.url = url;
        this.extras = extras;
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
