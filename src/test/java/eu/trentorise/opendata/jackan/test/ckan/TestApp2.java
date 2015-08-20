/* 
 * Copyright 2015 Trento Rise  (trentorise.eu) 
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

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;
import java.util.List;

/**
 * GETTING FIRST 10 DATASETS, SHOWING RESOURCES
 * @author David Leoni 
 * @since 0.4.1
 */
public class TestApp2 {

    public static void main(String[] args) {
                   
        CkanClient cc = new CkanClient("http://dati.trentino.it");
       
        List<String> ds = cc.getDatasetList(10, 0);

        for (String s : ds) {
            System.out.println();
            System.out.println("DATASET: " + s);
            CkanDataset d = cc.getDataset(s);
            System.out.println("  RESOURCES:");
            for (CkanResource r : d.getResources()) {
                System.out.println("    " + r.getName());
                System.out.println("    FORMAT: " + r.getFormat());
                System.out.println("       URL: " + r.getUrl());
            }
        }

    }

}
