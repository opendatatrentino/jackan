/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    public SearchResults(List<T> results, int count) {
        this.count = count;
        this.results = results;
    }

    private SearchResults(){
    }

    public int getCount() {
        return count;
    }

    public List<T> getResults() {
        return results;
    }
    
    
}
