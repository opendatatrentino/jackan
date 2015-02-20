/* 
 * Copyright 2015 Trento RISE (trentorise.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
