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
package eu.trentorise.opendata.jackan;

import java.util.List;
import javax.annotation.concurrent.Immutable;

/**
 *
 * @author David Leoni
 * @param <T> the type of the results
 */
@Immutable
public class SearchResults<T> {

    private int count;
    private List<T> results;

    /**
     * @param count The number of matches on the server, which may be greater
     * than the search results.
     */
    public SearchResults(List<T> results, int count) {
        this.count = count;
        this.results = results;
    }

    private SearchResults() {
    }

    /**
     * Returns the number of matches on the server, which may be greater than
     * the search results.
     */
    public int getCount() {
        return count;
    }

    public List<T> getResults() {
        return results;
    }

}
