package eu.trentorise.opendata.jackan.test.ckan;

import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class TestConversions {
    @Test
    public void testResourceToDcat(){
        new CkanResource().toDcatDistribution("some-catalog", "", "");
    }
    
    @Test
    public void testDatasetToDcat(){
        new CkanDataset().ToDcatDataset("some-catalog", "");
    }
}
