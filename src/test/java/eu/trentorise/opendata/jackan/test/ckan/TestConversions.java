package eu.trentorise.opendata.jackan.test.ckan;

import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.jackan.dcat.DcatFactory;
import java.util.Locale;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class TestConversions {
    @Test
    public void testResourceToDcat(){
        DcatFactory.distribution(new CkanResource(), "some-catalog", "", "", Locale.ROOT);
    }
    
    @Test
    public void testDatasetToDcat(){
        DcatFactory.dataset(new CkanDataset(), "some-catalog", Locale.ROOT);
    }
}
