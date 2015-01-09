package eu.trentorise.opendata.jackan.test.ckan;

import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class TestResult {
    private String testName;
    @Nullable 
    private Throwable error;
    String catalogName;
    String catalogURL;

    public TestResult(String testName, String catalogName, String catalogURL, @Nullable Throwable error) {
        this.testName = testName;
        this.error = error;
        this.catalogName = catalogName;
        this.catalogURL = catalogURL;
    }

    public String getTestName() {
        return testName;
    }

    boolean passed(){
        return error == null;
    }
    
    public Throwable getError() {
        if (passed()){
            throw new RuntimeException("Test passed!");
        }
        return error;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getCatalogURL() {
        return catalogURL;
    }
    
    
    
    
}
