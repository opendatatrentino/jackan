package eu.trentorise.opendata.jackan.ckan;

/**
 * Created by Ivan Tankoyeu on 23/05/2014.
 */
public class CkanResourceMinimized {

    private String format;
    private String name;
    private String url;
    private String description;
    private String package_id;
    private String mimetype;

    public CkanResourceMinimized(String format, String name, String url, String description, String package_id, String mimetype) {
        this.format = format;
        this.name = name;
        this.url = url;
        this.description = description;
        this.package_id = package_id;
        this.mimetype = mimetype;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }
}
