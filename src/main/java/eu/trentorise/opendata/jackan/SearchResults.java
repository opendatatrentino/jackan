/**
* *****************************************************************************
* Copyright 2013-2014 Trento Rise (www.trentorise.eu/)
*
* All rights reserved. This program and the accompanying materials are made
* available under the terms of the GNU Lesser General Public License (LGPL)
* version 2.1 which accompanies this distribution, and is available at
*
* http://www.gnu.org/licenses/lgpl-2.1.html
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*
*******************************************************************************
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
