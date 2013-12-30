/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.trentorise.opendata.jackan;

import java.util.ArrayList;
import javax.annotation.concurrent.Immutable;

/**
 *
 * @author David Leoni
 * @param <T> the type of the results
 */
@Immutable
public class SearchResults<T> {
    private final int count;
    private final ArrayList<T> results;

    /**
     *
     * @param results
     * @param count
     */
    public SearchResults(ArrayList<T> results, int count) {
        this.count = count;
        this.results = results;
    }
    
    
}
