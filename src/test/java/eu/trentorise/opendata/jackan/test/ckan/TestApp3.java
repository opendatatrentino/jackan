/*
 * Copyright 2015 Trento Rise.
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
import eu.trentorise.opendata.jackan.CkanQuery;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import java.util.List;

/**
 * Show searching datasets 
 * @since 0.4.1
 */
public class TestApp3 {

    public static void main(String[] args) {

        CkanClient cc = new CkanClient("http://dati.trentino.it");        

        CkanQuery query = CkanQuery.filter().byGroupNames("turismo").byTagNames("ristoranti");

        List<CkanDataset> filteredDatasets = cc.searchDatasets(query, 10, 0).getResults();

        for (CkanDataset d : filteredDatasets) {
            System.out.println();
            System.out.println("DATASET: " + d.getName());
        }
    }
}
