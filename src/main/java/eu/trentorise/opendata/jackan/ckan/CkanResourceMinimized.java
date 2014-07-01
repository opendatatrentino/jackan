package eu.trentorise.opendata.jackan.ckan;

/**
 * Created by Ivan Tankoyeu on 23/05/2014.
 */
public class CkanResourceMinimized {

    private String format;
    private String name;
    private String url;
    private String description;
    private String packageId;
    private String mimetype;
    private String id;

    public CkanResourceMinimized(String format, String name, String url, String description, String packageId, String mimetype) {
        this.format = format;
        this.name = name;
        this.url = url;
        this.description = description;
        this.packageId = packageId;
        this.mimetype = mimetype;

    }

    public CkanResourceMinimized(String format, String name, String url, String description, String packageId, String mimetype, String resourceId) {
        this.format = format;
        this.name = name;
        this.url = url;
        this.description = description;
        this.packageId = packageId;
        this.mimetype = mimetype;
        this.id = resourceId;
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

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {return id;}

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }


}
