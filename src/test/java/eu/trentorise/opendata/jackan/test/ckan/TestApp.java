package eu.trentorise.opendata.jackan.test.ckan;

import eu.trentorise.opendata.jackan.SearchResults;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanQuery;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import java.util.List;

public class TestApp 
{
          
    public static void main( String[] args )
    {
        
        CkanClient cc = new CkanClient("http://dati.trentino.it");

        
        System.out.println("*************************    GETTING FIRST 10 DATASETS, SHOWING RESOURCES   *************************");        
        System.out.println();
        System.out.println();
        List<String> ds = cc.getDatasetList(10, 0);
        
        for (String s : ds){
            System.out.println();
            System.out.println("DATASET: " + s);
            CkanDataset d = cc.getDataset(s);            
            System.out.println("  RESOURCES:");
            for (CkanResource r : d.getResources()){                
                System.out.println("    " + r.getName());
                System.out.println("    FORMAT: " + r.getFormat());
                System.out.println("       URL: " + r.getUrl());
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("*************************    SEARCHING DATASETS   *************************");
        System.out.println();
        System.out.println();
        
        CkanQuery query = CkanQuery.filter().byTagNames("settori economici", "agricoltura").byGroupNames("conoscenza");
        
        List<CkanDataset> filteredDatasets = cc.searchDatasets(query, 10, 0).getResults();
        
        for (CkanDataset d : filteredDatasets){
            System.out.println();
            System.out.println("DATASET: " + d.getName());           
        }                
        
    }
    
  
    
}
