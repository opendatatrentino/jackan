package eu.trentorise.opendata.jackan.test.ckan;

import com.google.common.base.Optional;
import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class TestResult {
    private String testName;
    
    private Optional<Throwable> error;
    private String catalogName;
    private String catalogURL;
    private int id;

    /**
     * 
     * @param id The unique identifier of the test result
     * @param error The throwable, if an erorr actually occurred. 
     */
    public TestResult(int id, String testName,  String catalogURL, String catalogName, Optional<Throwable> error) {
        this.id = id;
        this.testName = testName;
        this.error = error;
        this.catalogName = catalogName;
        this.catalogURL = catalogURL;
    }

    public String getTestName() {
        return testName;
    }

    boolean passed(){
        return !error.isPresent();
    }
    
    public Throwable getError() {
        if (passed()){
            throw new RuntimeException("Test passed!");
        }
        return error.get();
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getCatalogURL() {
        return catalogURL;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TestResult{" + "testName=" + testName + ", error=" + error + ", catalogName=" + catalogName + ", catalogURL=" + catalogURL + ", id=" + id + '}';
    }


    
    
        
}
